package utilities;

import java.util.Scanner;

public class InputValidator {
    private static final Scanner scanner = new Scanner(System.in);

    public static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) throw new Exception("Input cannot be empty!");
                
                int value = Integer.parseInt(input);
                if (value < min || value > max) {
                    System.out.println("[WARNING] Please enter a number between " + min + " and " + max + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Invalid input. Please enter a whole number.");
            } catch (Exception e) {
                System.out.println("[WARNING] " + e.getMessage());
            }
        }
    }

    public static double readDouble(String prompt, double min) {
        while (true) {
            System.out.print(prompt);
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) throw new Exception("Input cannot be empty!");
                
                double value = Double.parseDouble(input);
                if (value < min) {
                    System.out.println("[WARNING] Value cannot be less than " + min + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Invalid input. Please enter a decimal number (e.g., 99.99).");
            } catch (Exception e) {
                System.out.println("[WARNING] " + e.getMessage());
            }
        }
    }

    public static String readString(String prompt, boolean allowEmpty) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!allowEmpty && input.isEmpty()) {
                System.out.println("[WARNING] Input cannot be empty! Please try again.");
                continue;
            }
            return input;
        }
    }

    public static boolean confirm(String prompt) {
        while (true) {
            System.out.print(prompt + " (Y/N): ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("Y")) return true;
            if (input.equals("N")) return false;
            System.out.println("[WARNING] Please enter 'Y' for yes or 'N' for no.");
        }
    }
}
