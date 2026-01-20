public class Pieza {
    private int up;
    private int down;
    private int left;
    private int right;

    public Pieza(int up, int right, int down, int left) {
        this.up = up;
        this.down = down;
        this.left = left;
        this.right = right;
    }
    public int getUp() {return up;}
    public int getRight() {return right;}
    public int getDown() {return down;}
    public int getLeft() {return left;}
    public void printPieza(){
        System.out.print("Up: " + up + " Right: " + right + " Down: " + down + " Left: " + left +  " || ");
    }
}
