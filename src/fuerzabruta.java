import java.util.ArrayList;
import java.util.List;

public class fuerzabruta {

    // Contadores para analisis de complejidad
    private long comparaciones = 0;
    private long asignaciones = 0;
    private long intentos = 0;
    private long podas = 0;

    /**
     * Verifica si una pieza puede colocarse en la posicion (row, col).
     * - Primera pieza (0,0): cualquier pieza puede colocarse
     * - Primera fila: solo verifica que la izquierda coincida con la derecha de la
     * pieza anterior
     * - Primera columna: solo verifica que arriba coincida con abajo de la pieza
     * superior
     * - Resto: verifica ambos lados (izquierda y arriba)
     */
    public boolean canPlace(Tablero board, int row, int col, Pieza piece) {
        // Primera pieza (0,0): cualquier pieza puede ir aqui
        comparaciones += 2; // row == 0 && col == 0
        if (row == 0 && col == 0) {
            return true;
        }

        // Primera fila (solo verifica a la izquierda)
        comparaciones++; // row == 0
        if (row == 0) {
            asignaciones++; // leftPiece = ...
            Pieza leftPiece = board.tablero[row][col - 1];
            comparaciones += 2; // leftPiece != null && piece.getLeft() == leftPiece.getRight()
            return leftPiece != null && piece.getLeft() == leftPiece.getRight();
        }

        // Primera columna (solo verifica arriba)
        comparaciones++; // col == 0
        if (col == 0) {
            asignaciones++; // upPiece = ...
            Pieza upPiece = board.tablero[row - 1][col];
            comparaciones += 2; // upPiece != null && piece.getUp() == upPiece.getDown()
            return upPiece != null && piece.getUp() == upPiece.getDown();
        }

        // Resto de posiciones (verifica izquierda y arriba)
        asignaciones += 2; // leftPiece = ..., upPiece = ...
        Pieza leftPiece = board.tablero[row][col - 1];
        Pieza upPiece = board.tablero[row - 1][col];

        comparaciones += 2; // leftPiece != null && piece.getLeft() == leftPiece.getRight()
        asignaciones++; // leftMatches = ...
        boolean leftMatches = leftPiece != null && piece.getLeft() == leftPiece.getRight();

        comparaciones += 2; // upPiece != null && piece.getUp() == upPiece.getDown()
        asignaciones++; // upMatches = ...
        boolean upMatches = upPiece != null && piece.getUp() == upPiece.getDown();

        comparaciones++; // leftMatches && upMatches
        return leftMatches && upMatches;
    }

    /**
     * Resuelve el tablero usando fuerza bruta con backtracking.
     * Recibe una lista de piezas disponibles para evitar usar la misma pieza dos
     * veces.
     */
    public boolean solveBoard(Tablero board, int row, int col, List<Pieza> availablePieces) {
        // Condicion de salida: si llegamos mas alla de la ultima fila, terminamos
        comparaciones++; // row >= board.size
        if (row >= board.size) {
            return true; // Todas las piezas colocadas exitosamente
        }

        // Calcular siguiente posicion
        asignaciones += 2; // nextCol = ..., nextRow = ...
        int nextCol = col + 1;
        int nextRow = row;

        comparaciones++; // nextCol >= board.size
        if (nextCol >= board.size) {
            asignaciones += 2; // nextCol = 0, nextRow = row + 1
            nextCol = 0;
            nextRow = row + 1;
        }

        // Probar cada pieza disponible
        asignaciones++; // i = 0
        for (int i = 0; i < availablePieces.size(); i++) {
            comparaciones++; // i < availablePieces.size()

            asignaciones++; // piece = ...
            Pieza piece = availablePieces.get(i);

            if (canPlace(board, row, col, piece)) {
                // Hacer la eleccion
                asignaciones++; // setPieza cuenta como asignacion
                board.setPieza(row, col, piece);

                // Remover la pieza de las disponibles
                asignaciones++; // remove modifica la lista
                availablePieces.remove(i);

                // Llamada recursiva a la siguiente posicion
                comparaciones++; // resultado de solveBoard
                if (solveBoard(board, nextRow, nextCol, availablePieces)) {
                    return true; // ¡Encontramos solucion!
                }

                // Backtrack: deshacer la eleccion
                asignaciones++; // removePieza
                board.removePieza(row, col);
                asignaciones++; // add modifica la lista
                availablePieces.add(i, piece); // Restaurar la pieza en su posicion original
            }
            intentos++;
            asignaciones++; // i++
        }
        comparaciones++; // ultima comparacion del for
        podas++;
        return false; // No se encontro solucion con esta configuracion
    }

    /**
     * Metodo wrapper para iniciar la resolucion del tablero.
     * Crea una copia de la lista de piezas para no modificar la original.
     */
    public boolean solve(Tablero board) {
        // Reiniciar contadores
        comparaciones = 0;
        asignaciones = 0;
        intentos = 0;
        podas = 0;

        // Limpiar el tablero primero
        asignaciones++; // i = 0
        for (int i = 0; i < board.size; i++) {
            comparaciones++; // i < board.size
            asignaciones++; // j = 0
            for (int j = 0; j < board.size; j++) {
                comparaciones++; // j < board.size
                asignaciones++; // removePieza
                board.removePieza(i, j);
                asignaciones++; // j++
            }
            comparaciones++; // ultima comparacion del for interno
            asignaciones++; // i++
        }
        comparaciones++; // ultima comparacion del for externo

        // Crear copia de la lista de piezas disponibles
        asignaciones++; // availablePieces = ...
        List<Pieza> availablePieces = new ArrayList<>(board.listaPiezas);

        // Iniciar resolucion desde la posicion (0, 0)
        return solveBoard(board, 0, 0, availablePieces);
    }

    // Getters para los contadores
    public long getComparaciones() {
        return comparaciones;
    }

    public long getAsignaciones() {
        return asignaciones;
    }

    public long getIntentos() {
        return intentos;
    }

    public long getPodas() {
        return podas;
    }

    /**
     * Metodo main para probar el algoritmo.
     */
    public static void main(String[] args) {
        // Crear tablero de prueba
        int size = 4;
        int rangoNum = 9;

        Tablero tablero = new Tablero(size, rangoNum);
        tablero.createTablero();

        System.out.println("Tablero original (ordenado):");
        tablero.printTablero();
        System.out.println("\n");

        // Desordenar el tablero
        tablero.scrambleTablero();
        System.out.println("Tablero desordenado:");
        tablero.printTablero();
        System.out.println("\n");

        // Resolver con fuerza bruta
        fuerzabruta solver = new fuerzabruta();
        long startTime = System.currentTimeMillis();
        boolean solved = solver.solve(tablero);
        long endTime = System.currentTimeMillis();

        if (solved) {
            System.out.println("¡Tablero resuelto!");
            tablero.printTablero();
            System.out.println("\n");
            tablero.checkTablero(); // Verificar que esta correcto
        } else {
            System.out.println("No se encontro solucion :(");
        }

        System.out.println("Tiempo de ejecucion: " + (endTime - startTime) + " ms");
        System.out.println("Comparaciones: " + solver.getComparaciones());
        System.out.println("Asignaciones: " + solver.getAsignaciones());
        System.out.println("Total de operaciones: " + (solver.getComparaciones() + solver.getAsignaciones()));
        System.out.println("Intentos fallidos: " + solver.getIntentos());
        System.out.println("Podas: " + solver.getPodas());
    }
}