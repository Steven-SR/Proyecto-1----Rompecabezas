import java.util.ArrayList;
import java.util.List;

public class Voraz {
    
    // Contadores de operaciones
    private long comparaciones;
    private long asignaciones;
    
    /**
     * Constructor que inicializa los contadores
     */
    public Voraz() {
        resetearContadores();
    }
    
    /**
     * Resetea los contadores de operaciones
     */
    public void resetearContadores() {
        this.comparaciones = 0;
        this.asignaciones = 0;
    }
    
    /**
     * Obtiene el número de comparaciones realizadas
     */
    public long getComparaciones() {
        return comparaciones;
    }
    
    /**
     * Obtiene el número de asignaciones realizadas
     */
    public long getAsignaciones() {
        return asignaciones;
    }
    /**
     * Algoritmo voraz optimizado para resolver el rompecabezas de piezas encajables.
     * Estrategia: Limpia el tablero y para cada posición (incluyendo [0][0]),
     * selecciona la pieza disponible que mejor encaja con sus vecinos.
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
                asignaciones++; // Contar asignación de condición
                board.removePieza(i, j);
                asignaciones++; // Contar removePieza como asignación
            }
        }
        
        // Resolver posición por posición usando estrategia voraz (incluyendo [0][0])
        for (int i = 0; i < board.size; i++) {
            comparaciones++; // Comparación del for i
            for (int j = 0; j < board.size; j++) {
                comparaciones++; // Comparación del for j
                
                Pieza mejorPieza = null;
                asignaciones++; // Asignación de mejorPieza
                int indiceMejor = -1;
                asignaciones++; // Asignación de indiceMejor
                int mejorPuntuacion = Integer.MIN_VALUE;
                asignaciones++; // Asignación de mejorPuntuacion
                
                // Buscar la mejor pieza para esta posición
                for (int k = 0; k < disponibles.size(); k++) { 
                    comparaciones++; // Comparación del for k
                    Pieza pieza = disponibles.get(k);
                    asignaciones++; // Asignación de pieza
                    
                    // Validar restricciones
                    comparaciones++; // Comparación esValida
                    if (esValida(board, i, j, pieza)) {
                        int puntuacion = calcularPuntuacion(board, i, j, pieza);
                        asignaciones++; // Asignación de puntuacion
                        
                        comparaciones++; // Comparación puntuacion > mejorPuntuacion
                        if (puntuacion > mejorPuntuacion) {
                            asignaciones++; // Asignación mejorPuntuacion
                            mejorPuntuacion = puntuacion;
                            asignaciones++; // Asignación mejorPieza
                            mejorPieza = pieza;
                            asignaciones++; // Asignación indiceMejor
                            indiceMejor = k;
                        }
                    }
                }
                
                comparaciones++; // Comparación mejorPieza != null
                if (mejorPieza != null) {
                    board.setPieza(i, j, mejorPieza);
                    asignaciones++; // Asignación setPieza
                    disponibles.remove(indiceMejor);
                    asignaciones++; // Asignación remove
                } else {
                    return false;
                }
            }
        }
        
        return board.checkBoard();
    }
    
    /**
     * Calcula puntuación para una pieza en una posición específica.
     * Mayor puntuación significa mejor encaje con los vecinos.
     */
    private int calcularPuntuacion(Tablero board, int row, int col, Pieza pieza) {
        int puntuacion = 0;
        asignaciones++; // Asignación de puntuacion
        
        // Bonus por encaje vertical
        comparaciones++; // Comparación row > 0
        if (row > 0) {
            Pieza vecino = board.getPieza(row - 1, col);
            asignaciones++; // Asignación de vecino
            comparaciones++; // Comparación vecino != null
            comparaciones++; // Comparación vecino.getDown() == pieza.getUp()
            if (vecino != null && vecino.getDown() == pieza.getUp()) {
                asignaciones++; // Asignación puntuacion += 50
                puntuacion += 50;
            }
        }
        
        // Bonus por encaje horizontal
        comparaciones++; // Comparación col > 0
        if (col > 0) {
            Pieza vecinoIzq = board.getPieza(row, col - 1);
            asignaciones++; // Asignación vecinoIzq
            comparaciones++; // Comparación vecinoIzq != null
            comparaciones++; // Comparación vecinoIzq.getRight() == pieza.getLeft()
            if (vecinoIzq != null && vecinoIzq.getRight() == pieza.getLeft()) {
                asignaciones++; // Asignación puntuacion += 50
                puntuacion += 50;
            }
        }
        
        // Bonus por ser esquina o borde
        comparaciones += 4; // 4 comparaciones en la condición
        if ((row == 0 || row == board.size - 1) && (col == 0 || col == board.size - 1)) {
            asignaciones++; // Asignación puntuacion += 5
            puntuacion += 5;
        } else {
            comparaciones += 3; // 3 comparaciones más en el else if
            if (row == 0 || row == board.size - 1 || col == 0 || col == board.size - 1) {
                asignaciones++; // Asignación puntuacion += 3
                puntuacion += 3;
            }
        }
        
        asignaciones++; // Asignación puntuacion += 1
        puntuacion += 1;
        return puntuacion;
    }
    
    /**
     * Valida que una pieza pueda colocarse en una posición sin violar restricciones.
     */
    private boolean esValida(Tablero board, int row, int col, Pieza pieza) {
        // Validar restricción superior
        comparaciones++; // Comparación row > 0
        if (row > 0) {
            Pieza vecino = board.getPieza(row - 1, col);
            asignaciones++; // Asignación vecino
            comparaciones++; // Comparación vecino != null
            comparaciones++; // Comparación vecino.getDown() != pieza.getUp()
            if (vecino != null && vecino.getDown() != pieza.getUp()) {
                return false;
            }
        }
        
        // Validar restricción izquierda
        comparaciones++; // Comparación col > 0
        if (col > 0) {
            Pieza vecinoIzq = board.getPieza(row, col - 1);
            asignaciones++; // Asignación vecinoIzq
            comparaciones++; // Comparación vecinoIzq != null
            comparaciones++; // Comparación vecinoIzq.getRight() != pieza.getLeft()
            if (vecinoIzq != null && vecinoIzq.getRight() != pieza.getLeft()) {
                return false;
            }
        }
        
        return true;
    }
}
