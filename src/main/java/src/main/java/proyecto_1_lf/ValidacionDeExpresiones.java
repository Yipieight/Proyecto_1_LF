package src.main.java.proyecto_1_lf;

import java.io.BufferedReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidacionDeExpresiones {

    public int lineNumber = 0;

    // Method to get the column number of the first match failure
    public int getErrorColumn(String line, Pattern p) {
        Matcher m = p.matcher(line);
        String subcadena = "";
        for (int i = 0; i <= line.length(); i++) {
            subcadena = line.substring(0, i);
            Matcher subMatcher = p.matcher(subcadena);
            if (!subMatcher.matches() && !subMatcher.hitEnd()) {
                subcadena = subcadena.substring(0, i - 1);
                long extraColumn = subcadena.chars().filter(c -> c == '\t').count() * 3;
                return i + (int) extraColumn; // Regresar el índice donde falla
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
                "\\s*([A-Z_]+)\\s*=\\s*'([A-Za-z0-9_])'\\s*\\.\\.\\s*'([A-Za-z0-9_])'(\\s*\\+\\s*('([A-Za-z0-9_])'|('([A-Za-z0-9_])'\\s*\\.\\.\\s*'([A-Za-z0-9_])')))*\\s*|\\s*([A-Z_]+)\\s*=\\s*CHR\\(\\d+\\)\\s*\\.\\.\\s*CHR\\(\\d+\\)");

        System.out.println("Debug: Reading line " + lineNumber + ": " + line);
        logAsciiValues(line);

        // Clean the line and check against the regular expression
        String cleanedLine = removeWhitespace(line);
        System.out.println("Debug: Cleaned line: " + cleanedLine);

        Matcher m = p.matcher(cleanedLine);
        boolean isMatching = m.find();
        System.out.println("Debug: Matching result for line " + lineNumber + ": " + isMatching);

        if (!isMatching) {
            int errorColumn = getErrorColumn(line, p);
            throw new Exception("Error in line " + lineNumber + " at column " + errorColumn + ": " + line);
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
                        .println("Error in line " + lineNumber + ": Unbalanced parentheses, braces, or brackets.");
                return false;
            } else {
                System.out.println("Debug: Line " + lineNumber + " passed validation.");
            }
        } else {
            int errorColumn = getErrorColumn(line, p);
            throw new Exception("Error in line " + lineNumber + " at column " + errorColumn + ": " + line);
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
        Pattern reservadasPattern = Pattern.compile("(RESERVADAS\\(\\))|([A-Z]*\\(\\))");

        // Pattern to match action definitions like 18 = 'PROGRAM'
        Pattern actionPattern = Pattern.compile("\\s+\\d+\\s*=\\s*'\\w+'");

        boolean insideActionBlock = false; // Track whether we're inside an action block
        boolean readFunction = false; // Track whether we're inside an action block

        while ((line = (tempLine != null ? tempLine : reader.readLine())) != null) {
            if (line.trim().isEmpty()) {
                lineNumber++;
                tempLine = null;
                continue; // Ignora lineas vacias
            }
            else if (line.trim().equals("ACTIONS")){
                tempLine = null;
                continue; 
            }

            lineNumber++;
            // Check for RESERVADAS() on the first line
            if (!readFunction) {
                readFunction = true;
                Matcher reservadasMatcher = reservadasPattern.matcher(line);
                if (line.trim().equals("RESERVADAS()")) {
                    haveFuntionReservadas = true;
                }
                if (!reservadasMatcher.matches()) {
                    int errorColumn = getErrorColumn(line, reservadasPattern);
                    System.out.println("Error in line " + lineNumber + " at column " + errorColumn + ": " + line);
                    return false;
                }
                else{
                    tempLine = null;
                    System.out.println("Debug: Line " + lineNumber + " passed validation.");
                    continue;
                }
            }


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
                    readFunction = false;
                    insideActionBlock = false; // End of block
                    return true;
                }
                Matcher actionMatcher = actionPattern.matcher(line);
                if (!actionMatcher.matches()) {
                    int errorColumn = getErrorColumn(line, actionPattern);
                    System.out.println("Error in line " + lineNumber + " at column " + errorColumn + ": " + line);
                    return false;
                } else {
                    System.out.println("Debug: Line " + lineNumber + " passed validation.");
                }
            } else {
                System.out.println("Error: Expected opening `{` at line " + lineNumber +"and column:" + getErrorColumn(line,Pattern.compile("{")));
                return false;
            }
        } 
        if (insideActionBlock) {
            System.out.println("Error: Missing closing `}` for action block.");
            return false;
        }

        return true;
    }

    boolean verificarError(String line) {
        System.out.println("Debug: Verifying ERROR section.");

        Pattern p = Pattern.compile("\\s*ERROR\\s*=\\s*(\\d+)");

        System.out.println("Debug: Reading line " + lineNumber + ": " + line);

        Matcher m = p.matcher(line);
        boolean isMatching = m.matches();
        System.out.println("Debug: Matching result for line " + lineNumber + ": " + isMatching);

        if (!isMatching) {
            throw new IllegalStateException(
                    "Error in line " + lineNumber + " at column " + getErrorColumn(line, p) + ": " + line);
        } else {
            System.out.println("Debug: Line " + lineNumber + " passed validation.");
        }
        return true;
    }

    private String removeWhitespace(String line) {
        return line.replaceAll("\\s+", ""); // Replace all whitespace (spaces, tabs, etc.) with nothing
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