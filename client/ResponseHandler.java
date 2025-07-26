package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class ResponseHandler extends Thread {
	
    private BufferedReader reader;
    private BlockingQueue<String> messageQueue;

    public ResponseHandler(BufferedReader reader, BlockingQueue<String> messageQueue) {
        this.reader = reader;
        this.messageQueue = messageQueue;
    }

    @Override
    public void run() {
        try {
            String responseString = null;
            while ((responseString = reader.readLine()) != null) {
                messageQueue.put(responseString);
            }
        } catch (IOException e) {
            System.out.println("Error reading response");
        } catch (InterruptedException e) {
            System.out.println("Response reading thread was interrupted.");
            Thread.currentThread().interrupt();
        }
    }
}