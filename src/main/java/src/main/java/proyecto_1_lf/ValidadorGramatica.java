package src.main.java.proyecto_1_lf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidadorGramatica {

    // hola
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader("GRAMATICA.txt"));
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
                //Despues de analizar el campo de ACTIONS se verifica si se uso la funcion de RESERVADAS() donde tiene que estar obligatoriamente en el campo de ACTIONS
                if (!expresiones.haveFuntionReservadas) {
                    System.out.println("NO CONTAIN RESERVADAS() FUNCTION");
                    validation = false;
                    continue;
                }
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
            System.out.println("ALL LINES PASS SUCCESSFUL");
        }
    }

    public static boolean validationCamp(String line, int lineNumber) {
        ValidacionDeExpresiones temp = new ValidacionDeExpresiones();
        Pattern pattern;
        Matcher matcher;
        line = line.replaceAll("\\s*$", "");
        pattern = Pattern.compile("SETS");
        matcher = pattern.matcher(line);
        if (matcher.matches() || matcher.hitEnd()) {
            System.out.println("Error in line " + lineNumber + " at column "
                    + temp.getErrorColumn(line, matcher.pattern()) + ": " + line);
            return false;
        }
    
        pattern = Pattern.compile("TOKENS");
        matcher = pattern.matcher(line);
        if (matcher.matches()  || matcher.hitEnd()) {
            System.out.println("Error in line " + lineNumber + " at column "
                    + temp.getErrorColumn(line, matcher.pattern()) + ": " + line);
            return false;
        }
    
        pattern = Pattern.compile("ACTIONS");
        matcher = pattern.matcher(line);
        if (matcher.matches()|| matcher.hitEnd()) {
            System.out.println("Error in line " + lineNumber + " at column "
                    + temp.getErrorColumn(line, matcher.pattern()) + ": " + line);
            return false;
        }
    
        pattern = Pattern.compile("ERRROR");
        matcher = pattern.matcher(line);
        if (matcher.matches() || matcher.hitEnd()) {
            System.out.println("Error in line " + lineNumber + " at column "
                    + temp.getErrorColumn(line, matcher.pattern()) + ": " + line);
            return false;
        }

        System.out.println("Error in line " + lineNumber + " at column "
                 + temp.getErrorColumn(line,pattern)+": " + line);
        return false;
    }
}