package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ActionHandler implements ActionListener {
    public enum ActionType {
        ADD, QUERY, DELETE, ADD_MEANING, UPDATE
    }

    private Client client;
    private ActionType actionType;
    private JTextField wordField;
    private JTextField meaningField;
    private JTextField updateMeaningField;
    private JTextArea outputArea;

    private void initFields(ActionType actionType, JTextField wordField, JTextField meaningField,
                            JTextField updateMeaningField, JTextArea outputArea, Client client) {
        this.actionType = actionType;
        this.wordField = wordField;
        this.meaningField = meaningField;
        this.updateMeaningField = updateMeaningField;
        this.outputArea = outputArea;
        this.client = client;
    }

    public ActionHandler(ActionType actionType, JTextField wordField, JTextField meaningField,
                         JTextArea outputArea, Client client) {
        initFields(actionType, wordField, meaningField, null, outputArea, client);
    }

    public ActionHandler(ActionType actionType, JTextField wordField, JTextField meaningField,
                         JTextField updateMeaningField, JTextArea outputArea, Client client) {
        initFields(actionType, wordField, meaningField, updateMeaningField, outputArea, client);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (actionType == ActionType.ADD) {
            handleAdd();
        } else if (actionType == ActionType.QUERY) {
            handleQuery();
        } else if (actionType == ActionType.DELETE) {
            handleDelete();
        } else if (actionType == ActionType.ADD_MEANING) {
            handleAddMeaning();
        } else if (actionType == ActionType.UPDATE) {
            handleUpdate();
        }
    }

    private void handleAdd() {
        String word = wordField.getText();
        String meaningInput = meaningField.getText();

        if (word.isEmpty()) {
            outputArea.setText("Please input word");
        } else if (meaningInput.isEmpty()) {
            outputArea.setText("Please input meaning");
        } else {
            try {
                word = sanitizeInput(word);
                meaningInput = sanitizeInput(meaningInput);
                JSONArray meaningsArray = convertToJSONArray(meaningInput);

                JSONObject jsonRequest = new JSONObject();
                jsonRequest.put("command", "ADD");
                jsonRequest.put("word", word.toLowerCase());
                jsonRequest.put("meanings", meaningsArray);

                String responseString = sendRequest(jsonRequest);
                handleResponse(responseString);
            } catch (InterruptedException e) {
                outputArea.setText("Add failed.");
            }
        }

        resetFields();
    }

    private void handleQuery() {
        String word = wordField.getText();

        if (word.isEmpty()) {
            outputArea.setText("Word cannot be empty! Try again.");
        } else {
            try {
                word = sanitizeInput(word);

                JSONObject jsonRequest = new JSONObject();
                jsonRequest.put("command", "QUERY");
                jsonRequest.put("word", word.toLowerCase());

                String responseString = sendRequest(jsonRequest);
                handleResponse(responseString);
            } catch (InterruptedException e) {
                outputArea.setText("Query failed.");
            }
        }

        resetFields();
    }

    private void handleDelete() {
        String word = wordField.getText();

        if (word.isEmpty()) {
            outputArea.setText("Please input word");
        } else {
            try {
                word = sanitizeInput(word);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("command", "DELETE");
                jsonObject.put("word", word.toLowerCase());

                String responseString = sendRequest(jsonObject);
                handleResponse(responseString);
            } catch (InterruptedException e) {
                outputArea.setText("Delete failed.");
            }
        }

        resetFields();
    }

    private void handleAddMeaning() {
        String word = wordField.getText();
        String meaningInput = meaningField.getText();

        if (word.isEmpty()) {
            outputArea.setText("Please input word");
        } else if (meaningInput.isEmpty()) {
            outputArea.setText("Please input meaning");
        } else {
            try {
                word = sanitizeInput(word);
                meaningInput = sanitizeInput(meaningInput);
                JSONArray meaningsArray = convertToJSONArray(meaningInput);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("command", "ADD_MEANING");
                jsonObject.put("word", word.toLowerCase());
                jsonObject.put("meanings", meaningsArray);

                String responseString = sendRequest(jsonObject);
                handleResponse(responseString);
            } catch (InterruptedException e) {
                outputArea.setText("Add meaning failed.");
            }
        }
        resetFields();
    }

    private void handleUpdate() {
        String word = wordField.getText();
        String oldMeaning = meaningField.getText();
        String newMeaning = updateMeaningField != null ? updateMeaningField.getText() : "";

        if (word.isEmpty()) {
            outputArea.setText("Please input word");
        } else if (oldMeaning.isEmpty()) {
            outputArea.setText("Please input old meaning at meaning field");
        } else if (newMeaning.isEmpty()) {
            outputArea.setText("Please input new meaning at update meaning field");
        } else {
            try {
                word = sanitizeInput(word);
                oldMeaning = sanitizeInput(oldMeaning);
                newMeaning = sanitizeInput(newMeaning);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("command", "UPDATE");
                jsonObject.put("word", word.toLowerCase());
                jsonObject.put("oldMeaning", oldMeaning);
                jsonObject.put("newMeaning", newMeaning);

                String responseString = sendRequest(jsonObject);
                handleResponse(responseString);
            } catch (InterruptedException e) {
                outputArea.setText("Update failed.");
            }
        }
        resetFields();
    }

    private String sanitizeInput(String input) {
        return input.replaceAll("#", "--").trim().toLowerCase();
    }

    private JSONArray convertToJSONArray(String input) {
        Set<String> meaningSet = new HashSet<>();
        for (String meaning : input.split(";")) {
            String trimmed = meaning.trim();
            if (!trimmed.isEmpty()) {
                meaningSet.add(trimmed);
            }
        }
        JSONArray array = new JSONArray();
        array.addAll(meaningSet);
        return array;
    }

    private String sendRequest(JSONObject jsonRequest) throws InterruptedException {
        String requestString = jsonRequest.toJSONString();
        while (client.requestQueue.size() == 0) {
            client.requestQueue.put(requestString);
            System.out.println("Sent request: " + requestString);
            break;
        }
        return client.getResponse();
    }

    private void handleResponse(String responseString) {
        if (responseString == null) {
            outputArea.setText("You are disconnected.");
            return;
        }
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonResponse = (JSONObject) parser.parse(responseString);
            String status = (String) jsonResponse.get("status");
            String message = (String) jsonResponse.get("message");

            // If it's a query and status is OK, format message nicely
            if ("0".equals(status) && ActionType.QUERY.equals(actionType)) {
                outputArea.setText(message.replaceAll(";", ";\n"));
            } else {
                outputArea.setText(message);
            }
        } catch (Exception e) {
            outputArea.setText("Failed to parse JSON");
        }
    }

    private void resetFields() {
        wordField.setText("");
        meaningField.setText("");
        if (updateMeaningField != null) {
            updateMeaningField.setText("");
        }
    }



}