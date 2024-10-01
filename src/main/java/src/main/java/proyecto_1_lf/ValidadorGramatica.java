package src.main.java.proyecto_1_lf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidadorGramatica {

    // hola
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader("prueba_3-1.txt"));
        BufferedReader temp;
        String readLine = "";

        Pattern pattern = Pattern.compile("");

        ValidacionDeExpresiones expresiones = new ValidacionDeExpresiones();
        System.out.println(reader.lines());
        String currentName = "";
        boolean validation = true;

        // Read the file and classify each line based on the section
        while ((readLine = reader.readLine()) != null && validation) {
            if (readLine.trim().isEmpty()) {
                expresiones.lineNumber++;
                continue;
            }
            expresiones.lineNumber++; 

            if (pattern.compile("ERROR").matcher(readLine).find() || currentName.trim().equals("ERROR")) {
                currentName = "ERROR";
                validation = expresiones.verificarError(readLine);
            } else if (pattern.compile("ACTIONS").matcher(readLine).find() || currentName.trim().equals("ACTIONS")) {
                currentName = "ACTIONS";
                if (readLine.trim().equals("ACTIONS")) {
                    continue;
                }
                validation = expresiones.verificarActions(reader,readLine);
            } else if (pattern.compile("TOKENS").matcher(readLine).find() || currentName.trim().equals("TOKENS")) {
                currentName = "TOKENS";
                if (readLine.trim().equals("TOKENS")) {
                    continue;
                }
                validation = expresiones.verificarTokens(readLine);
            } else if (pattern.compile("SETS").matcher(readLine).find() || currentName.trim().equals("SETS")) {
                currentName = "SETS";
                if (readLine.trim().equals("SETS")) {
                    continue;
                }
                validation = expresiones.verificarSet(readLine);
            } else {
                validation = validationCamp(readLine, expresiones.lineNumber);
            }
        }
        reader.close();

        if (validation) {
            System.out.println("ALL LINES PASS SUCESSFUL");
        }
    }

    public static boolean validationCamp(String line, int lineNumber) {
        ValidacionDeExpresiones temp = new ValidacionDeExpresiones();
        Pattern pattern;
        Matcher matcher;
    
        pattern = Pattern.compile("S[E]?T?S?");
        matcher = pattern.matcher(line);
        if (matcher.find()) {
            System.out.println("Error in line " + lineNumber + " at column "
                    + temp.getErrorColumn(line, matcher.pattern()) + ": " + line);
            return false;
        }
    
        pattern = Pattern.compile("T[O]?K?E?N?S?");
        matcher = pattern.matcher(line);
        if (matcher.find()) {
            System.out.println("Error in line " + lineNumber + " at column "
                    + temp.getErrorColumn(line, matcher.pattern()) + ": " + line);
            return false;
        }
    
        pattern = Pattern.compile("A[C]?T?I?O?N?S?");
        matcher = pattern.matcher(line);
        if (matcher.find()) {
            System.out.println("Error in line " + lineNumber + " at column "
                    + temp.getErrorColumn(line, matcher.pattern()) + ": " + line);
            return false;
        }
    
        pattern = Pattern.compile("E[R]?R?R?O?R?");
        matcher = pattern.matcher(line);
        if (matcher.find()) {
            System.out.println("Error in line " + lineNumber + " at column "
                    + temp.getErrorColumn(line, matcher.pattern()) + ": " + line);
            return false;
        }
    
        return true;
    }
}