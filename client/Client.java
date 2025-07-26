package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.swing.SwingUtilities;

public class Client extends Thread {
	private static String serverAddress = "localhost";
	private static int serverPort = 6666;
    public BlockingQueue<String> requestQueue = new ArrayBlockingQueue<>(1);
	private BlockingQueue<String> responseQueue = new ArrayBlockingQueue<>(2);
	
	public Client(String inputAddress, int inputPort) {
		this.serverAddress = inputAddress;
		this.serverPort = inputPort;
	}

	public static void main(String[] args) {
		if (!checkArguments(args)) {
			System.exit(1);
		}
		
		final Client clientObj = new Client(serverAddress, serverPort);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientUI clientUI = new ClientUI(clientObj);
					clientUI.getFrame().setVisible(true);
					System.out.println("Client initialized");
				} catch(Exception e) {
					System.out.println("Client failed to initialize");
					System.exit(1);
				}
			}
		});
	}


	@Override
	public void run() {
		runClient();
	}


	public void runClient() {
		try {
			Socket clientSocket = new Socket(serverAddress, serverPort);
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
			System.out.println("Dictionary Server Connected!");
			
			ResponseHandler responseHandler = new ResponseHandler(reader, responseQueue);
			responseHandler.start();
			
			String requestString;
			while((requestString = requestQueue.take()) != null) {
				writer.write(requestString + "\n");
				writer.flush();
			}
		} catch(Exception e) {
			System.out.println("Disconnected from dictionary server");
		}
	}
	
	public String getResponse() {
	    try {
	        return responseQueue.take();
	    } catch (InterruptedException e) {
	        System.out.println("Response reading was interrupted");
	        Thread.currentThread().interrupt();
	        return null;
	    }
	}

	public static boolean isValidAddress(String address) {
		return address.equals("localhost") || address.matches(
				"^((25[0-5]|2[0-4]\\d|1?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|1?\\d\\d?)$"
		);
	}

	private static boolean checkArguments(String[] args) {
		if (args.length == 2) {
			String inputAddress = args[0];
			String portStr = args[1];

			if (!isValidAddress(inputAddress)) {
				System.out.println(" Invalid IP address.");
				return false;
			}

			try {
				int inputPort = Integer.parseInt(portStr);
				if (inputPort < 1024 || inputPort > 65535) {
					System.out.println(" Port must be between 1024 and 65535.");
					return false;
				}

				serverAddress = inputAddress;
				serverPort = inputPort;
			} catch (NumberFormatException e) {
				System.out.println(" Port must be an integer.");
				return false;
			}

			return true;
		} else if (args.length == 0) {
			System.out.println("No arguments provided, using default: " + serverAddress + ":" + serverPort);
			return true;
		} else {
			System.out.println("Invalid number of arguments. Expected: <IP> <port>");
			return false;
		}
	}

}


