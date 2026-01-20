
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Tablero {

    public Random random = new Random();
    public List<Pieza> listaPiezas;
    public Pieza[][] tablero;
    public int size;
    public int pieces;
    public int rangoNum;

    public Tablero(int size, int rangoNum) {
        this.size = size;
        this.pieces = size * size;
        this.rangoNum = rangoNum;
        this.tablero = new Pieza[size][size];
    }

    public void createTablero() {
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                if(i==0 && j==0){
                    tablero[i][j] = new Pieza(random.nextInt(rangoNum), random.nextInt(rangoNum), random.nextInt(rangoNum), random.nextInt(rangoNum));
                }
                else if(i==0){
                    tablero[i][j] = new Pieza(random.nextInt(rangoNum), random.nextInt(rangoNum), random.nextInt(rangoNum), tablero[i][j-1].getRight());
                }
                else if(j==0){
                    tablero[i][j] = new Pieza(tablero[i-1][j].getDown(), random.nextInt(rangoNum), random.nextInt(rangoNum), random.nextInt(rangoNum));
                }
                else{
                    tablero[i][j] = new Pieza(tablero[i-1][j].getDown(), random.nextInt(rangoNum), random.nextInt(rangoNum), tablero[i][j-1].getRight());
                }
            }
        }
        listaPiezas = new ArrayList<>();
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                listaPiezas.add(tablero[i][j]);
            }
        }
    }

    public Pieza[][] getTablero() {
        return tablero;
    }
    
    public List<Pieza> getPieces(){
        return listaPiezas;
    }

    public void scrambleTablero() {
        for (int i = 1; i < size; i++) {
            for(int j=0;j<size;j++){
                int swapRow = random.nextInt(size);
                int swapCol = random.nextInt(size);
                Pieza temp = tablero[i][j];
                tablero[i][j] = tablero[swapRow][swapCol];
                tablero[swapRow][swapCol] = temp;
            }
        }
    }

    //TODO quitar el de debug :v
    public void checkTablero() {
        for (int i = 0; i<size; i++) {
            for(int j=0;j<size;j++){
                if(i==0 && j==0){
                    continue;
                }
                else if(i==0){
                    if(tablero[0][j].getLeft() != tablero[0][j-1].getRight()){
                        System.out.println("No sirve we fila 0");
                        return;
                    }
                }
                else if(j==0){
                    if(tablero[i][0].getUp() != tablero[i-1][0].getDown()){
                        System.out.println("No sirve we columna 0");
                        return;
                    }
                }
                else{
                    if(tablero[i][j].getLeft() != tablero[i][j-1].getRight() || tablero[i][j].getUp() != tablero[i-1][j].getDown()){
                        System.out.println("No sirve we nigga");
                    }
                }
            }
        }
        System.out.println("yippie");
    }
    

   //esto es para el backtrakcing que ocupa un bool
    public boolean checkBoard() {
        for (int i = 0; i<size; i++) {
            for(int j=0;j<size;j++){
                if(i==0 && j==0){
                    continue;
                }
                else if(i==0){
                    if(tablero[0][j].getLeft() != tablero[0][j-1].getRight()){
                        return false;
                    }
                }
                else if(j==0){
                    if(tablero[i][0].getUp() != tablero[i-1][0].getDown()){
                        return false;
                    }
                }
                else{
                    if(tablero[i][j].getLeft() != tablero[i][j-1].getRight() || tablero[i][j].getUp() != tablero[i-1][j].getDown()){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void setPieza(int row, int col, Pieza pieza) {
        if (row >= 0 && row < size && col >= 0 && col < size) {
            tablero[row][col] = pieza;
        }
    }

    public void removePieza(int row, int col) {
        if (row >= 0 && row < size && col >= 0 && col < size) {
            tablero[row][col] = null;
        }
    }

    public void printTablero() {
        for (int i = 0; i < size; i++) {
            System.out.println();
            for(int j=0;j<size;j++){   
            tablero[i][j].printPieza();
            }
        }
    }
}
