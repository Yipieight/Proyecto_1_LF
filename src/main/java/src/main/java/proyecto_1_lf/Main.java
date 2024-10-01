package src.main.java.proyecto_1_lf;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        
        String hola = "256";
        Pattern p = Pattern.compile("((25[0-6])|(2[0-4][0-9])|(1[0-9]{2})|([1-9][0-9]|[1-9]))");
        Matcher m = p.matcher(hola);


        if(m.matches()){
            System.out.println("HOLAA");
        }
    }
}
