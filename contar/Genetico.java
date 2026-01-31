import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Genetico {
    private long comparaciones;
    private long asignaciones;
    private Random random;
    private List<ResultadoIndividuo> mejoresResultados;

    private static class ResultadoIndividuo {
        List<Pieza> cromosoma;
        int fitness;

        ResultadoIndividuo(List<Pieza> cromosoma, int fitness) {
            this.cromosoma = new ArrayList<>(cromosoma);
            this.fitness = fitness;
        }
    }

    public Genetico() {
        this.random = new Random();
        resetearContadores();
    }

    public void resetearContadores() {
        this.comparaciones = 0;
        this.asignaciones = 0;
    }

    public long getComparaciones() {
        return comparaciones;
    }

    public long getAsignaciones() {
        return asignaciones;
    }

    private int obtenerPoblacionInicial(int size) {
        if (size <= 3)
            return 3;
        if (size <= 5)
            return 5;
        if (size <= 10)
            return 10;
        if (size <= 15)
            return 15;
        return 30; // Para 30x30, 60x60, 100x100
    }

    private int obtenerCantidadHijos(int size) {
        if (size <= 3)
            return 6;
        if (size <= 5)
            return 10;
        if (size <= 10)
            return 20;
        if (size <= 15)
            return 30;
        return 60; // Para 30x30, 60x60, 100x100
    }

    public boolean solve(Tablero board) {
        resetearContadores();
        int n = board.size;
        int numPiezas = n * n;
        int tamPoblacion = obtenerPoblacionInicial(n);
        int cantidadHijos = obtenerCantidadHijos(n);
        int numGeneraciones = 10; // Siempre 10 generaciones segun PDF
        int fitnessObjetivo = 2 * n * (n - 1);
        asignaciones += 5; // +5
        System.out.println("=".repeat(60));
        System.out.println("ALGORITMO GENETICO - ROMPECABEZAS DE PIEZAS ENCAJABLES");
        System.out.println("=".repeat(60));
        System.out.println();
        System.out.println("--- REPRESENTACION DEL CROMOSOMA ---");
        System.out.println("Cromosoma: Permutacion de " + numPiezas + " piezas");
        System.out.println("Gen: Cada pieza en una posicion del cromosoma");
        System.out.println("Alelos: 4 numeros por pieza (up, right, down, left)");
        System.out.println();
        System.out.println("--- PARAMETROS ---");
        System.out.println("Tamano del tablero: " + n + "x" + n);
        System.out.println("Poblacion inicial: " + tamPoblacion + " (SIN REPETIDOS)");
        System.out.println("Cantidad de hijos por generacion: " + cantidadHijos);
        System.out.println("Numero de generaciones: " + numGeneraciones);
        System.out.println("Fitness objetivo (todos los lados calzan): " + fitnessObjetivo);
        System.out.println();
        List<List<Pieza>> poblacion = inicializarPoblacionSinRepetidos(board, tamPoblacion);
        mejoresResultados = new ArrayList<>();
        asignaciones += 2; // +2
        System.out.println("--- POBLACION INICIAL (sin repetidos) ---");
        for (int i = 0; i < poblacion.size(); i++) { // P iteraciones, cada una O(N)
            int fit = calcularFitness(poblacion.get(i), n); // +P * O(N)
            System.out
                    .println("Individuo " + (i + 1) + ": " + cromosomaToString(poblacion.get(i)) + " fitness: " + fit);
        }
        System.out.println();
        for (int gen = 1; gen <= numGeneraciones; gen++) { // G iteraciones
            comparaciones++; // +G
            System.out.println("=".repeat(60));
            System.out.println("GENERACION " + gen);
            System.out.println("=".repeat(60));
            System.out.println();
            int[] fitnesses = new int[poblacion.size()];
            asignaciones++; // +G
            for (int i = 0; i < poblacion.size(); i++) { // P iteraciones
                comparaciones++; // +G*P
                fitnesses[i] = calcularFitness(poblacion.get(i), n); // +G * P * O(N)
                asignaciones++; // +G*P
            }
            actualizarMejoresResultados(poblacion, fitnesses);
            List<List<Pieza>> todosLosHijos = new ArrayList<>();
            Set<String> hijosGenerados = new HashSet<>(); // Para evitar hijos repetidos
            asignaciones += 2; // +2G
            int crucesRealizados = 0;
            int intentosSinProgreso = 0;
            int maxIntentosSinProgreso = cantidadHijos * 10; // limite para evitar bucle infinito
            asignaciones++; // +G
            while (todosLosHijos.size() < cantidadHijos && intentosSinProgreso < maxIntentosSinProgreso) { // H/2 cruces
                comparaciones++; // +G*H
                crucesRealizados++;
                int hijosPrevios = todosLosHijos.size();
                int idxPadre1 = seleccionarMejor(fitnesses);
                int idxPadre2 = seleccionarSegundoMejor(fitnesses, idxPadre1);
                List<Pieza> padre1 = poblacion.get(idxPadre1);
                List<Pieza> padre2 = poblacion.get(idxPadre2);
                int fitPadre1 = fitnesses[idxPadre1];
                int fitPadre2 = fitnesses[idxPadre2];
                asignaciones += 4; // +4*G*H
                List<List<Pieza>> hijos = crucePMX(padre1, padre2); // +G * H * O(N^2)
                List<Pieza> hijo1 = hijos.get(0);
                List<Pieza> hijo2 = hijos.get(1);
                int fitHijo1 = calcularFitness(hijo1, n); // +G * H * O(N)
                int fitHijo2 = calcularFitness(hijo2, n); // +G * H * O(N)
                asignaciones += 5; // +5*G*H
                System.out.println("--- Cruce " + crucesRealizados + " ---");
                System.out.println("Padre1: " + cromosomaToString(padre1) + " puntuacion: " + fitPadre1);
                System.out.println("Padre2: " + cromosomaToString(padre2) + " puntuacion: " + fitPadre2);
                System.out.println("Hijo1:  " + cromosomaToString(hijo1) + " puntuacion: " + fitHijo1);
                System.out.println("Hijo2:  " + cromosomaToString(hijo2) + " puntuacion: " + fitHijo2);
                System.out.println();
                hijo1 = aplicarMutacionConImpresion(hijo1, fitHijo1, n, "Hijo1");
                hijo2 = aplicarMutacionConImpresion(hijo2, fitHijo2, n, "Hijo2");
                String clave1 = cromosomaToKey(hijo1);
                String clave2 = cromosomaToKey(hijo2);
                if (!hijosGenerados.contains(clave1) && todosLosHijos.size() < cantidadHijos) {
                    todosLosHijos.add(hijo1);
                    hijosGenerados.add(clave1);
                    asignaciones++;
                }
                if (!hijosGenerados.contains(clave2) && todosLosHijos.size() < cantidadHijos) {
                    todosLosHijos.add(hijo2);
                    hijosGenerados.add(clave2);
                    asignaciones++;
                }
                if (todosLosHijos.size() == hijosPrevios) {
                    intentosSinProgreso++;
                } else {
                    intentosSinProgreso = 0; // resetear si hubo progreso
                }
            }
            List<ResultadoIndividuo> todosIndividuos = new ArrayList<>();
            asignaciones++; // +G
            System.out.println("--- Competencia: Padres vs Hijos ---");
            for (int i = 0; i < poblacion.size(); i++) {
                comparaciones++; // +G*P
                todosIndividuos.add(new ResultadoIndividuo(poblacion.get(i), fitnesses[i]));
                asignaciones++; // +G*P
            }
            for (List<Pieza> hijo : todosLosHijos) {
                int fitHijo = calcularFitness(hijo, n); // +G * H * O(N)
                todosIndividuos.add(new ResultadoIndividuo(hijo, fitHijo));
                asignaciones += 2; // +2*G*H
            }
            System.out.println("Total competidores: " + todosIndividuos.size() +
                    " (" + poblacion.size() + " padres + " + todosLosHijos.size() + " hijos)");
            Collections.sort(todosIndividuos, (a, b) -> b.fitness - a.fitness);
            asignaciones++; // +G
            poblacion.clear();
            Set<String> usados = new HashSet<>();
            for (int i = 0; i < todosIndividuos.size() && poblacion.size() < tamPoblacion; i++) { // O(P+H)
                comparaciones += 2; // +2*G*(P+H)
                String clave = cromosomaToKey(todosIndividuos.get(i).cromosoma); // +G*(P+H)*O(N)
                if (!usados.contains(clave)) {
                    poblacion.add(todosIndividuos.get(i).cromosoma);
                    usados.add(clave);
                    asignaciones++; // +G*P
                }
            }
            while (poblacion.size() < tamPoblacion) {
                List<Pieza> nuevo = new ArrayList<>(board.listaPiezas);
                Collections.shuffle(nuevo, random);
                String clave = cromosomaToKey(nuevo);
                if (!usados.contains(clave)) {
                    poblacion.add(nuevo);
                    usados.add(clave);
                }
            }
            fitnesses = new int[poblacion.size()];
            for (int i = 0; i < poblacion.size(); i++) {
                comparaciones++; // +G*P
                fitnesses[i] = calcularFitness(poblacion.get(i), n); // +G * P * O(N)
                asignaciones++; // +G*P
            }
            System.out.println("\nSobrevivientes de generacion " + gen + ":");
            for (int i = 0; i < poblacion.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + cromosomaToString(poblacion.get(i)) +
                        " fitness: " + fitnesses[i]);
            }
            int mejorFitGen = fitnesses[0];
            System.out
                    .println("\n>> Mejor fitness de generacion " + gen + ": " + mejorFitGen + " / " + fitnessObjetivo);
            System.out.println();
            comparaciones++; // +G
            if (mejorFitGen == fitnessObjetivo) {
                System.out.println("*** SOLUCION OPTIMA ENCONTRADA EN GENERACION " + gen + " ***");
                aplicarSolucion(board, poblacion.get(0));
                imprimirTop3(n);
                return true;
            }
        }
        if (!mejoresResultados.isEmpty()) {
            Collections.sort(mejoresResultados, (a, b) -> b.fitness - a.fitness);
            aplicarSolucion(board, mejoresResultados.get(0).cromosoma);
        }
        imprimirTop3(n);
        return board.checkTablero();
    }

    private String cromosomaToKey(List<Pieza> cromosoma) {
        StringBuilder sb = new StringBuilder();
        for (Pieza p : cromosoma) {
            sb.append(p.getUp()).append(",");
            sb.append(p.getRight()).append(",");
            sb.append(p.getDown()).append(",");
            sb.append(p.getLeft()).append(";");
        }
        return sb.toString();
    }

    private List<Pieza> aplicarMutacionConImpresion(List<Pieza> individuo, int fitnessAntes, int n, String nombre) {
        List<Pieza> mutado = new ArrayList<>(individuo);
        asignaciones++;
        int idx1 = random.nextInt(mutado.size());
        int idx2 = random.nextInt(mutado.size());
        asignaciones += 2;
        comparaciones++;
        if (idx1 != idx2) {
            Pieza temp = mutado.get(idx1);
            mutado.set(idx1, mutado.get(idx2));
            mutado.set(idx2, temp);
            asignaciones += 3;
        }
        int fitnessDespues = calcularFitness(mutado, n);
        asignaciones++;
        System.out.println("--- Mutacion de " + nombre + " ---");
        System.out.println("Individuo: " + cromosomaToString(individuo) + " puntuacion: " + fitnessAntes);
        System.out.println("Mutacion:  " + cromosomaToString(mutado) + " puntuacion: " + fitnessDespues);
        comparaciones++;
        if (fitnessDespues > fitnessAntes) {
            System.out.println(
                    ">> Mutacion ACEPTADA (mejora el fitness: " + fitnessAntes + " -> " + fitnessDespues + ")");
            System.out.println();
            return mutado;
        } else {
            System.out.println(">> Mutacion RECHAZADA (no mejora: " + fitnessAntes + " -> " + fitnessDespues + ")");
            System.out.println();
            return individuo;
        }
    }

    private void actualizarMejoresResultados(List<List<Pieza>> poblacion, int[] fitnesses) {
        for (int i = 0; i < poblacion.size(); i++) {
            comparaciones++;
            mejoresResultados.add(new ResultadoIndividuo(poblacion.get(i), fitnesses[i]));
            asignaciones++;
        }
        Collections.sort(mejoresResultados, (a, b) -> b.fitness - a.fitness);
        Set<String> vistos = new HashSet<>();
        List<ResultadoIndividuo> unicos = new ArrayList<>();
        for (ResultadoIndividuo r : mejoresResultados) {
            String clave = cromosomaToKey(r.cromosoma);
            if (!vistos.contains(clave) && unicos.size() < 10) {
                unicos.add(r);
                vistos.add(clave);
            }
        }
        mejoresResultados = unicos;
    }

    private void imprimirTop3(int n) {
        System.out.println();
        System.out.println("=".repeat(60));
        System.out.println("TOP 3 MEJORES RESULTADOS (despues de 10 generaciones)");
        System.out.println("=".repeat(60));
        Collections.sort(mejoresResultados, (a, b) -> b.fitness - a.fitness);
        int fitnessObjetivo = 2 * n * (n - 1);
        for (int i = 0; i < Math.min(3, mejoresResultados.size()); i++) {
            ResultadoIndividuo resultado = mejoresResultados.get(i);
            System.out.println();
            System.out.println("--- LUGAR " + (i + 1) + " ---");
            System.out.println("Puntuacion: " + resultado.fitness + " / " + fitnessObjetivo +
                    " (" + String.format("%.1f", 100.0 * resultado.fitness / fitnessObjetivo) + "%)");
            System.out.println("Cromosoma: " + cromosomaToString(resultado.cromosoma));
            System.out.println("Tablero:");
            imprimirTableroDesdecromosoma(resultado.cromosoma, n);
        }
    }

    private String cromosomaToString(List<Pieza> cromosoma) {
        if (cromosoma.size() <= 9) {
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < cromosoma.size(); i++) {
                if (i > 0)
                    sb.append(", ");
                Pieza p = cromosoma.get(i);
                sb.append("(" + p.getUp() + "," + p.getRight() + "," + p.getDown() + "," + p.getLeft() + ")");
            }
            sb.append("]");
            return sb.toString();
        } else {
            return "[" + cromosoma.size() + " piezas]";
        }
    }

    private void imprimirTableroDesdecromosoma(List<Pieza> cromosoma, int n) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int idx = i * n + j;
                Pieza p = cromosoma.get(idx);
                System.out.print("[" + p.getUp() + "," + p.getRight() + "," + p.getDown() + "," + p.getLeft() + "] ");
            }
            System.out.println();
        }
    }

    private int seleccionarMejor(int[] fitnesses) {
        int mejor = 0;
        for (int i = 1; i < fitnesses.length; i++) {
            comparaciones++;
            if (fitnesses[i] > fitnesses[mejor]) {
                mejor = i;
                asignaciones++;
            }
        }
        return mejor;
    }

    private int seleccionarSegundoMejor(int[] fitnesses, int excluir) {
        int mejor = (excluir == 0) ? 1 : 0;
        asignaciones++;

        for (int i = 0; i < fitnesses.length; i++) {
            comparaciones++;
            if (i != excluir && fitnesses[i] > fitnesses[mejor]) {
                mejor = i;
                asignaciones++;
            }
        }
        return mejor;
    }

    private List<List<Pieza>> inicializarPoblacionSinRepetidos(Tablero board, int tamPoblacion) {
        List<List<Pieza>> poblacion = new ArrayList<>();
        Set<String> usados = new HashSet<>();
        asignaciones += 2; // +2
        int intentos = 0;
        int maxIntentos = tamPoblacion * 100;
        while (poblacion.size() < tamPoblacion && intentos < maxIntentos) { // P iteraciones (en promedio)
            comparaciones++; // +P
            intentos++;
            List<Pieza> individuo = new ArrayList<>(board.listaPiezas); // +P (copia de N elementos = O(N))
            Collections.shuffle(individuo, random); // +P * O(N) para shuffle
            asignaciones++; // +P
            String clave = cromosomaToKey(individuo); // +P * O(N) para generar clave
            comparaciones++; // +P
            if (!usados.contains(clave)) { // O(1) amortizado para HashSet
                poblacion.add(individuo);
                usados.add(clave);
                asignaciones += 2; // +2P (peor caso)
            }
        }
        return poblacion;
    }

    private int calcularFitness(List<Pieza> individuo, int n) {
        int fitness = 0;
        asignaciones++; // +1
        for (int i = 0; i < n; i++) { // sqrt(N) iteraciones
            comparaciones++; // +sqrt(N)
            for (int j = 0; j < n; j++) { // sqrt(N) iteraciones -> N total
                comparaciones++; // +N
                int idx = i * n + j;
                Pieza actual = individuo.get(idx);
                asignaciones += 2; // +2N
                comparaciones++; // +N
                if (j < n - 1) {
                    Pieza derecha = individuo.get(idx + 1);
                    asignaciones++; // +N (peor caso)
                    comparaciones++; // +N
                    if (actual.getRight() == derecha.getLeft()) {
                        fitness++;
                        asignaciones++; // +N (peor caso)
                    }
                }
                comparaciones++; // +N
                if (i < n - 1) {
                    Pieza abajo = individuo.get(idx + n);
                    asignaciones++; // +N (peor caso)
                    comparaciones++; // +N
                    if (actual.getDown() == abajo.getUp()) {
                        fitness++;
                        asignaciones++; // +N (peor caso)
                    }
                }
            }
        }
        return fitness;
    }

    private List<List<Pieza>> crucePMX(List<Pieza> padre1, List<Pieza> padre2) {
        int n = padre1.size();
        asignaciones++; // +1
        int punto1 = random.nextInt(n);
        int punto2 = random.nextInt(n);
        asignaciones += 2; // +2
        comparaciones++; // +1
        if (punto1 > punto2) {
            int temp = punto1;
            punto1 = punto2;
            punto2 = temp;
            asignaciones += 3; // +3 (peor caso)
        }
        List<Pieza> hijo1 = crearHijoPMX(padre1, padre2, punto1, punto2); // +O(N^2)
        List<Pieza> hijo2 = crearHijoPMX(padre2, padre1, punto1, punto2); // +O(N^2)
        asignaciones += 2; // +2
        List<List<Pieza>> hijos = new ArrayList<>();
        hijos.add(hijo1);
        hijos.add(hijo2);
        asignaciones++; // +1
        return hijos;
    }

    private List<Pieza> crearHijoPMX(List<Pieza> padre1, List<Pieza> padre2, int punto1, int punto2) {
        int n = padre1.size();
        Pieza[] hijo = new Pieza[n];
        boolean[] usado = new boolean[n];
        asignaciones += 3; // +3
        for (int i = punto1; i <= punto2; i++) { // k iteraciones
            comparaciones++; // +k
            hijo[i] = padre1.get(i);
            asignaciones++; // +k
            for (int j = 0; j < n; j++) { // N iteraciones por cada k -> k*N total
                comparaciones++; // +k*N
                if (padre2.get(j) == hijo[i]) {
                    usado[j] = true;
                    asignaciones++; // +k (peor caso)
                    break;
                }
            }
        }
        int idxPadre2 = 0;
        asignaciones++; // +1
        for (int i = 0; i < n; i++) { // N iteraciones
            comparaciones++; // +N
            comparaciones++; // +N
            if (i >= punto1 && i <= punto2) {
                continue; // Ya fue llenado desde padre1
            }
            while (usado[idxPadre2]) { // O(N) en total para todo el for
                comparaciones++; // +N (amortizado)
                idxPadre2++;
                asignaciones++; // +N (amortizado)
            }
            comparaciones++; // +N
            hijo[i] = padre2.get(idxPadre2);
            usado[idxPadre2] = true;
            idxPadre2++;
            asignaciones += 3; // +3*(N-k)
        }
        List<Pieza> resultado = new ArrayList<>();
        asignaciones++; // +1
        for (Pieza p : hijo) { // N iteraciones
            resultado.add(p);
            asignaciones++; // +N
        }
        return resultado;
    }

    private void aplicarSolucion(Tablero board, List<Pieza> solucion) {
        int n = board.size;
        asignaciones++;
        for (int i = 0; i < n; i++) {
            comparaciones++;
            for (int j = 0; j < n; j++) {
                comparaciones++;
                int idx = i * n + j;
                board.setPieza(i, j, solucion.get(idx));
                asignaciones += 2;
            }
        }
    }
}