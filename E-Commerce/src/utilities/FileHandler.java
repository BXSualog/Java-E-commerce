package utilities;

import java.io.*;
import java.util.*;

public class FileHandler {
    private static final String DATA_DIR = "data/";

    public static void checkDataDir() {
        File dir = new File(DATA_DIR);
        if (!dir.exists())
            dir.mkdirs();
    }

    public static void saveListToCSV(String filename, List<String> lines) {
        checkDataDir();
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_DIR + filename))) {
            for (String line : lines) {
                writer.println(line);
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Error saving to " + filename + ": " + e.getMessage());
        }
    }

    public static void saveTextFile(String filename, String content) {
        checkDataDir();
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_DIR + filename))) {
            writer.print(content);
        } catch (IOException e) {
            System.out.println("[ERROR] Error saving to " + filename + ": " + e.getMessage());
        }
    }

    public static List<String> readCSV(String filename) {
        checkDataDir();
        List<String> lines = new ArrayList<>();
        File file = new File(DATA_DIR + filename);
        if (!file.exists())
            return lines;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty())
                    lines.add(line);
            }
        } catch (FileNotFoundException e) {

        }
        return lines;
    }

    public static void saveReceipt(int orderId, String content) {
        String filename = "receipts/receipt_Order#" + orderId + ".txt";
        new File("receipts").mkdirs();
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println(content);
        } catch (IOException e) {
            System.out.println("[ERROR] Error generating receipt: " + e.getMessage());
        }
    }

    public static List<String> listReceipts() {
        File folder = new File("receipts");
        if (!folder.exists()) return new ArrayList<>();
        
        String[] files = folder.list();
        List<String> receiptList = new ArrayList<>();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].endsWith(".txt")) {
                    receiptList.add(files[i]);
                }
            }
        }
        return receiptList;
    }

    public static String readReceipt(String filename) {
        StringBuilder sb = new StringBuilder();
        try (Scanner scanner = new Scanner(new File("receipts/" + filename))) {
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine()).append("\n");
            }
        } catch (FileNotFoundException e) {
            return "[ERROR] Receipt file not found.";
        }
        return sb.toString();
    }
}
