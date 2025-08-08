import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2 || !args[0].equals("-E")) {
            System.out.println("Usage: ./your_program.sh -E <pattern>");
            System.exit(1);
        }

        StringBuilder patternBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            if (i > 1) patternBuilder.append(" ");
            patternBuilder.append(args[i]);
        }
        String pattern = patternBuilder.toString();
        Scanner scanner = new Scanner(System.in);
        String inputLine = scanner.nextLine();

        // You can use print statements as follows for debugging, they'll be visible when running
        // tests.
        System.err.println("Logs from your program will appear here!");

        // Uncomment this block to pass the first stage

        if (matchPattern(inputLine, pattern)) {
            System.exit(0);
        } else {
            System.exit(1);
        }
    }

    public static boolean matchPattern(String inputLine, String pattern) {
        // Si el patrón contiene paréntesis, evalúa el patrón completo como regex
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
                String regexPattern =
                        p.replaceAll("\\\\d", "\\\\d").replaceAll("\\\\w", "\\\\w") + "\\b";
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
            if (!inputLine.matches(regex)) return false;
        }
        return true;
    }
}
