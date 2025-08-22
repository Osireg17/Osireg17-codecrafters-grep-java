
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
        for (int i = 1; i < args.length; i++) {
            if (i > 1 && !(args[i].endsWith(".txt") || args[i].endsWith(".log"))) {
                patternBuilder.append(" ");
            }
            if (!(args[i].endsWith(".txt") || args[i].endsWith(".log"))) {
                patternBuilder.append(args[i]);
            }
        }
        String pattern = patternBuilder.toString();

        boolean found = false;

        String filename = null;
        if (args.length > 2
                && (args[args.length - 1].endsWith(".txt")
                || args[args.length - 1].endsWith(".log"))) {
            filename = args[args.length - 1];
        }

        if (filename != null) {
            try (Scanner fileScanner = new Scanner(new File(filename))) {
                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();
                    if (matchPattern(line, pattern)) {
                        System.out.println(line);
                        found = true;
                    }
                }
            } catch (FileNotFoundException e) {
                System.err.println("File not found: " + filename);
                System.exit(1);
            }
            System.exit(found ? 0 : 1);
        } else {
            // Leer de stdin como antes
            Scanner scanner = new Scanner(System.in);
            String inputLine = scanner.nextLine();
            if (matchPattern(inputLine, pattern)) {
                System.out.println(inputLine);
                System.exit(0);
            } else {
                System.exit(1);
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
