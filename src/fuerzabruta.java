public class fuerzabruta {
    public boolean canPlace(Tablero board, int row, int col, Pieza piece) {
        if (col == 0 && row == 0) {
            // ! hay que ver como buscar cual es la primera pieza :VV
        }

        //primera fila (solo verifica a la izquierda)
        if (row == 0) {
            //TODO:  la logica
        }

        //TODO: el resto de filas (verifica izquierda y arriba)


        //! ninguna posicion valida :Vvvvvvvvvvvvvvvvv
        // ? dos puntos uve: :v 
        return false;
    }

    public boolean solveBoard(Tablero board, int row, int col) {
        // condicion de salida
        if (board.checkBoard()) {
            return true;
        }

        // elegir siguiente paso
        for (int i = row; i < board.size; i++) {
            for (int j = col; j < board.size; j++) {
                /* 
                creo que esos 2 fors de arriba no van a servir??
                pq no solo van a empezar al inicio desde col, sino cada vez que se repita el primer for
                entonces talvez en vez de hacer 2 fors hacer la llamada recursiva con los nuevos valores de i y j?

                https://tenor.com/view/rayleigh-one-piece-crying-gif-7583256620608062087
                */ 
                for (Pieza piece : board.listaPiezas) { // explorar opciones
                    // evaluar si es solucion
                    if (canPlace(board, i, j, piece)) {
                        board.setPieza(i, j, piece); // hacer la eleccion
                        if (solveBoard(board, i, j)) { // ! nose si esto funcione o haya que hacer una copia para que no pegue con otras ramas de la recursion xd
                            return true; // si es solucion
                        }
                        board.removePieza(i, j);
                    
                    }
                    return false; // backtrack si no es solucion
                }
            }
        }
        return false;
    }

    static boolean solveNQUtil(int board[][], int col) {
        if (col >= N)
            return true;

        for (int i = 0; i < N; i++) {
            if (isSafe(board, i, col)) {
                board[i][col] = 1;
                if (solveNQUtil(board, col + 1))
                    return true;
                board[i][col] = 0; // backtrack
            }
        }

        return false;
    }

    public static void main(String[] args) {

    }
}

/*  
    Choose: Make a choice for the next step towards a solution.
    Explore: Explore all possible options from the current choice.
    Evaluate: Determine if the current choice leads to a solution.
    Backtrack: If the choice doesn’t lead to a solution, undo the choice and try another option.
*/