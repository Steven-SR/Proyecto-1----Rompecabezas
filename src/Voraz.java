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
        resetearContadores(); // +1
        List<Pieza> disponibles = new ArrayList<>(board.getPieces()); // +N

        // Limpiar el tablero completamente
        for (int i = 0; i < board.size; i++) { // 1, sqrt(N)+1, sqrt(N)+1
            for (int j = 0; j < board.size; j++) { // sqrt(N)*(1, sqrt(N)+1, sqrt(N)+1)
                asignaciones++; // +N
                board.removePieza(i, j); //Colocar null en cada celda // +N
                asignaciones++; // +N
            }
        }

        // Resolver posicion por posicion usando estrategia voraz
        for (int i = 0; i < board.size; i++) { // 1, sqrt(N)+1, sqrt(N)+1
            comparaciones++; // +sqrt(N)
            for (int j = 0; j < board.size; j++) { // sqrt(N)*(1, sqrt(N)+1, sqrt(N)+1)
                comparaciones++; // +N

                Pieza mejorPieza = null; // +N
                asignaciones++; // +N
                int indiceMejor = -1; // +N
                asignaciones++; // +N
                int mejorPuntuacion = Integer.MIN_VALUE; // +N
                asignaciones++; // +N

                // Buscar la mejor pieza para esta posicion
                // En iteracion k (0-indexed), hay (N-k) piezas disponibles
                // Total de iteraciones internas: N + (N-1) + (N-2) + ... + 1 = N*(N+1)/2
                for (int k = 0; k < disponibles.size(); k++) { // +N*(N+1)/2
                    comparaciones++; // +N*(N+1)/2
                    Pieza pieza = disponibles.get(k); // +N*(N+1)/2
                    asignaciones++; // +N*(N+1)/2

                    comparaciones++; // +N*(N+1)/2
                    if (esValida(board, i, j, pieza)) { // +N*(N+1)/2 * O(1)
                        int puntuacion = calcularPuntuacion(board, i, j, pieza); // +N*(N+1)/2 * O(1)
                        asignaciones++; // +N*(N+1)/2

                        comparaciones++; // +N*(N+1)/2
                        if (puntuacion > mejorPuntuacion) { // +N*(N+1)/2
                            asignaciones++; // +N*(N+1)/2
                            mejorPuntuacion = puntuacion; // +N*(N+1)/2
                            asignaciones++; // +N*(N+1)/2
                            mejorPieza = pieza; // +N*(N+1)/2
                            asignaciones++; // +N*(N+1)/2
                            indiceMejor = k; // +N*(N+1)/2
                        }
                    }
                }

                comparaciones++; // +N
                if (mejorPieza != null) { // +N
                    board.setPieza(i, j, mejorPieza); // +N
                    asignaciones++; // +N
                    disponibles.remove(indiceMejor); // +N (amortizado)
                    asignaciones++; // +N
                } else {
                    return false; // +1
                }
            }
        }

        return board.checkTablero(); // +1
    }
    // Suma solveGreedyOptimizado:
    // Limpieza: O(N)
    // Loop principal: N posiciones
    // Para cada posicion: 3 asignaciones + buscar en piezas disponibles
    // Busqueda total: sum(k=1 to N) de k = N*(N+1)/2 iteraciones
    // Cada iteracion: esValida O(1) + calcularPuntuacion O(1) + operaciones constantes
    // 
    // T_solveGreedyOptimizado(N) = O(N) + N*O(1) + N*(N+1)/2 * O(1) + O(N)
    // T_solveGreedyOptimizado(N) = O(N) + O(N) + O(N^2) + O(N)
    // T_solveGreedyOptimizado(N) = O(N^2)
    // Big O: O(N^2)

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
        resetearContadores(); // +1
        List<Pieza> disponibles = new ArrayList<>(board.getPieces()); // +N

        // Limpiar el tablero completamente (N = n*n celdas)
        for (int i = 0; i < board.size; i++) { // 1, sqrt(N)+1, sqrt(N)+1
            for (int j = 0; j < board.size; j++) { // sqrt(N)*(1, sqrt(N)+1, sqrt(N)+1)
                asignaciones++; // +N
                board.removePieza(i, j); // +N
                asignaciones++; // +N
            }
        }

        // Iniciar resolucion recursiva desde (0,0)
        // Inicializar contadores de bordes para lookahead
        int maxVal = board.rangoNum; // +1
        int[] countUp = new int[maxVal + 1]; // +R (R = rangoNum)
        int[] countLeft = new int[maxVal + 1]; // +R

        for (Pieza p : disponibles) { // +N iteraciones
            countUp[p.getUp()]++; // +N
            countLeft[p.getLeft()]++; // +N
        }

        return solveBacktrackRecursivo(board, 0, 0, disponibles, countUp, countLeft); // +T_backtrack(N)
    }
    // Suma solveConBacktracking: 1 + N + (sqrt(N)+1) + sqrt(N)*(sqrt(N)+1) + 3N + 1 + 2R + 3N + T_backtrack(N)
    // T_solveConBacktracking(N) = T_backtrack(N) + 7N + 2R + O(sqrt(N))
    // Big O: O(N * N!) en peor caso (dominado por T_backtrack)

    /**
     * Metodo recursivo de backtracking.
     * Para cada posicion, ordena las piezas validas por puntuacion (greedy)
     * e intenta cada una en orden de mejor a peor.
     */
    private boolean solveBacktrackRecursivo(Tablero board, int row, int col, List<Pieza> disponibles, int[] countUp,
            int[] countLeft) {
        // Condicion de salida: tablero completo
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

        // Obtener lista de piezas validas ordenadas por puntuacion (mejor primero)
        List<PiezaConPuntuacion> candidatas = new ArrayList<>(); // +1
        asignaciones++; // +1

        for (int i = 0; i < disponibles.size(); i++) { // 1, K+1, K (K = piezas disponibles)
            comparaciones++; // +K
            Pieza pieza = disponibles.get(i); // +K
            asignaciones++; // +K

            comparaciones++; // +K
            if (esValida(board, row, col, pieza)) { // +K * O(1)
                int puntuacion = calcularPuntuacion(board, row, col, pieza); // +K * O(1)
                candidatas.add(new PiezaConPuntuacion(pieza, i, puntuacion)); // +K
                asignaciones += 2; // +2K
            }
        }

        // Ordenar por disponibilidad de vecinos (Ascendente: Fail-First /
        // Hardest-First)
        // Preferimos piezas que tengan POCOS vecinos compatibles futuros.
        // Esto ayuda a que, si una rama va a fallar, falle rapido (poda temprana).
        candidatas.sort((a, b) -> { // +K*log(K) comparaciones del sort
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
        asignaciones++; // +1

        // Log de progreso cada 5 millones de comparaciones
        if (comparaciones % 5000000 == 0) {
            // System.out.println("[INFO] Buscando... Ops: " + comparaciones + ", Depth: " +
            // (row * board.size + col));
        }

        // Intentar cada pieza candidata en orden (C = candidatas validas, C <= K)
        for (PiezaConPuntuacion candidata : candidatas) { // 1, C+1, C
            comparaciones++; // +C
            intentosBacktrack++; // +C

            // Colocar la pieza

            // GUILLOTINE OPTIMIZATION: Lookahead (Poda)
            // Verificar si al usar esta pieza nos quedamos sin opciones para los vecinos
            // futuros
            Pieza p = candidata.pieza; // +C

            // Simular uso de la pieza (decrementar contadores)
            countUp[p.getUp()]--; // +C
            countLeft[p.getLeft()]--; // +C

            boolean posible = true; // +C

            // Verificar vecino derecho (necesita un left == p.right)
            if (col < board.size - 1) { // +C
                if (countLeft[p.getRight()] <= 0) { // +C
                    posible = false; // +C
                }
            }

            // Verificar vecino abajo (necesita un up == p.down)
            if (posible && row < board.size - 1) { // +C
                if (countUp[p.getDown()] <= 0) { // +C
                    posible = false; // +C
                }
            }

            if (!posible) { // +C
                // Restaurar y podar rama
                countUp[p.getUp()]++; // +C
                countLeft[p.getLeft()]++; // +C
                continue;
            }

            board.setPieza(row, col, candidata.pieza); // +C
            asignaciones++; // +C

            // Remover de disponibles (List.remove es O(K))
            disponibles.remove(candidata.indiceOriginal); // +C * K
            asignaciones++; // +C

            // Intentar resolver el resto recursivamente
            comparaciones++; // +C
            if (solveBacktrackRecursivo(board, nextRow, nextCol, disponibles, countUp, countLeft)) { // +C * T(K-1)
                return true; // +1
            }

            // Backtrack: deshacer la eleccion
            board.removePieza(row, col); // +C
            asignaciones++; // +C
            disponibles.add(candidata.indiceOriginal, candidata.pieza); // +C * K

            // GUILLOTINE: Restaurar contadores
            countUp[candidata.pieza.getUp()]++; // +C
            countLeft[candidata.pieza.getLeft()]++; // +C

            asignaciones++; // +C
        }

        // No se encontro solucion desde esta posicion
        return false; // +1
    }
    // Suma solveBacktrackRecursivo: 1+1+2+1+1+1+2+1+1+1 + (K+1)+K+K+K+K + K*log(K) + 1 + 
    //   (C+1) + C+C+C+C+C+C+C+C+C+C+C+C+C + C*K + C + C + C*T(K-1) + C+C + C*K + C+C+C + 1
    // Donde K = piezas disponibles en esta llamada, C = candidatas validas (C <= K)
    // T_backtrack(N) = K * T(K-1) + K*log(K) + 2*K^2 + O(K) (peor caso: C = K)
    // Aplicando recurrencia: T(N) = N * (N-1) * ... * 1 * T(0) + costos_locales
    // Big O (PEOR CASO): O(N * N!)
    // NOTA: En la practica, el ordenamiento greedy (fail-first) y la poda guillotina
    // reducen significativamente el espacio de busqueda, haciendo que el caso promedio
    // sea mucho mejor que el peor caso factorial.

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
        int puntuacion = 0; // +1
        asignaciones++; // +1

        // Bonus por encaje vertical
        comparaciones++; // +1
        if (row > 0) { // +1
            Pieza vecino = board.getPieza(row - 1, col); // +1
            asignaciones++; // +1
            comparaciones += 2; // +1
            if (vecino != null && vecino.getDown() == pieza.getUp()) { // +1
                asignaciones++; // +1
                puntuacion += 50; // +1
            }
        }

        // Bonus por encaje horizontal
        comparaciones++; // +1
        if (col > 0) { // +1
            Pieza vecinoIzq = board.getPieza(row, col - 1); // +1
            asignaciones++; // +1
            comparaciones += 2; // +1
            if (vecinoIzq != null && vecinoIzq.getRight() == pieza.getLeft()) { // +1
                asignaciones++; // +1
                puntuacion += 50; // +1
            }
        }

        // Bonus por ser esquina o borde
        comparaciones += 4; // +1
        if ((row == 0 || row == board.size - 1) && (col == 0 || col == board.size - 1)) { // +1
            asignaciones++; // +1
            puntuacion += 5; // +1
        } else {
            comparaciones += 3; // +1
            if (row == 0 || row == board.size - 1 || col == 0 || col == board.size - 1) { // +1
                asignaciones++; // +1
                puntuacion += 3; // +1
            }
        }

        asignaciones++; // +1
        puntuacion += 1; // +1
        return puntuacion; // +1
    }
    // Suma calcularPuntuacion: 1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1 = 27 (Peor caso)
    // T_calcularPuntuacion(N) = 27 = O(1)

    /**
     * Valida que una pieza pueda colocarse en una posicion sin violar
     * restricciones.
     */
    private boolean esValida(Tablero board, int row, int col, Pieza pieza) {
        // Validar restriccion superior
        comparaciones++; // +1
        if (row > 0) { // +1
            Pieza vecino = board.getPieza(row - 1, col); // +1
            asignaciones++; // +1
            comparaciones += 2; // +1
            if (vecino != null && vecino.getDown() != pieza.getUp()) { // +1
                return false; // +1
            }
        }

        // Validar restriccion izquierda
        comparaciones++; // +1
        if (col > 0) { // +1
            Pieza vecinoIzq = board.getPieza(row, col - 1); // +1
            asignaciones++; // +1
            comparaciones += 2; // +1
            if (vecinoIzq != null && vecinoIzq.getRight() != pieza.getLeft()) { // +1
                return false; // +1
            }
        }

        return true; // +1
    }
    // Suma esValida: 1+1+1+1+1+1+1+1+1+1+1+1+1+1+1 = 15 (Peor caso)
    // T_esValida(N) = 15 = O(1)
}
/**
 * ANALISIS DE COMPLEJIDAD NO EMPIRICO GLOBAL (VORAZ CON BACKTRACKING):
 * Sea N el numero total de piezas/celdas (N = n * n).
 * 
 * Metodos auxiliares:
 * - T_esValida(N) = O(1)
 * - T_calcularPuntuacion(N) = O(1)
 * 
 * Metodo recursivo solveBacktrackRecursivo:
 * - En cada nivel de recursion con K piezas disponibles:
 *   - Filtrar piezas validas: O(K)
 *   - Ordenar candidatas (fail-first): O(K * log(K))
 *   - Para cada candidata C (C <= K):
 *     - Verificacion guillotina (lookahead): O(1)
 *     - Colocar pieza: O(1)
 *     - Remover de lista: O(K)
 *     - Llamada recursiva: T(K-1)
 *     - Backtrack (restaurar): O(K)
 * 
 * Recurrencia (peor caso, todas las piezas son candidatas validas):
 * T(K) = K * T(K-1) + O(K^2) + O(K * log(K))
 * T(K) = K * T(K-1) + O(K^2)
 * 
 * Expansion de la recurrencia:
 * T(N) = N * T(N-1) + O(N^2)
 *      = N * (N-1) * T(N-2) + N * O((N-1)^2) + O(N^2)
 *      = N * (N-1) * (N-2) * ... * 1 * T(0) + sum_de_costos
 *      = N! * O(1) + O(N^3)
 *      = O(N!)
 * 
 * Considerando que hay N posiciones a llenar:
 * T_total(N) = O(N * N!)
 * 
 * Complejidad Final Big O (PEOR CASO): O(N * N!)
 * En terminos del lado n del tablero: O(n^2 * (n^2)!)
 * 
 * NOTA IMPORTANTE SOBRE CASO PROMEDIO:
 * Aunque el peor caso es factorial (similar a fuerza bruta), este algoritmo
 * incluye optimizaciones que lo hacen MUCHO mas rapido en la practica:
 * 
 * 1. Ordenamiento Fail-First: Al probar primero las piezas con menos opciones
 *    futuras, las ramas fallidas se detectan y podan mas rapidamente.
 * 
 * 2. Poda Guillotina (Lookahead): Antes de hacer la recursion, verifica si
 *    quedan piezas compatibles para los vecinos futuros (derecha y abajo).
 *    Si no hay, poda la rama sin explorarla.
 * 
 * Estas optimizaciones no cambian la complejidad asintotica del peor caso,
 * pero reducen drasticamente el numero de nodos explorados en casos tipicos,
 * haciendo que el algoritmo sea practicamente viable para tableros moderados.
 */

/**
 * ANALISIS DE COMPLEJIDAD NO EMPIRICO GLOBAL (VORAZ PURO - SIN BACKTRACKING):
 * Sea N el numero total de piezas/celdas (N = n * n).
 * 
 * Metodos auxiliares:
 * - T_esValida(N) = 15 = O(1)
 * - T_calcularPuntuacion(N) = 27 = O(1)
 * 
 * Metodo principal solveGreedyOptimizado:
 * - Limpieza del tablero: O(N)
 * - Loop principal: N posiciones a llenar
 * - Para la posicion i, quedan (N-i) piezas disponibles
 * - Total de iteraciones del loop interno: N + (N-1) + (N-2) + ... + 1 = N*(N+1)/2
 * - En cada iteracion: esValida O(1) + calcularPuntuacion O(1) = O(1)
 * 
 * T(N) = O(N) + sum(i=0 to N-1) [(N-i) * O(1)]
 * T(N) = O(N) + O(1) * [N + (N-1) + (N-2) + ... + 1]
 * T(N) = O(N) + O(1) * N*(N+1)/2
 * T(N) = O(N) + O(N^2)
 * T(N) = O(N^2)
 * 
 * Complejidad Final Big O: O(N^2)
 * En terminos del lado n del tablero: O(n^4)
 */
