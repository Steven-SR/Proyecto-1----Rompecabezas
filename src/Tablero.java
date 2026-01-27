//Java.util necesarias de base
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

    //Constructor, inicia con un tablero vacio
    public Tablero(int size, int rangoNum) {
        this.size = size;
        this.pieces = size * size;
        this.rangoNum = rangoNum;
        this.tablero = new Pieza[size][size];
    }

    //Crea un tablero ordenado de base
    public void createTablero() {
        //Llenar el tablero de piezas
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                //Tablero [0][0] siempre random
                if(i==0 && j==0){
                    tablero[i][j] = new Pieza(random.nextInt(rangoNum), random.nextInt(rangoNum), random.nextInt(rangoNum), random.nextInt(rangoNum));
                }
                //Primera fila, solo depende de la pieza anterior
                else if(i==0){
                    tablero[i][j] = new Pieza(random.nextInt(rangoNum), random.nextInt(rangoNum), random.nextInt(rangoNum), tablero[i][j-1].getRight());
                }
                //Primera columna, solo depende de la pieza superior
                else if(j==0){
                    tablero[i][j] = new Pieza(tablero[i-1][j].getDown(), random.nextInt(rangoNum), random.nextInt(rangoNum), random.nextInt(rangoNum));
                }
                //Resto de piezas, depende de la pieza superior y la anterior
                else{
                    tablero[i][j] = new Pieza(tablero[i-1][j].getDown(), random.nextInt(rangoNum), random.nextInt(rangoNum), tablero[i][j-1].getRight());
                }
            }
        }
    }

    public Pieza[][] getTablero() {
        return tablero;
    }
    
    public List<Pieza> getPieces(){
        return listaPiezas;
    }

    //Desordena el tablero, cambia de posicion las piezas de forma aleatoria
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
        //Actualizar la lista de piezas despues de desordenar
        listaPiezas = new ArrayList<>();
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                listaPiezas.add(tablero[i][j]);
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
   //Revisa el tablero y devuelve true si todas las piezas estan en su lugar
    public boolean checkBoard() {
        for (int i = 0; i<size; i++) {
            for(int j=0;j<size;j++){
                //Para la primera pieza no hay que revisar nada
                if(i==0 && j==0){
                    continue;
                }
                //Revisa la primera fila
                else if(i==0){
                    if(tablero[0][j].getLeft() != tablero[0][j-1].getRight()){
                        return false;
                    }
                }
                //Revisa la primera columna
                else if(j==0){
                    if(tablero[i][0].getUp() != tablero[i-1][0].getDown()){
                        return false;
                    }
                }
                //Revisa el resto de piezas
                else{
                    if(tablero[i][j].getLeft() != tablero[i][j-1].getRight() || tablero[i][j].getUp() != tablero[i-1][j].getDown()){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    //Metodos para modificar el tablero, coloca una pieza en una posicion especifica
    public void setPieza(int row, int col, Pieza pieza) {
        if (row >= 0 && row < size && col >= 0 && col < size) {
            tablero[row][col] = pieza;
        }
    }

    //Obtiene una pieza en una posicion especifica
    public Pieza getPieza(int row, int col) {
        if (row >= 0 && row < size && col >= 0 && col < size) {
            return tablero[row][col];
        }
        return null;
    }

    //Remueve una pieza de una posicion especifica, coloca un null
    public void removePieza(int row, int col) {
        if (row >= 0 && row < size && col >= 0 && col < size) {
            tablero[row][col] = null;
        }
    }

    //Imprime el tablero en consola
    public void printTablero() {
        for (int i = 0; i < size; i++) {
            System.out.println();
            for(int j=0;j<size;j++){   
            tablero[i][j].printPieza();
            }
        }
    }
}
