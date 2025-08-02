
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

        if (match(pattern, inputLine)) {
            System.exit(0);
        } else {
            System.exit(1);
        }
    }

    // C-style regex matcher based on codecrafters example
    // match: search for regexp anywhere in text
    public static boolean match(String regexp, String text) {
        if (regexp.length() > 0 && regexp.charAt(0) == '^') {
            return matchHere(regexp.substring(1), text, 0);
        }
        
        // For complex patterns with escape sequences or character classes, use enhanced matcher
        if (regexp.contains("\\") || regexp.contains("[")) {
            for (int i = 0; i <= text.length(); i++) {
                if (matchPattern(regexp, text, 0, i)) {
                    return true;
                }
            }
            return false;
        }
        
        // Must look even if string is empty (for simple patterns)
        for (int i = 0; i <= text.length(); i++) {
            if (matchHere(regexp, text, i)) {
                return true;
            }
        }
        return false;
    }
    
    // matchHere: search for regexp at beginning of text (starting at position)
    public static boolean matchHere(String regexp, String text, int textPos) {
        // If pattern is empty, we've matched successfully
        if (regexp.length() == 0) {
            return true;
        }
        
        // Handle star quantifier (c*)
        if (regexp.length() > 1 && regexp.charAt(1) == '*') {
            return matchStar(regexp.charAt(0), regexp.substring(2), text, textPos);
        }
        
        // Handle end anchor ($)
        if (regexp.length() == 1 && regexp.charAt(0) == '$') {
            return textPos == text.length();
        }
        
        // Check if current character matches and recurse
        if (textPos < text.length() && matchCharacter(regexp.charAt(0), text.charAt(textPos))) {
            return matchHere(regexp.substring(1), text, textPos + 1);
        }
        
        return false;
    }
    
    // matchStar: search for c*regexp at beginning of text
    public static boolean matchStar(char c, String regexp, String text, int textPos) {
        // Try matching zero occurrences first
        if (matchHere(regexp, text, textPos)) {
            return true;
        }
        
        // Try matching one or more occurrences
        while (textPos < text.length() && matchCharacter(c, text.charAt(textPos))) {
            textPos++;
            if (matchHere(regexp, text, textPos)) {
                return true;
            }
        }
        
        return false;
    }
    
    // matchCharacter: check if pattern character matches text character
    public static boolean matchCharacter(char patternChar, char textChar) {
        // Handle wildcard
        if (patternChar == '.') {
            return true; // . matches any character
        }
        
        // For literal character matching
        return patternChar == textChar;
    }
    
    // Enhanced matcher for handling escape sequences like \d, \w
    public static boolean matchPattern(String pattern, String text, int patternPos, int textPos) {
        if (patternPos >= pattern.length()) {
            return true; // Pattern fully consumed
        }
        
        if (textPos >= text.length()) {
            return false; // Text consumed but pattern remains
        }
        
        char currentChar = pattern.charAt(patternPos);
        
        // Handle escape sequences
        if (currentChar == '\\' && patternPos + 1 < pattern.length()) {
            char nextChar = pattern.charAt(patternPos + 1);
            switch (nextChar) {
                case 'd':
                    return Character.isDigit(text.charAt(textPos)) && 
                           matchPattern(pattern, text, patternPos + 2, textPos + 1);
                case 'w':
                    return (Character.isLetterOrDigit(text.charAt(textPos)) || text.charAt(textPos) == '_') && 
                           matchPattern(pattern, text, patternPos + 2, textPos + 1);
                default:
                    return text.charAt(textPos) == nextChar && 
                           matchPattern(pattern, text, patternPos + 2, textPos + 1);
            }
        }
        
        // Handle character classes [abc] and [^abc]
        if (currentChar == '[') {
            int closeBracket = pattern.indexOf(']', patternPos);
            if (closeBracket != -1) {
                String charClass = pattern.substring(patternPos, closeBracket + 1);
                boolean matches = matchCharacterClass(charClass, text.charAt(textPos));
                return matches && matchPattern(pattern, text, closeBracket + 1, textPos + 1);
            }
        }
        
        // Handle literal characters
        return matchCharacter(currentChar, text.charAt(textPos)) && 
               matchPattern(pattern, text, patternPos + 1, textPos + 1);
    }
    
    // Handle character classes like [abc] or [^abc]
    public static boolean matchCharacterClass(String charClass, char textChar) {
        if (!charClass.startsWith("[") || !charClass.endsWith("]")) {
            return false;
        }
        
        String chars = charClass.substring(1, charClass.length() - 1);
        boolean isNegative = chars.startsWith("^");
        
        if (isNegative) {
            chars = chars.substring(1);
            return chars.indexOf(textChar) == -1;
        } else {
            return chars.indexOf(textChar) != -1;
        }
    }

}
