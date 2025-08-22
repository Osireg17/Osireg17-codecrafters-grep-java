
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        if (args.length < 2 || !args[0].equals("-E")) {
            System.out.println("Usage: ./your_program.sh -E <pattern> [file]");
            System.exit(1);
        }

        StringBuilder patternBuilder = new StringBuilder();
        int firstFileIdx = -1;
        for (int i = 1; i < args.length; i++) {
            if (args[i].endsWith(".txt") || args[i].endsWith(".log")) {
                firstFileIdx = i;
                break;
            }
            if (i > 1) {
                patternBuilder.append(" ");
            }
            patternBuilder.append(args[i]);
        }
        String pattern = patternBuilder.toString();

        boolean found = false;

        if (firstFileIdx != -1) {
            for (int i = firstFileIdx; i < args.length; i++) {
                String filename = args[i];
                try (Scanner fileScanner = new Scanner(new File(filename))) {
                    while (fileScanner.hasNextLine()) {
                        String line = fileScanner.nextLine();
                        if (matchPattern(line, pattern)) {
                            if (args.length - firstFileIdx > 1) {
                                System.out.println(filename + ":" + line);
                            } else {
                                System.out.println(line);
                            }
                            found = true;
                        }
                    }
                } catch (FileNotFoundException e) {
                    System.err.println("File not found: " + filename);
                    System.exit(1);
                }
            }
            System.exit(found ? 0 : 1);
        } else {
            try (
                    Scanner scanner = new Scanner(System.in)) {
                String inputLine = scanner.nextLine();
                if (matchPattern(inputLine, pattern)) {
                    System.out.println(inputLine);
                    System.exit(0);
                } else {
                    System.exit(1);
                }
            }
        }
    }

    public static boolean matchPattern(String inputLine, String pattern) {
        if (pattern.contains("(") || pattern.contains(")")) {
            return inputLine.matches(pattern);
        }
        String[] patterns = pattern.split(" ");
        for (String p : patterns) {
            String regex;
            if (p.equals("\\d")) {
                regex = ".*\\d.*";
            } else if (p.equals("\\w")) {
                regex = ".*\\w.*";
            } else if (p.matches("(\\\\d|\\\\w)+s")) {
                String regexPattern
                        = p.replaceAll("\\\\d", "\\\\d").replaceAll("\\\\w", "\\\\w") + "\\b";
                regex = ".*" + regexPattern + ".*";
            } else if (p.matches("(\\\\d)+")) {
                regex = ".*" + p.replaceAll("\\\\d", "\\\\d") + ".*";
            } else if (p.matches("(\\\\w)+")) {
                regex = ".*" + p.replaceAll("\\\\w", "\\\\w") + ".*";
            } else if (p.matches("\\[\\^.+\\]")) {
                regex = ".*" + p + ".*";
            } else if (p.matches("\\[.+\\]")) {
                regex = ".*" + p + ".*";
            } else if (p.startsWith("^")) {
                regex = "^" + p.substring(1) + ".*";
            } else if (p.endsWith("$")) {
                regex = p.substring(0, p.length() - 1) + "$";
            } else if (p.contains("+") || p.contains("?") || p.contains(".") || p.contains("|")) {
                regex = ".*" + p + ".*";
            } else if (p.contains("(") || p.contains(")")) {
                regex = p;
            } else {
                regex = ".*" + java.util.regex.Pattern.quote(p) + ".*";
            }
            if (!inputLine.matches(regex)) {
                return false;
            }
        }
        return true;
    }
}
