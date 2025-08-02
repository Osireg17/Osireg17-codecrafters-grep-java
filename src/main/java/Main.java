
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        if (args.length != 2 || !args[0].equals("-E")) {
            System.out.println("Usage: ./your_program.sh -E <pattern>");
            System.exit(1);
        }

        String pattern = args[1];
        Scanner scanner = new Scanner(System.in);
        String inputLine = scanner.nextLine();
        List<Character> inputChars = stringToCharList(inputLine);

        System.err.println("Logs from your program will appear here!");

        if (matchPattern(inputChars, pattern)) {
            System.exit(0);
        } else {
            System.exit(1);
        }
    }

    public static List<Character> stringToCharList(String str) {
        List<Character> chars = new ArrayList<>();
        for (char c : str.toCharArray()) {
            chars.add(c);
        }
        return chars;
    }

    public static boolean matchPattern(List<Character> inputChars, String pattern) {
        // Sliding window approach - check each position
        for (int i = 0; i < inputChars.size(); i++) {
            if (matchPatternAtPosition(inputChars, i, pattern)) {
                return true;
            }
        }
        return false;
    }

    public static boolean matchPatternAtPosition(List<Character> inputChars, int position, String pattern) {
        if (position >= inputChars.size()) {
            return false;
        }
        return matchCharacterWithRegex(inputChars.get(position), pattern);
    }
    
    public static boolean matchCharacterWithRegex(char ch, String pattern) {
        String charStr = String.valueOf(ch);
        
        try {
            // Convert our pattern to Java regex pattern
            String regexPattern = convertToJavaRegex(pattern);
            Pattern compiledPattern = Pattern.compile(regexPattern);
            Matcher matcher = compiledPattern.matcher(charStr);
            return matcher.matches();
        } catch (Exception e) {
            // Fallback to original logic for unhandled patterns
            return matchCharacterFallback(ch, pattern);
        }
    }
    
    public static String convertToJavaRegex(String pattern) {
        switch (pattern) {
            case "\\d" -> {
                return "\\d";
            }
            case "\\w" -> {
                return "\\w";
            }
            default -> {
                if (pattern.length() == 1) {
                    return Pattern.quote(pattern);
                } else if (pattern.startsWith("[") && pattern.endsWith("]")) {
                    return pattern; // Character classes are already valid regex
                } else {
                    throw new RuntimeException("Unhandled pattern: " + pattern);
                }
            }
        }
    }
    
    public static boolean matchCharacterFallback(char ch, String pattern) {
        switch (pattern) {
            case "\\d" -> {
                return Character.isDigit(ch);
            }
            case "\\w" -> {
                return Character.isLetterOrDigit(ch) || ch == '_';
            }
            default -> {
                if (pattern.length() == 1) {
                    return ch == pattern.charAt(0);
                } else if (pattern.startsWith("[") && pattern.endsWith("]")) {
                    PatternType groupType = getCharacterGroupType(pattern);
                    if (groupType == PatternType.POSITIVE_GROUP) {
                        String chars = pattern.substring(1, pattern.length() - 1);
                        return chars.indexOf(ch) != -1;
                    } else if (groupType == PatternType.NEGATIVE_GROUP) {
                        String chars = pattern.substring(2, pattern.length() - 1);
                        return chars.indexOf(ch) == -1;
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
