public class Main {
    public static void main(String[] args) {
        Tablero t = new Tablero(2,15);
        t.createTablero();
        t.printTablero();
        System.out.println(t.checkBoard());
        t.scrambleTablero();
        t.printTablero();
        System.out.println(t.checkBoard());
    }
    
}