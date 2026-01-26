//Clase Pieza
//! Mantener el orden arriba, derecha, abajo, izquierda (porque la profe dijo asi xddd)
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
    }
    //Getters
    public int getUp() {return up;}
    public int getRight() {return right;}
    public int getDown() {return down;}
    public int getLeft() {return left;}

    //Metodo para imprimir la pieza
    //TODO mejorar la impresion
    public void printPieza(){
        System.out.print("Up: " + up + " Right: " + right + " Down: " + down + " Left: " + left +  " || ");
    }
}
