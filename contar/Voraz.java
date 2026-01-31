import java.util.ArrayList;
import java.util.List;

public class Voraz {
    private boolean usarBacktracking;
    private long comparaciones;
    private long asignaciones;
    private long intentosBacktrack;

    public Voraz() {
        this.usarBacktracking = false;
        resetearContadores();
    }

    public Voraz(boolean usarBacktracking) {
        this.usarBacktracking = usarBacktracking;
        resetearContadores();
    }

    public void setBacktracking(boolean usar) {
        this.usarBacktracking = usar;
    }

    public boolean isBacktrackingEnabled() {
        return usarBacktracking;
    }

    public void resetearContadores() {
        this.comparaciones = 0;
        this.asignaciones = 0;
        this.intentosBacktrack = 0;
    }

    public long getComparaciones() {
        return comparaciones;
    }

    public long getAsignaciones() {
        return asignaciones;
    }

    public long getIntentosBacktrack() {
        return intentosBacktrack;
    }

    public boolean solve(Tablero board) {
        if (usarBacktracking) {
            return solveConBacktracking(board);
        }
        return solveGreedyOptimizado(board);
    }

    public boolean solveGreedyOptimizado(Tablero board) {
        resetearContadores(); // +1
        List<Pieza> disponibles = new ArrayList<>(board.getPieces()); // +N
        for (int i = 0; i < board.size; i++) { // 1, sqrt(N)+1, sqrt(N)+1
            for (int j = 0; j < board.size; j++) { // sqrt(N)*(1, sqrt(N)+1, sqrt(N)+1)
                asignaciones++; // +N
                board.removePieza(i, j); // Colocar null en cada celda // +N
                asignaciones++; // +N
            }
        }
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

    public boolean solveConBacktracking(Tablero board) {
        resetearContadores(); // +1
        List<Pieza> disponibles = new ArrayList<>(board.getPieces()); // +N
        for (int i = 0; i < board.size; i++) { // 1, sqrt(N)+1, sqrt(N)+1
            for (int j = 0; j < board.size; j++) { // sqrt(N)*(1, sqrt(N)+1, sqrt(N)+1)
                asignaciones++; // +N
                board.removePieza(i, j); // +N
                asignaciones++; // +N
            }
        }
        int maxVal = board.rangoNum; // +1
        int[] countUp = new int[maxVal + 1]; // +R (R = rangoNum)
        int[] countLeft = new int[maxVal + 1]; // +R
        for (Pieza p : disponibles) { // +N iteraciones
            countUp[p.getUp()]++; // +N
            countLeft[p.getLeft()]++; // +N
        }
        return solveBacktrackRecursivo(board, 0, 0, disponibles, countUp, countLeft); // +T_backtrack(N)
    }

    private boolean solveBacktrackRecursivo(Tablero board, int row, int col, List<Pieza> disponibles, int[] countUp,
            int[] countLeft) {
        // Condicion de salida: tablero completo
        comparaciones++; // +1
        if (row >= board.size) { // +1
            return true; // +1
        }
        asignaciones += 2; // +2
        int nextCol = col + 1; // +1
        int nextRow = row; // +1
        comparaciones++; // +1
        if (nextCol >= board.size) { // +1
            asignaciones += 2; // +2
            nextCol = 0; // +1
            nextRow = row + 1; // +1
        }
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
        candidatas.sort((a, b) -> { // +K*log(K) comparaciones del sort
            int scoreA = 0;
            int scoreB = 0;
            if (col < board.size - 1)
                scoreA += countLeft[a.pieza.getRight()];
            if (row < board.size - 1)
                scoreA += countUp[a.pieza.getDown()];
            if (col < board.size - 1)
                scoreB += countLeft[b.pieza.getRight()];
            if (row < board.size - 1)
                scoreB += countUp[b.pieza.getDown()];
            return scoreA - scoreB; // Ascending (Fail-First)
        });
        asignaciones++; // +1
        if (comparaciones % 5000000 == 0) {
        }
        for (PiezaConPuntuacion candidata : candidatas) { // 1, C+1, C
            comparaciones++; // +C
            intentosBacktrack++; // +C
            Pieza p = candidata.pieza; // +C
            countUp[p.getUp()]--; // +C
            countLeft[p.getLeft()]--; // +C
            boolean posible = true; // +C
            if (col < board.size - 1) { // +C
                if (countLeft[p.getRight()] <= 0) { // +C
                    posible = false; // +C
                }
            }
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
            disponibles.remove(candidata.indiceOriginal); // +C * K
            asignaciones++; // +C
            comparaciones++; // +C
            if (solveBacktrackRecursivo(board, nextRow, nextCol, disponibles, countUp, countLeft)) { // +C * T(K-1)
                return true; // +1
            }
            board.removePieza(row, col); // +C
            asignaciones++; // +C
            disponibles.add(candidata.indiceOriginal, candidata.pieza); // +C * K
            countUp[candidata.pieza.getUp()]++; // +C
            countLeft[candidata.pieza.getLeft()]++; // +C
            asignaciones++; // +C
        }
        return false; // +1
    }

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

    private int calcularPuntuacion(Tablero board, int row, int col, Pieza pieza) {
        int puntuacion = 0; // +1
        asignaciones++; // +1
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

    private boolean esValida(Tablero board, int row, int col, Pieza pieza) {
        comparaciones++; // +1
        if (row > 0) { // +1
            Pieza vecino = board.getPieza(row - 1, col); // +1
            asignaciones++; // +1
            comparaciones += 2; // +1
            if (vecino != null && vecino.getDown() != pieza.getUp()) { // +1
                return false; // +1
            }
        }
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
}