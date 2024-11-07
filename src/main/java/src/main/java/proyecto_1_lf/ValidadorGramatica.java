package src.main.java.proyecto_1_lf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.main.java.proyecto_1_lf.Sets.Rango;

public class ValidadorGramatica {
    public static Map<String, String> actionTokens = new HashMap<>();
    public static Map<String, String> sets = new HashMap<>();
    public static Map<String, List<Rango>> mapaRangos = new HashMap<>();

    public static void main(String[] args) throws Exception {
        // Se agrega aqui el archivo que se desea evaluar
        String archivoGramatica = "GRAMATICA.txt";
        BufferedReader reader = new BufferedReader(new FileReader(archivoGramatica));
        String readLine = "";

        Pattern pattern = Pattern.compile("");

        ValidacionDeExpresiones expresiones = new ValidacionDeExpresiones();
        actionTokens = expresiones.actionsTokensReturn();
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
                // Despues de analizar el campo de ACTIONS se verifica si se uso la funcion de
                // RESERVADAS() donde tiene que estar obligatoriamente en el campo de ACTIONS
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
                validation = expresiones.verificarActions(reader, readLine);
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
                procesarLinea(readLine);
            } else {
                validation = validationCamp(readLine, expresiones.lineNumber);
            }
        }
        reader.close();

        if (validation) {
            System.out.println("ALL LINES PASS SUCCESSFUL");
            ExpresionRegularToken Token = new ExpresionRegularToken();
            String ExpresionTokenn = Token.generarExpresionRegular(archivoGramatica);
            String regex = "(" + ExpresionTokenn + ").#";
            System.out.println(regex);
            ExpressionTreeParser parser = new ExpressionTreeParser();
            TreeNode root = parser.parse(regex);
            String rutaArchivo = "arbol_expresion.txt";
            File archivo = new File(rutaArchivo);

            if (!archivo.exists()) {
                archivo.createNewFile();
                System.out.println("Archivo creado: " + archivo.getName());
            }
            ExpressionTreePrinter.printTreeToFile(root, rutaArchivo);

            ExpressionTreeExcelExporter.exportToExcel(root);

            Map<Integer, Set<Integer>> followMap = new HashMap<>();
            root.calculateFollow(followMap);

            ExpressionTreeExcelExporter.exportFollowsToExcel(followMap);

            Set<Integer> firstSet = root.first();

            Map<Integer, String> terminalMap = new HashMap<>();
            generateTerminals(root, terminalMap);

            DFA dfa = new DFA(terminalMap, followMap);
            dfa.generateStates(firstSet);

            // Imprimir los estados del DFA
            /*
             * System.out.println("\nEstados del AFD:");
             * dfa.printStates();
             */

            State firstState = dfa.firstState();
            System.out.println();

            ExpressionTreeExcelExporter.exportDFAStatesToExcel(dfa.states, terminalMap);

            Scanner scanner = new Scanner(System.in);
            String line = "";
            while (!line.equals("exit")) {
                System.out.println("Añade una cadena");
                line = scanner.nextLine();
                System.out.println("------Token-Segun-La-Cadena------");
                validarCadena(line, dfa, firstState);
                System.out.println("\n\n\n");
            }
            scanner.close();

        }
    }

    public static void procesarLinea(String linea) {
        linea = linea.replaceAll("\s+", "").trim();
        if (linea.isEmpty())
            return;

        String[] partes = linea.split("=");
        if (partes.length < 2)
            return;

        String tipo = partes[0];
        String definicion = partes[1].trim().replace("'", "");
        List<Rango> listaRangos = new ArrayList<>();

        String[] rangos = definicion.split("\\+");
        for (String rango : rangos) {
            if (rango.contains("CHR")) {
                rango = rango.replaceAll("CHR\\((\\d+)\\)", "$1");
                String[] limites = rango.split("\\.\\.");
                int inicio = Integer.parseInt(limites[0]);
                int fin = Integer.parseInt(limites[1]);
                listaRangos.add(new Rango(inicio, fin));
            } else if (rango.contains("..")) {
                String[] limites = rango.split("\\.\\.");
                int inicio = limites[0].charAt(0);
                int fin = limites[1].charAt(0);
                listaRangos.add(new Rango(inicio, fin));
            } else {
                // Si es un carácter individual
                char unico = rango.charAt(0);
                listaRangos.add(new Rango(unico, unico));
            }
        }

        mapaRangos.put(tipo, listaRangos);
    }

    public static String detectarRango(char c) {
        for (Map.Entry<String, List<Rango>> entry : mapaRangos.entrySet()) {
            String tipo = entry.getKey();
            List<Rango> rangos = entry.getValue();
            if (tipo.equals("CHARSET")) {
                continue;
            }
            if (perteneceARangos(c, rangos)) {
                return tipo;
            }
        }
        return "ERROR";
    }

    public static String detectarRangoCharset(char c) {
        for (Map.Entry<String, List<Rango>> entry : mapaRangos.entrySet()) {
            String tipo = entry.getKey();
            if (!tipo.equals("CHARSET")) {
                continue;
            }
            List<Rango> rangos = entry.getValue();
            if (perteneceARangos(c, rangos)) {
                return tipo;
            }
        }
        return "ERROR";
    }

    public static boolean perteneceARangos(char c, List<Rango> rangos) {
        for (Rango rango : rangos) {
            if (rango.contiene((int) new String(new char[]{c}).getBytes(Charset.forName("IBM437"))[0] & 0xFF)) {
                return true;
            }
        }
        return false;
    }

    public static void validarCadena(String cadena, DFA dfa, State stateTemp) {
        String[] cadenaSeparado = cadena.split(" ");
        for (String token : cadenaSeparado) {
            String preVal = contieneClaveAction(token.toUpperCase());

            if (preVal != null) {
                System.out.println(preVal);
                continue;
            }
            String val = validarCadenaSeparada(token, dfa, stateTemp);
            if (val.matches("ERROR")) {
                System.out.println(val);
                break;
            } else {
                System.out.println(val);
            }
            stateTemp = dfa.firstState();
        }

    }

    public static String validarCadenaSeparada(String token, DFA dfa, State stateTemp) {
        for (char c : token.toCharArray()) {
            if (stateTemp.transitions.containsKey("CHARSET")) {
                String preSetsCharacter = detectarRangoCharset(token.charAt(1));
                stateTemp = stateTemp.transitions.get(preSetsCharacter);
                if (stateTemp == null) {
                    return (token + " = " + "Error");
                }
                continue;
            }
            String preSets = detectarRango(c);
            if (preSets != null && !preSets.equals("ERROR")) {
                stateTemp = stateTemp.transitions.get(preSets);
                continue;
            }
            stateTemp = stateTemp.transitions.get(String.valueOf(c));
            if (stateTemp == null && preSets.equals("ERROR")) {
                return ("" + token + " = error");
            }

        }
        if (stateTemp != null) {
            String val = contieneClaveConPatron(stateTemp.transitions);
            if (val != null) {
                return (token + " = " + val);
            } else {
                return (token + " = " + "Error");
            }
        } else {
            return (token + " = " + "Error");
        }

    }

    public static String contieneClaveAction(String cadena) {

        String action = actionTokens.get(cadena);
        if (action != null) {
            return cadena + " = " + action;
        }
        return null;
    }

    public static String contieneClaveConPatron(Map<String, State> mapa) {
        for (String clave : mapa.keySet()) {
            if (clave.matches("T\\d+")) {
                return clave; // Encontramos una coincidencia
            }
        }
        return null; // No se encontró ninguna coincidencia
    }

    private static void generateTerminals(TreeNode node, Map<Integer, String> terminalMap) {
        if (node instanceof OperandNode) {
            OperandNode opNode = (OperandNode) node;
            if (!opNode.getValue().equals("#")) {
                terminalMap.put(opNode.getId(), opNode.getValue());
            }
        } else if (node instanceof OperatorNode) {
            OperatorNode opNode = (OperatorNode) node;
            generateTerminals(opNode.getLeft(), terminalMap);
            generateTerminals(opNode.getRight(), terminalMap);
        } else if (node instanceof UnaryOperatorNode) {
            UnaryOperatorNode unaryNode = (UnaryOperatorNode) node;
            generateTerminals(unaryNode.getChild(), terminalMap);
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
        if (matcher.matches() || matcher.hitEnd()) {
            System.out.println("Error in line " + lineNumber + " at column "
                    + temp.getErrorColumn(line, matcher.pattern()) + ": " + line);
            return false;
        }

        pattern = Pattern.compile("ACTIONS");
        matcher = pattern.matcher(line);
        if (matcher.matches() || matcher.hitEnd()) {
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
                + temp.getErrorColumn(line, pattern) + ": " + line);
        return false;
    }
}