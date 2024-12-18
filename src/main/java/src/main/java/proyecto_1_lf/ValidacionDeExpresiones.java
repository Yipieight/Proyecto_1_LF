package src.main.java.proyecto_1_lf;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidacionDeExpresiones {

    public int lineNumber = 0;
    public Map<String, String> actionToken = new HashMap<>();


    public int getErrorColumn(String line, Pattern p) {
        line = line.replaceAll("\\s+$", "");
        String subcadena = line;
        Matcher m = p.matcher(line);

        for (int i = 0; i <= line.length(); i++) {
            subcadena = line.substring(0, i);
            Matcher subMatcher = p.matcher(subcadena);
            if (!subMatcher.matches() && !subMatcher.hitEnd()) {
                subcadena = subcadena.substring(0, i - 1);
                long extraColumn = subcadena.chars().filter(c -> c == '\t').count() * 3;
                return i + (int) extraColumn; 
            }
        }

        long extraColumn = subcadena.chars().filter(c -> c == '\t').count() * 3;

        return m.regionEnd() + (int) extraColumn + 1;
    }

    private void logAsciiValues(String line) {
        System.out.print("Debug: ASCII values of line: ");
        for (char c : line.toCharArray()) {
            System.out.print((int) c + " ");
        }
        System.out.println();
    }

    boolean verificarSet(String line) throws Exception {
        System.out.println("Debug: Verifying SETS section.");

        Pattern p = Pattern.compile(
                "\\s+([A-Z_]+)\\s*=\\s*'([A-Za-z0-9_])'\\s*\\.\\.\\s*'([A-Za-z0-9_])'(\\s*\\+\\s*('([A-Za-z0-9_])'|('([A-Za-z0-9_])'\\s*\\.\\.\\s*'([A-Za-z0-9_])')))*\\s*|\\s*([A-Z_]+)\\s*=\\s*CHR\\(((25[0-6])|(2[0-4][0-9])|(1[0-9]{2})|([1-9][0-9]|[1-9]))\\)\\s*\\.\\.\\s*CHR\\(((25[0-6])|(2[0-4][0-9])|(1[0-9]{2})|([1-9][0-9]|[1-9]))\\)\\s*");

        System.out.println("Debug: Reading line " + lineNumber + ": " + line);
        logAsciiValues(line);

        // Clean the line and check against the regular expression
        System.out.println("Debug: Cleaned line: " + line);

        Matcher m = p.matcher(line);
        boolean isMatching = m.matches();
        System.out.println("Debug: Matching result for line " + lineNumber + ": " + isMatching);

        if (!isMatching) {
            int errorColumn = getErrorColumn(line, p);
            System.out.println("Error in line " + lineNumber + " at column " + errorColumn + ": " + line);
            return false;
        } else {
            System.out.println("Debug: Line " + lineNumber + " passed validation.");
        }

        return true;
    }

    boolean verificarTokens(String line) throws Exception {
        System.out.println("Debug: Verifying TOKENS section.");

        // Updated regular expression for TOKEN lines, including support for parentheses
        // and functions
        Pattern p = Pattern.compile(
                "\\s+TOKEN\\s*(\\d+)\\s*=\\s*([A-Za-z0-9_'\"()*+?|\\s-]+|'([^']|'')*'|\"[^\"]*\"|\\([A-Za-z0-9_'\"\\s|*+?]+\\)|\\{\\s*[A-Za-z0-9_]+\\(\\)\\s*\\}|\\(\\s*[A-Za-z0-9_'\"\\s|*+?]+\\s*\\))*");
        System.out.println("Debug: Reading line " + lineNumber + ": " + line);

        Matcher m = p.matcher(line);
        boolean isMatching = m.find();
        System.out.println("Debug: Matching result for line " + lineNumber + ": " + isMatching);

        if (isMatching) {
            // After regex match, check if parentheses/brackets/braces are balanced
            String tokenExpression = line.split("=")[1].trim(); // Get the part after '='
            if (!isBalanced(tokenExpression)) {
                System.out
                        .println("Error in line " + lineNumber + " at column " + getErrorColumn(line, p) + ": " + line);
                return false;
            } else {
                System.out.println("Debug: Line " + lineNumber + " passed validation.");
            }
        } else {
            int errorColumn = getErrorColumn(line, p);
            System.out.println("Error in line " + lineNumber + " at column " + errorColumn + ": " + line);
            return false;
        }
        return true;
    }

    boolean verificarVariables(List<String> lines) {
        return true;
    }

    public boolean haveFuntionReservadas = false;

    boolean verificarActions(BufferedReader reader, String tempLine) throws Exception {
        System.out.println("Debug: Verifying ACTIONS section.");
        String line = "";

        // Pattern to match the RESERVADAS() declaration
        Pattern funcPattern = Pattern.compile("([A-Z]*\\(\\))");
        Pattern reservadasPattern = Pattern.compile("(RESERVADAS\\(\\))");

        // Pattern to match action definitions like 18 = 'PROGRAM'
        Pattern actionPattern = Pattern.compile("\\s+\\d+\\s*=\\s*'\\w+'");

        boolean insideActionBlock = false; 
        boolean readFunction = false; 

        while ((line = (tempLine != null ? tempLine : reader.readLine())) != null) {
            tempLine = null;
            if (line.trim().isEmpty()) {
                lineNumber++;
                continue; 
            }
            if (!readFunction) {
                readFunction = true;
                Matcher reservadasMatcher = reservadasPattern.matcher(line);
                if (reservadasMatcher.find()) {
                    haveFuntionReservadas = true;
                }
                reservadasMatcher = funcPattern.matcher(line);
                if (!reservadasMatcher.find()) {
                    int errorColumn = getErrorColumn(line, reservadasPattern);
                    System.out.println("Error in line " + lineNumber + " at column " + errorColumn + ": " + line);
                    return false;
                }
                if (line.contains("{}")) {
                    return true;
                } else if (line.contains("{")) {
                    insideActionBlock = true;
                }
                System.out.println("Debug: Line " + lineNumber + " passed validation.");
                continue;
            }
            lineNumber++;

            // Check for the opening {
            if (!insideActionBlock && line.trim().equals("{")) {
                insideActionBlock = true;
                System.out.println("Debug: Opening brace found.");
                continue;
            }
            // If inside an action block, validate action definitions
            if (insideActionBlock) {
                if (line.trim().equals("}")) {
                    System.out.println("Debug: Closing brace found. Action block ended.");
                    return true;
                }
                Matcher actionMatcher = actionPattern.matcher(line);
                if (!actionMatcher.matches()) {
                    int errorColumn = getErrorColumn(line, actionPattern);
                    System.out.println("Error in line " + lineNumber + " at column " + errorColumn + ": " + line);
                    return false;
                } else {
                    System.out.println("Debug: Line " + lineNumber + " passed validation.");
                    actionsTokens(line);
                }
            } else {
                System.out.println("Error: Expected opening `{` at line " + lineNumber + " and column: "
                        + getErrorColumn(line, Pattern.compile("\\{")));
                return false;
            }
        }
        if (insideActionBlock) {
            System.out.println("Error: Missing closing `}` for action block.");
            return false;
        }

        return true;
    }

    public void actionsTokens(String line){
        Pattern pattern = Pattern.compile("(\\d+)\\s*=\\s*'([^']*)'");
        Matcher matcher = pattern.matcher(line);
        System.out.println(line);

        if(matcher.find()){
            String numeroToken = matcher.group(1);
            String finalNumeroToken = "T"+numeroToken;
            String contenidoToken = matcher.group(2);
            actionToken.put(contenidoToken, finalNumeroToken);
        }

    }

    public Map<String, String> actionsTokensReturn(){
        return actionToken;
    }


    boolean verificarError(String line) {
        System.out.println("Debug: Verifying ERROR section.");

        Pattern p = Pattern.compile("\\s*ERROR\\s*=\\s*(\\d+)");

        System.out.println("Debug: Reading line " + lineNumber + ": " + line);

        Matcher m = p.matcher(line);
        boolean isMatching = m.matches();
        System.out.println("Debug: Matching result for line " + lineNumber + ": " + isMatching);

        if (!isMatching) {
            System.out.println(
                    "Error in line " + lineNumber + " at column " + getErrorColumn(line, p) + ": " + line);
            return false;
        } else {
            System.out.println("Debug: Line " + lineNumber + " passed validation.");
        }
        return true;
    }

    private boolean isBalanced(String expression) {
        int parentheses = 0;
        int curlyBraces = 0;
        int squareBrackets = 0;
        boolean insideSingleQuotes = false;

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);

            // Toggle insideSingleQuotes flag when encountering a single quote
            if (ch == '\'') {
                insideSingleQuotes = !insideSingleQuotes;
            }

            // Only validate parentheses, braces, and brackets if not inside single quotes
            if (!insideSingleQuotes) {
                if (ch == '(')
                    parentheses++;
                else if (ch == ')')
                    parentheses--;
                if (ch == '{')
                    curlyBraces++;
                else if (ch == '}')
                    curlyBraces--;
                if (ch == '[')
                    squareBrackets++;
                else if (ch == ']')
                    squareBrackets--;

                // If at any point closing bracket comes before opening one
                if (parentheses < 0 || curlyBraces < 0 || squareBrackets < 0) {
                    return false;
                }
            }
        }

        // After the loop, all counts should be zero if balanced
        return parentheses == 0 && curlyBraces == 0 && squareBrackets == 0;
    }

}