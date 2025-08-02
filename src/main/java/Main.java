
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
                    PatternType groupType = getCharacterGroupType(pattern);
                    if (groupType == PatternType.POSITIVE_GROUP) {
                        return inputLine.matches(".*[" + pattern.substring(1, pattern.length() - 1) + "].*");
                    } else if (groupType == PatternType.NEGATIVE_GROUP) {
                        return inputLine.matches(".*[^" + pattern.substring(2, pattern.length() - 1) + "].*");
                    }
                    return false;
                } else {
                    throw new RuntimeException("Unhandled pattern: " + pattern);
                }
            }
        }
    }

    public static PatternType getCharacterGroupType(String pattern) {
        if (pattern == null || pattern.length() < 3) {
            return null;
        }

        if (pattern.startsWith("[") && pattern.endsWith("]")) {
            if (pattern.charAt(1) == '^') {
                return PatternType.NEGATIVE_GROUP;
            } else {
                return PatternType.POSITIVE_GROUP;
            }
        }
        return null;
    }

    public static int isPositiveCharacterGroup(String pattern) {
        PatternType type = getCharacterGroupType(pattern);
        return (type == PatternType.POSITIVE_GROUP) ? 0 : 1;
    }
}
