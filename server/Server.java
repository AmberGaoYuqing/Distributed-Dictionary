package server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
	
	private static Server server;
	public static ConcurrentHashMap<String, String> dictionary = new ConcurrentHashMap<>();
	private static int clientIDCounter = 1;
	public static int clientCount = 0;
	public static ConcurrentHashMap<Socket, String> userMap = new ConcurrentHashMap<>();
	private DictionaryManager dictionaryManager;

	ServerSocket serverSocket = null;
	static int port = 6666;
	static String dictionaryFile = "src/dictionary.txt";

	public static void main(String[] args) {
		if (!checkArguments(args)){
			System.exit(1);//
		}

		ServerGUI serverGUI = new ServerGUI();
		serverGUI.start();
		
		server = new Server();
		server.dictionaryManager = new DictionaryManager(dictionaryFile, dictionary);
		server.dictionaryManager.loadDictionary();
		
		try {
			Thread.sleep(500);
		} catch(InterruptedException e) {
			System.out.println(e);
		}
		server.dictionaryManager.updateDisplay();
		
		server.runServer();
	}
	
	public void runServer() {
		try {
			serverSocket = new ServerSocket(port);
		} catch(Exception e) {
			System.out.println("Server already running on port " + port + "!");
		}
		
		Socket clientSocket = null;
		
		while(true) {
			try {
				clientSocket = serverSocket.accept();
				clientCount++;
				String ip = clientSocket.getInetAddress().getHostAddress();
				String clientName = "Client-" + clientIDCounter++ + " [" + ip + "]";
				userMap.put(clientSocket, clientName);
				ServerGUI.userListModel.addElement(clientName);

				ServerGUI.textField.setText(Integer.toString(clientCount));
				RequestHandler requestHandler = new RequestHandler(clientSocket, dictionaryManager);
				requestHandler.start();
			} catch(SocketException e) {
				break;
			} catch(IOException e) {
				break;
			}
		} try {
			serverSocket.close();
		} catch(IOException e) {
			System.out.println("Server socket close error!");
		}
	}

	private static boolean checkArguments(String[] args) {
		if (args.length == 0) {
			// Use default settings
			System.out.println("No arguments provided. Using default port and dictionary file path.");
			return true;
		} else if (args.length == 2) {
			// Try to parse the port number
			try {
				port = Integer.parseInt(args[0].trim());
				if (port < 1024 || port > 65535) {
					System.out.println("Port number must be between 1024 and 65535!");
					return false;
				}
			} catch (NumberFormatException e) {
				System.out.println(" Port number must be a valid integer!");
				return false;
			}
			dictionaryFile = args[1].trim();
			File file = new File(dictionaryFile);
			if (!file.exists() || !file.isFile()) {
				System.out.println(" Dictionary file does not exist or is not a regular file!");
				return false;
			}
			return true;
		} else {
			System.out.println("Invalid number of arguments. Expected: <port> <dictionary_file_path> or no arguments.");
			return false;
		}
	}

}
