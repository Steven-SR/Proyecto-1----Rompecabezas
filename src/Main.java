public class Main {
    public static void main(String[] args) {
        Tablero tResolver = new Tablero(100, 9);
        tResolver.createTablero();
        
        System.out.println("=== TABLERO ORIGINAL ===");
        tResolver.printTablero();
        System.out.println("\nTablero válido: " + tResolver.checkBoard());
        
        System.out.println("\n=== TABLERO DESORDENADO ===");
        tResolver.scrambleTablero();
        tResolver.printTablero();
        System.out.println("Tablero válido: " + tResolver.checkBoard());
        
        System.out.println("\n=== RESOLVIENDO CON ALGORITMO VORAZ ===");
        Voraz voraz = new Voraz();
        
        long inicio = System.currentTimeMillis();
        boolean resultado = voraz.solveGreedyOptimizado(tResolver);
        long fin = System.currentTimeMillis();
        
        System.out.println("\n=== RESULTADO ===");
        System.out.println("¿Resuelto?: " + resultado);
        System.out.println("Tiempo: " + (fin - inicio) + "ms");
        System.out.println("\n=== ESTADÍSTICAS DE OPERACIONES ===");
        System.out.println("Comparaciones: " + voraz.getComparaciones());
        System.out.println("Asignaciones: " + voraz.getAsignaciones());
        System.out.println("Total de operaciones: " + (voraz.getComparaciones() + voraz.getAsignaciones()));
        
        if (resultado) {
            System.out.println("\nTablero resuelto:");
            tResolver.printTablero();
            System.out.println("\nTablero válido: " + tResolver.checkBoard());
        }
    }
}