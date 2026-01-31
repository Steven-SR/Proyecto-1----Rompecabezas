// Fecha de creación: 25 de enero de 2026
// Última modificación: 27 de enero de 2026
public class Pieza {
    //Cuatro lados de la pieza
    private int up;
    private int down;
    private int left;
    private int right;

    //Constructor
    public Pieza(int up, int right, int down, int left) {
        this.up = up;
        this.down = down;
        this.left = left;
        this.right = right;
    // Fecha de creación: 25 de enero de 2026
    // Última modificación: 25 de enero de 2026
    }
    //Getters
    public int getUp() {return up;}
    public int getRight() {return right;}
    public int getDown() {return down;}
    public int getLeft() {return left;}

    //Metodo para imprimir la pieza
    public void printPieza(){
        System.out.print("[" + up + "," + right + "," + down + "," + left + "] ");
    }
}
