
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        if (args.length != 2 || !args[0].equals("-E")) {
            System.out.println("Usage: ./your_program.sh -E <pattern>");
            System.exit(1);
        }

        String pattern = args[1];
        Scanner scanner = new Scanner(System.in);
        String inputLine = scanner.nextLine();

        System.err.println("Logs from your program will appear here!");

        if (matchPattern(inputLine, pattern)) {
            System.exit(0);
        } else {
            System.exit(1);
        }
    }

    public static boolean matchPattern(String inputLine, String pattern) {
        switch (pattern) {
            case "\\d" -> {
                return inputLine.matches(".*\\d.*");
            }
            case "\\w" -> {
                return inputLine.matches(".*\\w.*");
            }
            default -> {
                if (pattern.length() == 1) {
                    return inputLine.contains(pattern);
                } else if (pattern.startsWith("[") && pattern.endsWith("]")) {
                    if (isPositiveCharacterGroup(pattern) == 0) {
                        return inputLine.matches(".*[" + pattern.substring(1, pattern.length() - 1) + "].*");
                    }
                    return false;
                } else {
                    throw new RuntimeException("Unhandled pattern: " + pattern);
                }
            }
        }
    }

    public static int isPositiveCharacterGroup(String pattern) {
        if (pattern == null || pattern.length() < 3) {
            return 1;
        }

        boolean isPositiveGroup = pattern.startsWith("[")
                && pattern.endsWith("]")
                && pattern.charAt(1) != '^';

        return isPositiveGroup ? 0 : 1;
    }
}
