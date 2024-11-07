package src.main.java.proyecto_1_lf;


public class Sets {

    // Clase para representar un rango de caracteres
    static class Rango {
        int inicio;
        int fin;

        public Rango(int inicio, int fin) {
            this.inicio = inicio;
            this.fin = fin;
        }

        public boolean contiene(char c) {
            return c >= inicio && c <= fin;
        }
    }

}
