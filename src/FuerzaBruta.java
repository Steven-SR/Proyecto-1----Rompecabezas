import java.util.ArrayList;
import java.util.List;

public class FuerzaBruta {

    // Contadores para analisis de complejidad
    private long comparaciones = 0;
    private long asignaciones = 0;
    private long intentos = 0;
    private long podas = 0;

    /**
     * Verifica si una pieza puede colocarse en una posición específica.
     * Compara los bordes de la pieza con sus vecinos (izquierda y arriba)
     * para asegurar que coincidan los números.
     * 
     * @param board El tablero actual
     * @param row   Fila de la posición
     * @param col   Columna de la posición
     * @param piece La pieza que se desea colocar
     * @return true si la pieza encaja legalmente, false de lo contrario
     */
    public boolean canPlace(Tablero board, int row, int col, Pieza piece) {
        // Si es la primera pieza no ocupa verificar
        comparaciones += 2; // row == 0 && col == 0 // +1
        if (row == 0 && col == 0) { // +1
            return true; // +1
        }

        // Primera fila (solo verifica a la izquierda)
        comparaciones++; // row == 0 // +1
        if (row == 0) { // +1
            asignaciones++; // +1
            Pieza leftPiece = board.tablero[row][col - 1]; // +1
            comparaciones += 2; // +1
            return leftPiece != null && piece.getLeft() == leftPiece.getRight(); // +1
        }

        // Primera columna (solo verifica arriba)
        comparaciones++; // col == 0 // +1
        if (col == 0) { // +1
            asignaciones++; // +1
            Pieza upPiece = board.tablero[row - 1][col]; // +1
            comparaciones += 2; // +1
            return upPiece != null && piece.getUp() == upPiece.getDown(); // +1
        }

        // Resto de posiciones (verifica izquierda y arriba)
        asignaciones += 2; // +2
        Pieza leftPiece = board.tablero[row][col - 1]; // +1
        Pieza upPiece = board.tablero[row - 1][col]; // +1

        comparaciones += 2; // +1
        asignaciones++; // +1
        boolean leftMatches = leftPiece != null && piece.getLeft() == leftPiece.getRight(); // +1

        comparaciones += 2; // +1
        asignaciones++; // +1
        boolean upMatches = upPiece != null && piece.getUp() == upPiece.getDown(); // +1

        comparaciones++; // +1
        return leftMatches && upMatches; // +1
    }
    // Suma canPlace: 1+1+1+1+1+1+1+1+1+1+1+1+2+1+1+1+1+1+1+1+1+1+1 = 23 (Peor caso)
    // T_canPlace(N) = 23 = O(1)

    /**
     * Este es el backtracking que usamos para resolver el rompecabezas.
     * Intenta colocar cada pieza disponible en la posición actual, si encaja,
     * avanza a la siguiente posición recursivamente. Si no hay solución,
     * retrocede (backtrack) y prueba la siguiente pieza.
     * 
     * @param board           El tablero a completar
     * @param row             Fila actual del proceso
     * @param col             Columna actual del proceso
     * @param availablePieces Lista de piezas que aún no han sido colocadas
     * @return true si se encontró una solución completa, false si no
     */
    public boolean solveBoard(Tablero board, int row, int col, List<Pieza> availablePieces) {
        // Condicion de salida: si llegamos mas alla de la ultima fila, terminamos
        comparaciones++; // +1
        if (row >= board.size) { // +1
            return true; // +1
        }

        // Calcular siguiente posicion
        asignaciones += 2; // +2
        int nextCol = col + 1; // +1
        int nextRow = row; // +1

        comparaciones++; // +1
        if (nextCol >= board.size) { // +1
            asignaciones += 2; // +2
            nextCol = 0; // +1
            nextRow = row + 1; // +1
        }

        // Probar cada pieza disponible (N = total de piezas)
        for (int i = 0; i < availablePieces.size(); i++) { // 1, N+1, N+1
            Pieza piece = availablePieces.get(i); // +N

            if (canPlace(board, row, col, piece)) { // +N * O(1)
                // Hacer la eleccion
                asignaciones++; // +N
                board.setPieza(row, col, piece); // +N

                // Remover la pieza de las disponibles (List.remove es O(N))
                asignaciones++; // +N
                availablePieces.remove(i); // +N * N

                // Llamada recursiva a la siguiente posicion
                comparaciones++; // resultado de solveBoard // +N
                if (solveBoard(board, nextRow, nextCol, availablePieces)) { // +N * T(N-1)
                    return true; // +1
                }

                // Backtrack: deshacer la eleccion
                asignaciones++; // +N
                board.removePieza(row, col); // +N
                asignaciones++; // +N
                availablePieces.add(i, piece); // +N * N
            }
            intentos++; // +N
            asignaciones++; // +N
        }
        comparaciones++; // +1
        podas++; // +1
        return false; // +1
    }
    // Suma solveBoard: 1+1+2+1+1+1+1+2+1+1+1 + (N+1) + N+N+N + N*O(1) + N+N + N*N +
    // N + N*T(N-1) + N+N+N + N*N + N+N + 1+1+1
    // T_solveBoard(N) = N * T(N-1) + 2*N^2 + 13N + 14
    // Big O: O(N * N!)

    /**
     * Inicializa el proceso de resolución por fuerza bruta.
     * Resetea los contadores, limpia el tablero y prepara una copia
     * de todas las piezas para comenzar el backtracking desde (0,0).
     * 
     * @param board El tablero con las piezas desordenadas
     * @return true si el tablero fue resuelto exitosamente
     */
    public boolean solve(Tablero board) {
        // Reiniciar contadores
        comparaciones = 0; // +1
        asignaciones = 0; // +1
        intentos = 0; // +1
        podas = 0; // +1

        // Limpiar el tablero primero (N = total de celdas, n = sqrt(N))
        for (int i = 0; i < board.size; i++) { // 1, sqrt(N)+1, sqrt(N)+1
            for (int j = 0; j < board.size; j++) { // sqrt(N)(1, sqrt(N)+1, sqrt(N)+1)
                board.removePieza(i, j); // +N
            }
        }
        comparaciones++; // +1
        // Crear copia de la lista de piezas disponibles
        asignaciones++; // +1
        List<Pieza> availablePieces = new ArrayList<>(board.listaPiezas); // +N

        // Iniciar resolucion desde la posicion (0, 0)
        return solveBoard(board, 0, 0, availablePieces); // +T(N)
    }
    // Suma solve: 1+1+1+1+1 + (sqrt(N)+1) + sqrt(N)+sqrt(N) + (N+sqrt(N)) + N+N+N+N
    // + sqrt(N)+sqrt(N) + 1+1+N + T(N)
    // T_solve(N) = T_solveBoard(N) + 5N + 7*sqrt(N) + 10
    // Big O: O(N * N!)

    // Getters para los contadores
    /**
     * Obtiene el número de comparaciones totales realizadas.
     */
    public long getComparaciones() {
        return comparaciones;
    }

    /**
     * Obtiene el número de asignaciones totales realizadas.
     */
    public long getAsignaciones() {
        return asignaciones;
    }

    /**
     * Obtiene el número total de intentos realizados.
     */
    public long getIntentos() {
        return intentos;
    }

    /**
     * Obtiene el número de podas realizadas durante el backtracking.
     */
    public long getPodas() {
        return podas;
    }

    /**
     * Ejecuta una prueba individual con un tamaño y rango especifico
     */
    /* private static void ejecutarPrueba(int size, int rangoNum) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println(String.format("PRUEBA: Tablero %dx%d con rango 0..%d", size, size, rangoNum));
        System.out.println("=".repeat(70));

        // Preparacion para medicion de memoria
        System.gc();
        Runtime runtime = Runtime.getRuntime();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

        Tablero tablero = new Tablero(size, rangoNum);
        tablero.createTablero();

        System.out.println("\nTablero original (ordenado):");
        tablero.printTablero();

        // Desordenar el tablero
        tablero.scrambleTablero();
        System.out.println("\nTablero desordenado:");
        tablero.printTablero();

        // Resolver con fuerza bruta
        FuerzaBruta solver = new FuerzaBruta();
        long startTime = System.nanoTime();
        boolean solved = solver.solve(tablero);
        long endTime = System.nanoTime();

        // Medicion de memoria post-ejecucion
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = Math.max(0, memoryAfter - memoryBefore);

        System.out.println("\n--- RESULTADO ---");
        if (solved) {
            System.out.println("Estado: ¡TABLERO RESUELTO!");
            tablero.printTablero();
            boolean check = tablero.checkTablero();
            System.out.println("Verificacion: " + (check ? "CORRECTO" : "INCORRECTO"));
        } else {
            System.out.println("Estado: No se encontro solucion");
        }

        System.out.println("\n--- Estadisticas ---");
        double durationMs = (endTime - startTime) / 1_000_000.0;
        System.out.println(String.format("Tiempo de ejecucion: %.3f ms", durationMs));
        System.out.println("Comparaciones: " + solver.getComparaciones());
        System.out.println("Asignaciones: " + solver.getAsignaciones());
        long lineasEjecutadas = solver.getComparaciones() + solver.getAsignaciones();
        System.out.println("Lineas Ejecutadas (C + A): " + lineasEjecutadas);
        System.out.println("Intentos fallidos: " + solver.getIntentos());
        System.out.println("Podas: " + solver.getPodas());
        System.out.println("Memoria usada: " + memoryUsed + " bytes (" + String.format("%.2f", memoryUsed / 1024.0) + " KB)");
    } */
}
/**
 * ANALISIS DE COMPLEJIDAD NO EMPIRICO GLOBAL:
 * Sea N el numero total de piezas/celdas (N = n * n).
 * 
 * T(N) = T_solveBoard(N) + T_limpieza(N) + T_copia(N)
 * T(N) = [N * T(N-1) + 2*N^2 + O(N)] + [O(N)] + [O(N)]
 * 
 * Aplicando expansion por recurrencia:
 * T(N) = N * (N-1) * (N-2) * ... * 1 * T(0) + sumatoria_de_costos_locales
 * T(N) approx N! * O(N^2)
 * 
 * Complejidad Final Big O: O(N * N!)
 * En terminos del lado n del tablero: O(n^2 * (n^2)!)
 */