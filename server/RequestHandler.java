package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class RequestHandler extends Thread {

	Socket clientSocket = null;
	BufferedReader reader;
    BufferedWriter writer;
    private DictionaryManager dictionaryManager;


    public RequestHandler(Socket socket, DictionaryManager dictionaryManager) {
		this.clientSocket = socket;
        this.dictionaryManager = dictionaryManager;

        try {
			writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
			reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
		} catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	

    public void run() {
        String request = null;
        JSONParser parser = new JSONParser();
        try {
            while ((request = reader.readLine()) != null) {
                System.out.println("Received a request:" + request);
                try {

                    JSONObject jsonRequest = (JSONObject) parser.parse(request);
                    String command = (String) jsonRequest.get("command");

                    if (command == null) {
                        System.out.println("Command is null");
                        continue;
                    }

                    switch (command) {
                        case "ADD": handleAdd(jsonRequest); break;
                        case "QUERY": handleQuery(jsonRequest); break;
                        case "DELETE": handleDelete(jsonRequest); break;
                        case "ADD_MEANING": handleAddMeaning(jsonRequest); break;
                        case "UPDATE": handleUpdate(jsonRequest); break;
                        default: sendErrorResponse("Unsupported command: " + command); break;
                    }

                    dictionaryManager.saveDictionary();
                    System.out.println(" Current dictionary:" + Server.dictionary.toString());

                    dictionaryManager.updateDisplay();
                    System.out.println("Saved to dictionary.");
                } catch (ParseException pe) {
                    System.out.println("Failed to parse JSON: " + pe.getMessage());
                    writer.write("Invalid json format\n");
                    writer.flush();
                }
            }
        } catch (IOException e) {
            System.out.println("Client disconnected!");
            Server.clientCount--;
            ServerGUI.textField.setText(Integer.toString(Server.clientCount));
            String name = Server.userMap.get(clientSocket); // s 是 Socket
            if (name != null) {
                ServerGUI.userListModel.removeElement(name);
                Server.userMap.remove(clientSocket);
            }
        } catch (Exception e) {
            System.out.println("Client disconnected!");
            Server.clientCount--;
            ServerGUI.textField.setText(Integer.toString(Server.clientCount));
            String name = Server.userMap.get(clientSocket);
            if (name != null) {
                ServerGUI.userListModel.removeElement(name);
                Server.userMap.remove(clientSocket);
            }
        }
    }
    private void handleAdd(JSONObject jsonRequest) throws IOException {
        String word = (String) jsonRequest.get("word");
        JSONArray meaningsArray = (JSONArray) jsonRequest.get("meanings");

        if (word == null || meaningsArray == null) {
            sendErrorResponse("Please provide both the word and its meaning(s).");
            return;
        }

        Set<String> meaningSet = new HashSet<>();
        for (Object obj : meaningsArray) {
            String m = ((String) obj).trim();
            if (!m.isEmpty()) meaningSet.add(m);
        }

        String combinedMeanings = String.join(";", meaningSet);
        String response;

        if (Server.dictionary.containsKey(word)) {
            response = createJsonResponse("1", "This word already exists.");
        } else {
            Server.dictionary.put(word, combinedMeanings);
            response = createJsonResponse("0", " Word added successfully.");
        }

        sendResponse(response);
        log("Added word '" + word + "'");
    }

    private void handleDelete(JSONObject jsonRequest) throws IOException {
        String word = (String) jsonRequest.get("word");

        if (word == null || word.trim().isEmpty()) {
            sendErrorResponse("Please input a word to delete.");
            return;
        }

        String response;
        if (!Server.dictionary.containsKey(word)) {
            response = createJsonResponse("1", " Word not found in dictionary.");
        } else {
            Server.dictionary.remove(word);
            response = createJsonResponse("0", " Word deleted successfully.");
        }

        sendResponse(response);
        log("Deleted word '" + word + "'");
    }

    private void handleQuery(JSONObject jsonRequest) throws IOException {
        String word = (String) jsonRequest.get("word");

        if (word == null || word.trim().isEmpty()) {
            sendErrorResponse("Please input a word to query.");
            return;
        }

        String response;
        if (!Server.dictionary.containsKey(word)) {
            response = createJsonResponse("1", "Word not found in dictionary.");
        } else {
            String meanings = Server.dictionary.get(word);
            response = createJsonResponse("0", meanings);
        }

        sendResponse(response);
        log("Queried word '" + word + "'");
    }

    private void handleAddMeaning(JSONObject jsonRequest) throws IOException {
        String word = (String) jsonRequest.get("word");
        JSONArray meaningsArray = (JSONArray) jsonRequest.get("meanings");

        if (word == null || meaningsArray == null || word.trim().isEmpty()) {
            sendErrorResponse("Please provide both the word and new meaning(s).");
            return;
        }

        if (!Server.dictionary.containsKey(word)) {
            sendErrorResponse(" Word not found in dictionary.");
            return;
        }

        String existingMeaningStr = Server.dictionary.get(word);
        Set<String> currentMeanings = new HashSet<>();
        for (String m : existingMeaningStr.split(";")) {
            currentMeanings.add(m.trim());
        }

        Set<String> addedMeanings = new HashSet<>();
        for (Object obj : meaningsArray) {
            String m = ((String) obj).trim();
            if (!m.isEmpty() && !currentMeanings.contains(m)) {
                currentMeanings.add(m);
                addedMeanings.add(m);
            }
        }

        String response;
        if (addedMeanings.isEmpty()) {
            response = createJsonResponse("1", "All provided meanings already exist.");
        } else {
            String updated = String.join(";", currentMeanings);
            Server.dictionary.put(word, updated);
            response = createJsonResponse("0", "Meanings added: " + String.join(", ", addedMeanings));
        }

        sendResponse(response);
        log("Added meanings to '" + word + "'");
    }


    private void handleUpdate(JSONObject jsonRequest) throws IOException {
        String word = (String) jsonRequest.get("word");
        String oldMeaning = (String) jsonRequest.get("oldMeaning");
        String newMeaning = (String) jsonRequest.get("newMeaning");

        if (word == null || oldMeaning == null || newMeaning == null ||
                word.trim().isEmpty() || oldMeaning.trim().isEmpty() || newMeaning.trim().isEmpty()) {
            sendErrorResponse("Please fill in the word, old meaning, and new meaning.");
            return;
        }

        if (!Server.dictionary.containsKey(word)) {
            sendErrorResponse(" Word not found in dictionary.");
            return;
        }

        String[] meanings = Server.dictionary.get(word).split(";");
        boolean found = false;

        for (int i = 0; i < meanings.length; i++) {
            if (meanings[i].trim().equals(oldMeaning.trim())) {
                meanings[i] = newMeaning.trim();
                found = true;
                break;
            }
        }

        String response;
        if (!found) {
            response = createJsonResponse("1", " Could not find the old meaning to update.");
        } else {
            Server.dictionary.put(word, String.join(";", meanings));
            response = createJsonResponse("0", "Meaning updated successfully.");
        }

        sendResponse(response);
        log("Updated meaning for word '" + word + "'");
    }

	private String createJsonResponse(String status, String message) {
	    JSONObject jsonResponse = new JSONObject();
	    jsonResponse.put("status", status);
	    jsonResponse.put("message", message);
	    return jsonResponse.toJSONString();
	}

    private void sendResponse(String message) throws IOException {
        writer.write(message + "\n");
        writer.flush();
    }

    private void sendErrorResponse(String errorMsg) throws IOException {
        String errorResponse = createJsonResponse("1", errorMsg);
        sendResponse(errorResponse);
    }

    private void log(String message) {
        String timestamp = java.time.LocalTime.now().toString();
        ServerGUI.logArea.append("[" + timestamp + "] " + message + "\n");
    }

    private String getClientInfo() {
        return clientSocket.getInetAddress().getHostAddress();
    }
	
}
