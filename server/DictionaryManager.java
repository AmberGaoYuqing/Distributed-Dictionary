package server;

import java.io.*;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class DictionaryManager {

    private final String dictionaryFile;
    private final ConcurrentHashMap<String, String> dictionary;

    public DictionaryManager(String dictionaryFile, ConcurrentHashMap<String, String> dictionary) {
        this.dictionaryFile = dictionaryFile;
        this.dictionary = dictionary;
    }

    public void loadDictionary() {
        try {
            File file = new File(dictionaryFile);
            if (!file.exists() || !file.isFile()|| !file.canRead()) {
                System.out.println(" Dictionary file not found or unreadable:"+ dictionaryFile);
                return;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
                String line;

                while ((line = reader.readLine()) != null) {
                    // Skip empty or malformed lines
                    if (line.trim().isEmpty() || !line.contains(":")) continue;
                    String[] split = line.split(":", 2);
                    if (split.length == 2) {
                        String word = split[0].trim();
                        String meaning = split[1].trim();
                        dictionary.put(word, meaning);
                    }
                }
                System.out.println(" Dictionary loaded successfully.");
            }
        } catch (Exception e) {
            System.out.println("Failed to read dictionary: " + e.getMessage());
        }
    }

    public void saveDictionary() {
        File file = new File(dictionaryFile);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Entry<String, String> entry : dictionary.entrySet()) {
                writer.write(entry.getKey() + " : " + entry.getValue() + "\n");
            }
            System.out.println("Dictionary saved successfully.");
        } catch (IOException e) {
            System.out.println(" Failed to write dictionary: " + e.getMessage());
        }
    }

    public void updateDisplay() {
        StringBuilder content = new StringBuilder();

        dictionary.entrySet().stream()
                .sorted(Entry.comparingByKey())
                .forEach(entry -> content.append(entry.getKey()).append(" : ").append(entry.getValue()).append("\n"));

        ServerGUI.outputArea.setText(content.toString());
    }
}
