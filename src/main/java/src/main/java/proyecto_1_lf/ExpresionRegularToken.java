package src.main.java.proyecto_1_lf;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

public class ExpresionRegularToken {

    public static String generarExpresionRegular(String archivoGramatica) throws IOException {
        Map<Integer, String> tokens = leerTokens(archivoGramatica);
        StringBuilder expresionRegularFinal = new StringBuilder();
        boolean esPrimero = true;  
        
        for (Map.Entry<Integer, String> entry : tokens.entrySet()) {
            int numeroToken = entry.getKey();  
            String token = entry.getValue().trim();  
            
            String expresion = "(("+ token +").T"+numeroToken +")";  
            
            if (!esPrimero) {
                expresionRegularFinal.append("|");  
            }
            expresionRegularFinal.append(expresion);
            esPrimero = false;
        }
        
        return expresionRegularFinal.toString();
    }
    
    
    public static Map<Integer, String> leerTokens(String archivoGramatica) throws IOException {
        Map<Integer, String> tokens = new TreeMap<>();
        boolean enSeccionTokens = false;
        
        try (BufferedReader br = new BufferedReader(new FileReader(archivoGramatica))) {
            String linea;
            Pattern pattern = Pattern.compile("TOKEN\\s*(\\d+)\\s*=\\s*(.*)");  
            
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                
                if (linea.startsWith("TOKENS")) {
                    enSeccionTokens = true;
                    continue;
                }
                
                if (enSeccionTokens && linea.startsWith("ACTIONS")) {
                    break;  
                }
                
                if (enSeccionTokens) {
                    Matcher matcher = pattern.matcher(linea);
                    
                    if (matcher.matches()) {
                        int numeroToken = Integer.parseInt(matcher.group(1));  
                        String contenidoToken = matcher.group(2).trim();  

                        String tokenLimpio = limpiarComillasSimples(contenidoToken);
                        String concatenado = concatenarTerminos(tokenLimpio);
                        String cleanspace = eliminarTodosLosEspacios(concatenado);
                        
                        tokens.put(numeroToken, cleanspace);  
                    }
                }
            }
        }
        
        return tokens;  
    }


    public static String limpiarComillasSimples(String token) {
        token = token.replaceAll("'''", "'");  
        
        token = token.replaceAll("'([*+?.])'", "'$1'");  
        
        token = token.replaceAll("'([^*+?\\(\\)\\.])'", " $1 ");  
    
        return token;
    }
    

    public static String eliminarTodosLosEspacios(String token) {
        return token.replaceAll("\\s+", "");  
    }
    

    public static String concatenarTerminos(String token) {
        token = token.replaceAll("(?<!')\\s*([*+?=,:.;])\\s*(?!')", " $1 ");  // No concatenar operadores o símbolos fuera de comillas simples
    
        token = token.replaceAll("\\s*\\(\\s*", " ( ");  // Evitar concatenación para paréntesis abiertos fuera de comillas
        token = token.replaceAll("\\s*\\)\\s*", " ) ");  // Evitar concatenación para paréntesis cerrados fuera de comillas
    
        token = token.replaceAll("'\\s*([*+?=(),:.;])\\s*'\\s*'\\s*([*+?=(),:.;])\\s*'", "'$1'.'$2'");  // Concatenar dos valores entre comillas simples
    
        token = token.replaceAll("'\\s*\\(\\s*'", "'('");  // Tratar '(' como un valor completo
        token = token.replaceAll("'\\s*\\)\\s*'", "')'");  // Tratar ')' como un valor completo
    
        token = token.replaceAll("(?<=\\w)\\s*'\\s*([*+?=(),:.;])\\s*'", ".'$1'");  // Dato seguido de operador, paréntesis, punto o símbolo entre comillas
        token = token.replaceAll("'\\s*([*+?=(),:.;])\\s*'\\s*(?=\\w)", "'$1'.");  // Operador o símbolo entre comillas seguido de un dato
    
        token = token.replaceAll("(?<=\\w|[{}])\\s*([*+?])", "$1");  // Si el operador está a la derecha de un valor o símbolo, lo concatenamos directamente
    
        token = token.replaceAll("([*+?])\\s+(?=\\w|[{}])", "$1.");  // Si el operador está a la izquierda de un valor o símbolo, se concatena como operador.valor
    
        token = token.replaceAll("\\)\\s+(?=\\w|[{}'\"#\\$%\\-;¡>])", ").");  // Concatenar paréntesis cerrados con valores o símbolos a la derecha
    
        token = token.replaceAll("'\\s*'\\s*'\\s*'", "'''");  // Concatenar comillas simples triples
        token = token.replaceAll("(?<=\\w)\\s*'\\s*'\\s*(?=\\w)", ".'.");  // Concatenar comilla simple con otros valores
    
        token = token.replaceAll("(?<=\\w)\\s*\\(\\s*\\)\\s*", "()");  // Concatenar () con la palabra anterior, como FUNCION()
    
        token = token.replaceAll("(?<=\\w|[<>'\"#\\$%{}\\-;¡>=,:.;])\\s+(?=\\w|[<>'\"#\\$%{}\\-;¡>=,:.;])", ".");  // Concatenar entre términos, caracteres, símbolos, y otros símbolos
    
      
        token = token.replaceAll("(?<=\\w|[<>'\"#\\$%{}\\-;¡>=,:.;])\\s+\\(", ".(");  // Concatenar si hay un paréntesis abierto a la derecha
    
        return token.trim();  
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}


    
    

    

