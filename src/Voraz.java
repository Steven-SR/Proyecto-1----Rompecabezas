import java.util.ArrayList;
import java.util.List;

/**
 * Algoritmo Voraz (Greedy) para resolver el rompecabezas de piezas encajables.
 * 
 * Soporta dos modos de operacion:
 * 1. Voraz puro (sin backtracking): Selecciona la mejor pieza en cada paso
 * 2. Voraz con backtracking: Intenta la mejor pieza primero, retrocede si falla
 * 
 * El modo se puede configurar mediante:
 * - Constructor: new Voraz(true) para backtracking
 * - Setter: voraz.setBacktracking(true)
 * - Metodo directo: solveConBacktracking() o solveGreedyOptimizado()
 */
public class Voraz {

    // ========== CONFIGURACION ==========
    private boolean usarBacktracking;

    // Contadores de operaciones
    private long comparaciones;
    private long asignaciones;
    private long intentosBacktrack;

    /**
     * Constructor por defecto (sin backtracking)
     */
    public Voraz() {
        this.usarBacktracking = false;
        resetearContadores();
    }

    /**
     * Constructor con opcion de backtracking
     * 
     * @param usarBacktracking true para habilitar backtracking
     */
    public Voraz(boolean usarBacktracking) {
        this.usarBacktracking = usarBacktracking;
        resetearContadores();
    }

    /**
     * Activa o desactiva el backtracking
     */
    public void setBacktracking(boolean usar) {
        this.usarBacktracking = usar;
    }

    /**
     * Verifica si el backtracking esta habilitado
     */
    public boolean isBacktrackingEnabled() {
        return usarBacktracking;
    }

    /**
     * Resetea los contadores de operaciones
     */
    public void resetearContadores() {
        this.comparaciones = 0;
        this.asignaciones = 0;
        this.intentosBacktrack = 0;
    }

    /**
     * Obtiene el numero de comparaciones realizadas
     */
    public long getComparaciones() {
        return comparaciones;
    }

    /**
     * Obtiene el numero de asignaciones realizadas
     */
    public long getAsignaciones() {
        return asignaciones;
    }

    /**
     * Obtiene el numero de intentos de backtracking
     */
    public long getIntentosBacktrack() {
        return intentosBacktrack;
    }

    /**
     * Metodo principal de resolucion.
     * Usa backtracking segun la configuracion actual.
     * 
     * @param board El tablero a resolver
     * @return true si logra resolver completamente
     */
    public boolean solve(Tablero board) {
        if (usarBacktracking) {
            return solveConBacktracking(board);
        }
        return solveGreedyOptimizado(board);
    }

    // ========================================================================
    // ALGORITMO VORAZ SIN BACKTRACKING
    // ========================================================================

    /**
     * Algoritmo voraz optimizado para resolver el rompecabezas de piezas
     * encajables.
     * Estrategia: Limpia el tablero y para cada posicion (incluyendo [0][0]),
     * selecciona la pieza disponible que mejor encaja con sus vecinos.
     * 
     * NO usa backtracking - si no encuentra solucion, falla.
     * 
     * @param board El tablero a resolver
     * @return true si logra resolver completamente, false si no puede continuar
     */
    public boolean solveGreedyOptimizado(Tablero board) {
        resetearContadores();
        List<Pieza> disponibles = new ArrayList<>(board.getPieces());

        // Limpiar el tablero completamente
        for (int i = 0; i < board.size; i++) {
            for (int j = 0; j < board.size; j++) {
                asignaciones++;
                board.removePieza(i, j);
                asignaciones++;
            }
        }

        // Resolver posicion por posicion usando estrategia voraz
        for (int i = 0; i < board.size; i++) {
            comparaciones++;
            for (int j = 0; j < board.size; j++) {
                comparaciones++;

                Pieza mejorPieza = null;
                asignaciones++;
                int indiceMejor = -1;
                asignaciones++;
                int mejorPuntuacion = Integer.MIN_VALUE;
                asignaciones++;

                // Buscar la mejor pieza para esta posicion
                for (int k = 0; k < disponibles.size(); k++) {
                    comparaciones++;
                    Pieza pieza = disponibles.get(k);
                    asignaciones++;

                    comparaciones++;
                    if (esValida(board, i, j, pieza)) {
                        int puntuacion = calcularPuntuacion(board, i, j, pieza);
                        asignaciones++;

                        comparaciones++;
                        if (puntuacion > mejorPuntuacion) {
                            asignaciones++;
                            mejorPuntuacion = puntuacion;
                            asignaciones++;
                            mejorPieza = pieza;
                            asignaciones++;
                            indiceMejor = k;
                        }
                    }
                }

                comparaciones++;
                if (mejorPieza != null) {
                    board.setPieza(i, j, mejorPieza);
                    asignaciones++;
                    disponibles.remove(indiceMejor);
                    asignaciones++;
                } else {
                    return false;
                }
            }
        }

        return board.checkTablero();
    }

    // ========================================================================
    // ALGORITMO VORAZ CON BACKTRACKING
    // ========================================================================

    /**
     * Algoritmo voraz CON backtracking.
     * Estrategia: Ordena las piezas candidatas por puntuacion (mejor primero),
     * intenta colocar la mejor, y si falla mas adelante, retrocede y prueba
     * la siguiente mejor opcion.
     * 
     * @param board El tablero a resolver
     * @return true si logra resolver completamente
     */
    public boolean solveConBacktracking(Tablero board) {
        resetearContadores();
        List<Pieza> disponibles = new ArrayList<>(board.getPieces());

        // Limpiar el tablero completamente
        for (int i = 0; i < board.size; i++) {
            for (int j = 0; j < board.size; j++) {
                asignaciones++;
                board.removePieza(i, j);
                asignaciones++;
            }
        }

        // Iniciar resolucion recursiva desde (0,0)
        // Inicializar contadores de bordes para lookahead
        int maxVal = board.rangoNum;
        int[] countUp = new int[maxVal + 1];
        int[] countLeft = new int[maxVal + 1];

        for (Pieza p : disponibles) {
            countUp[p.getUp()]++;
            countLeft[p.getLeft()]++;
        }

        return solveBacktrackRecursivo(board, 0, 0, disponibles, countUp, countLeft);
    }

    /**
     * Metodo recursivo de backtracking.
     * Para cada posicion, ordena las piezas validas por puntuacion (greedy)
     * e intenta cada una en orden de mejor a peor.
     */
    private boolean solveBacktrackRecursivo(Tablero board, int row, int col, List<Pieza> disponibles, int[] countUp,
            int[] countLeft) {
        // Condicion de salida: tablero completo
        comparaciones++;
        if (row >= board.size) {
            return true;
        }

        // Calcular siguiente posicion
        asignaciones += 2;
        int nextCol = col + 1;
        int nextRow = row;

        comparaciones++;
        if (nextCol >= board.size) {
            asignaciones += 2;
            nextCol = 0;
            nextRow = row + 1;
        }

        // Obtener lista de piezas validas ordenadas por puntuacion (mejor primero)
        List<PiezaConPuntuacion> candidatas = new ArrayList<>();
        asignaciones++;

        for (int i = 0; i < disponibles.size(); i++) {
            comparaciones++;
            Pieza pieza = disponibles.get(i);
            asignaciones++;

            comparaciones++;
            if (esValida(board, row, col, pieza)) {
                int puntuacion = calcularPuntuacion(board, row, col, pieza);
                candidatas.add(new PiezaConPuntuacion(pieza, i, puntuacion));
                asignaciones += 2;
            }
        }

        // Ordenar por disponibilidad de vecinos (Ascendente: Fail-First /
        // Hardest-First)
        // Preferimos piezas que tengan POCOS vecinos compatibles futuros.
        // Esto ayuda a que, si una rama va a fallar, falle rapido (poda temprana).
        candidatas.sort((a, b) -> {
            int scoreA = 0;
            int scoreB = 0;

            // Score A
            if (col < board.size - 1)
                scoreA += countLeft[a.pieza.getRight()];
            if (row < board.size - 1)
                scoreA += countUp[a.pieza.getDown()];

            // Score B
            if (col < board.size - 1)
                scoreB += countLeft[b.pieza.getRight()];
            if (row < board.size - 1)
                scoreB += countUp[b.pieza.getDown()];

            return scoreA - scoreB; // Ascending (Fail-First)
        });
        asignaciones++;

        // Log de progreso cada 5 millones de comparaciones
        if (comparaciones % 5000000 == 0) {
            // System.out.println("[INFO] Buscando... Ops: " + comparaciones + ", Depth: " +
            // (row * board.size + col));
        }

        // Intentar cada pieza candidata en orden
        for (PiezaConPuntuacion candidata : candidatas) {
            comparaciones++;
            intentosBacktrack++;

            // Colocar la pieza

            // GUILLOTINE OPTIMIZATION: Lookahead (Poda)
            // Verificar si al usar esta pieza nos quedamos sin opciones para los vecinos
            // futuros
            Pieza p = candidata.pieza;

            // Simular uso de la pieza (decrementar contadores)
            countUp[p.getUp()]--;
            countLeft[p.getLeft()]--;

            boolean posible = true;

            // Verificar vecino derecho (necesita un left == p.right)
            if (col < board.size - 1) {
                if (countLeft[p.getRight()] <= 0) {
                    posible = false;
                }
            }

            // Verificar vecino abajo (necesita un up == p.down)
            if (posible && row < board.size - 1) {
                if (countUp[p.getDown()] <= 0) {
                    posible = false;
                }
            }

            if (!posible) {
                // Restaurar y podar rama
                countUp[p.getUp()]++;
                countLeft[p.getLeft()]++;
                continue;
            }

            board.setPieza(row, col, candidata.pieza);
            asignaciones++;

            // Remover de disponibles
            disponibles.remove(candidata.indiceOriginal);
            asignaciones++;

            // Intentar resolver el resto recursivamente
            comparaciones++;
            if (solveBacktrackRecursivo(board, nextRow, nextCol, disponibles, countUp, countLeft)) {
                return true;
            }

            // Backtrack: deshacer la eleccion
            board.removePieza(row, col);
            asignaciones++;
            disponibles.add(candidata.indiceOriginal, candidata.pieza);

            // GUILLOTINE: Restaurar contadores
            countUp[candidata.pieza.getUp()]++;
            countLeft[candidata.pieza.getLeft()]++;

            asignaciones++;
        }

        // No se encontro solucion desde esta posicion
        return false;
    }

    /**
     * Clase auxiliar para almacenar pieza con su puntuacion e indice
     */
    private static class PiezaConPuntuacion {
        Pieza pieza;
        int indiceOriginal;
        int puntuacion;

        PiezaConPuntuacion(Pieza pieza, int indice, int puntuacion) {
            this.pieza = pieza;
            this.indiceOriginal = indice;
            this.puntuacion = puntuacion;
        }
    }

    // ========================================================================
    // METODOS AUXILIARES (usados por ambos algoritmos)
    // ========================================================================

    /**
     * Calcula puntuacion para una pieza en una posicion especifica.
     * Mayor puntuacion significa mejor encaje con los vecinos.
     */
    private int calcularPuntuacion(Tablero board, int row, int col, Pieza pieza) {
        int puntuacion = 0;
        asignaciones++;

        // Bonus por encaje vertical
        comparaciones++;
        if (row > 0) {
            Pieza vecino = board.getPieza(row - 1, col);
            asignaciones++;
            comparaciones += 2;
            if (vecino != null && vecino.getDown() == pieza.getUp()) {
                asignaciones++;
                puntuacion += 50;
            }
        }

        // Bonus por encaje horizontal
        comparaciones++;
        if (col > 0) {
            Pieza vecinoIzq = board.getPieza(row, col - 1);
            asignaciones++;
            comparaciones += 2;
            if (vecinoIzq != null && vecinoIzq.getRight() == pieza.getLeft()) {
                asignaciones++;
                puntuacion += 50;
            }
        }

        // Bonus por ser esquina o borde
        comparaciones += 4;
        if ((row == 0 || row == board.size - 1) && (col == 0 || col == board.size - 1)) {
            asignaciones++;
            puntuacion += 5;
        } else {
            comparaciones += 3;
            if (row == 0 || row == board.size - 1 || col == 0 || col == board.size - 1) {
                asignaciones++;
                puntuacion += 3;
            }
        }

        asignaciones++;
        puntuacion += 1;
        return puntuacion;
    }

    /**
     * Valida que una pieza pueda colocarse en una posicion sin violar
     * restricciones.
     */
    private boolean esValida(Tablero board, int row, int col, Pieza pieza) {
        // Validar restriccion superior
        comparaciones++;
        if (row > 0) {
            Pieza vecino = board.getPieza(row - 1, col);
            asignaciones++;
            comparaciones += 2;
            if (vecino != null && vecino.getDown() != pieza.getUp()) {
                return false;
            }
        }

        // Validar restriccion izquierda
        comparaciones++;
        if (col > 0) {
            Pieza vecinoIzq = board.getPieza(row, col - 1);
            asignaciones++;
            comparaciones += 2;
            if (vecinoIzq != null && vecinoIzq.getRight() != pieza.getLeft()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Ejecuta una prueba individual con un tamano y rango especifico
     */
    private static void ejecutarPrueba(int size, int rangoNum, boolean conBacktracking) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println(String.format("PRUEBA: Tablero %dx%d con rango 0..%d (Backtracking: %s)",
                size, size, rangoNum, conBacktracking ? "SI" : "NO"));
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

        // Resolver con algoritmo voraz
        Voraz solver = new Voraz(conBacktracking);
        long startTime = System.nanoTime();
        boolean solved = solver.solve(tablero);
        long endTime = System.nanoTime();

        // Medicion de memoria post-ejecucion
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = Math.max(0, memoryAfter - memoryBefore);

        System.out.println("\n--- RESULTADO ---");
        if (solved) {
            System.out.println("Estado: TABLERO RESUELTO CORRECTAMENTE");
            tablero.printTablero();
            System.out.println("Verificacion: " + (tablero.checkTablero() ? "CORRECTO" : "INCORRECTO"));
        } else {
            System.out.println("Estado: NO SE ENCONTRO SOLUCION COMPLETA");
            System.out.println("Nota: El algoritmo voraz " +
                    (conBacktracking ? "con backtracking no encontro solucion"
                            : "sin backtracking no garantiza solucion"));
            tablero.printTablero();
        }

        System.out.println("\n--- Estadisticas ---");
        double durationMs = (endTime - startTime) / 1_000_000.0;
        System.out.println(String.format("Tiempo de ejecucion: %.3f ms", durationMs));
        System.out.println("Comparaciones: " + solver.getComparaciones());
        System.out.println("Asignaciones: " + solver.getAsignaciones());
        if (conBacktracking) {
            System.out.println("Intentos de backtrack: " + solver.getIntentosBacktrack());
        }
        long lineasEjecutadas = solver.getComparaciones() + solver.getAsignaciones();
        System.out.println("Lineas Ejecutadas (C + A): " + lineasEjecutadas);
        System.out.println(
                "Memoria usada: " + memoryUsed + " bytes (" + String.format("%.2f", memoryUsed / 1024.0) + " KB)");
    }

    /**
     * Metodo main para probar el algoritmo con multiples tamanos y rangos.
     */
    public static void main(String[] args) {
        System.out.println("=====================================================================");
        System.out.println("              ALGORITMO VORAZ - MEDICION EMPIRICA                    ");
        System.out.println("=====================================================================");

        // Tamanos de tablero a probar
        int[] sizes = { 3, 5, 10, 15, 30 };

        // Rangos de numeros segun PDF
        int[] rangos = { 9, 15 };

        // Probar SIN backtracking
        System.out.println("\n");
        System.out.println("*".repeat(70));
        System.out.println("*  MODO: VORAZ SIN BACKTRACKING");
        System.out.println("*".repeat(70));

        for (int rangoNum : rangos) {
            System.out.println("\n");
            System.out.println("-".repeat(70));
            System.out.println(String.format("  COMBINACION DE NUMEROS: 0..%d", rangoNum));
            System.out.println("-".repeat(70));

            for (int size : sizes) {
                ejecutarPrueba(size, rangoNum, false);
            }
        }

        // Probar CON backtracking
        System.out.println("\n");
        System.out.println("*".repeat(70));
        System.out.println("*  MODO: VORAZ CON BACKTRACKING");
        System.out.println("*".repeat(70));

        for (int rangoNum : rangos) {
            System.out.println("\n");
            System.out.println("-".repeat(70));
            System.out.println(String.format("  COMBINACION DE NUMEROS: 0..%d", rangoNum));
            System.out.println("-".repeat(70));

            for (int size : sizes) {
                ejecutarPrueba(size, rangoNum, true);
            }
        }

        System.out.println("\n");
        System.out.println("=".repeat(70));
        System.out.println("Algoritmo Voraz completado para todos los tamanos.");
        System.out.println("=".repeat(70));
    }
}
