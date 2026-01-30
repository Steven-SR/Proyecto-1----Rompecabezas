import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Algoritmo Genetico para resolver el rompecabezas de piezas encajables.
 * 
 * ============================================================================
 * REPRESENTACION DEL CROMOSOMA:
 * ============================================================================
 * - CROMOSOMA: Una permutacion de todas las piezas del tablero (ordenamiento)
 * - GEN: Cada posicion del cromosoma representa una pieza
 * - ALELOS: Cada pieza tiene 4 alelos (los 4 numeros de sus lados):
 * - up (arriba)
 * - right (derecha)
 * - down (abajo)
 * - left (izquierda)
 * 
 * Ejemplo para tablero 3x3:
 * Cromosoma = [Pieza0, Pieza1, Pieza2, Pieza3, Pieza4, Pieza5, Pieza6, Pieza7,
 * Pieza8]
 * Donde cada Pieza = (up, right, down, left)
 * 
 * El cromosoma se mapea al tablero asi:
 * Posicion 0 -> [0][0], Posicion 1 -> [0][1], Posicion 2 -> [0][2]
 * Posicion 3 -> [1][0], Posicion 4 -> [1][1], Posicion 5 -> [1][2]
 * Posicion 6 -> [2][0], Posicion 7 -> [2][1], Posicion 8 -> [2][2]
 * 
 * ============================================================================
 * FUNCION DE APTITUD (FITNESS):
 * ============================================================================
 * Evalua que tan bien ordenado esta el rompecabezas contando las conexiones
 * validas (lados que calzan) entre piezas adyacentes:
 * - Conexion horizontal: pieza[i].right == pieza[i+1].left
 * - Conexion vertical: pieza[i].down == pieza[i+n].up
 * 
 * Fitness maximo = 2 * n * (n-1) donde n = lado del tablero
 * 
 * ============================================================================
 * REQUISITOS DEL PROYECTO:
 * ============================================================================
 * - Poblacion inicial aleatoria SIN INDIVIDUOS REPETIDOS
 * - Cruce valido que NO REPITE NI OMITE piezas (PMX)
 * - 10 generaciones exactas
 * - Seleccion: padres e hijos compiten, quedan los mejores
 * - Imprime todos los cruces con puntuaciones
 * - Imprime mutaciones con puntuaciones
 * - Muestra los 3 mejores resultados al final
 */
public class Genetico {

    // Contadores de operaciones
    private long comparaciones;
    private long asignaciones;

    // Generador de numeros aleatorios
    private Random random;

    // Almacenar los mejores resultados
    private List<ResultadoIndividuo> mejoresResultados;

    /**
     * Clase para almacenar un individuo con su fitness
     */
    private static class ResultadoIndividuo {
        List<Pieza> cromosoma;
        int fitness;

        ResultadoIndividuo(List<Pieza> cromosoma, int fitness) {
            this.cromosoma = new ArrayList<>(cromosoma);
            this.fitness = fitness;
        }
    }

    /**
     * Constructor
     */
    public Genetico() {
        this.random = new Random();
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
     * Obtiene el numero de comparaciones realizadas
     */
    public long getComparaciones() {
        return comparaciones;
    }

    /**
     * Obtiene el numero de asignaciones realizadas
     */
    public long getAsignaciones() {
        return asignaciones;
    }

    /**
     * Obtiene la poblacion inicial segun el tamano del tablero
     * Basado en la tabla del PDF (rango 0..9)
     */
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

    /**
     * Obtiene la cantidad de hijos a generar segun el tamano del tablero
     * Basado en la tabla del PDF (rango 0..9)
     */
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

    /**
     * Resuelve el rompecabezas usando algoritmo genetico
     */
    public boolean solve(Tablero board) {
        resetearContadores();
        int n = board.size;
        int numPiezas = n * n;

        // Parametros segun la tabla del PDF
        int tamPoblacion = obtenerPoblacionInicial(n);
        int cantidadHijos = obtenerCantidadHijos(n);
        int numGeneraciones = 10; // Siempre 10 generaciones segun PDF

        // Fitness maximo posible
        int fitnessObjetivo = 2 * n * (n - 1);
        asignaciones += 5;

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

        // Inicializar poblacion SIN REPETIDOS
        List<List<Pieza>> poblacion = inicializarPoblacionSinRepetidos(board, tamPoblacion);
        mejoresResultados = new ArrayList<>();
        asignaciones += 2;

        System.out.println("--- POBLACION INICIAL (sin repetidos) ---");
        for (int i = 0; i < poblacion.size(); i++) {
            int fit = calcularFitness(poblacion.get(i), n);
            System.out
                    .println("Individuo " + (i + 1) + ": " + cromosomaToString(poblacion.get(i)) + " fitness: " + fit);
        }
        System.out.println();

        // Evolucion por 10 generaciones
        for (int gen = 1; gen <= numGeneraciones; gen++) {
            comparaciones++;

            System.out.println("=".repeat(60));
            System.out.println("GENERACION " + gen);
            System.out.println("=".repeat(60));
            System.out.println();

            // Evaluar poblacion actual
            int[] fitnesses = new int[poblacion.size()];
            asignaciones++;

            for (int i = 0; i < poblacion.size(); i++) {
                comparaciones++;
                fitnesses[i] = calcularFitness(poblacion.get(i), n);
                asignaciones++;
            }

            // Guardar mejores para el resultado final
            actualizarMejoresResultados(poblacion, fitnesses);

            // Lista para almacenar todos los hijos generados
            List<List<Pieza>> todosLosHijos = new ArrayList<>();
            Set<String> hijosGenerados = new HashSet<>(); // Para evitar hijos repetidos
            asignaciones += 2;

            // Generar la cantidad de hijos especificada
            int crucesRealizados = 0;
            int intentosSinProgreso = 0;
            int maxIntentosSinProgreso = cantidadHijos * 10; // limite para evitar bucle infinito
            asignaciones++;

            while (todosLosHijos.size() < cantidadHijos && intentosSinProgreso < maxIntentosSinProgreso) {
                comparaciones++;
                crucesRealizados++;
                int hijosPrevios = todosLosHijos.size();

                // Seleccionar padres (los mejores de la poblacion)
                int idxPadre1 = seleccionarMejor(fitnesses);
                int idxPadre2 = seleccionarSegundoMejor(fitnesses, idxPadre1);

                List<Pieza> padre1 = poblacion.get(idxPadre1);
                List<Pieza> padre2 = poblacion.get(idxPadre2);
                int fitPadre1 = fitnesses[idxPadre1];
                int fitPadre2 = fitnesses[idxPadre2];
                asignaciones += 4;

                // Realizar cruce PMX (no repite ni omite piezas)
                List<List<Pieza>> hijos = crucePMX(padre1, padre2);
                List<Pieza> hijo1 = hijos.get(0);
                List<Pieza> hijo2 = hijos.get(1);
                int fitHijo1 = calcularFitness(hijo1, n);
                int fitHijo2 = calcularFitness(hijo2, n);
                asignaciones += 5;

                // Imprimir informacion del cruce
                System.out.println("--- Cruce " + crucesRealizados + " ---");
                System.out.println("Padre1: " + cromosomaToString(padre1) + " puntuacion: " + fitPadre1);
                System.out.println("Padre2: " + cromosomaToString(padre2) + " puntuacion: " + fitPadre2);
                System.out.println("Hijo1:  " + cromosomaToString(hijo1) + " puntuacion: " + fitHijo1);
                System.out.println("Hijo2:  " + cromosomaToString(hijo2) + " puntuacion: " + fitHijo2);
                System.out.println();

                // Aplicar mutacion si mejora
                hijo1 = aplicarMutacionConImpresion(hijo1, fitHijo1, n, "Hijo1");
                hijo2 = aplicarMutacionConImpresion(hijo2, fitHijo2, n, "Hijo2");

                // Agregar hijos si no estan repetidos
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

                // Detectar si no hubo progreso (no se agregaron hijos nuevos)
                if (todosLosHijos.size() == hijosPrevios) {
                    intentosSinProgreso++;
                } else {
                    intentosSinProgreso = 0; // resetear si hubo progreso
                }
            }

            // Combinar poblacion actual con hijos (compiten entre si)
            List<ResultadoIndividuo> todosIndividuos = new ArrayList<>();
            asignaciones++;

            System.out.println("--- Competencia: Padres vs Hijos ---");

            // Agregar poblacion actual (padres)
            for (int i = 0; i < poblacion.size(); i++) {
                comparaciones++;
                todosIndividuos.add(new ResultadoIndividuo(poblacion.get(i), fitnesses[i]));
                asignaciones++;
            }

            // Agregar hijos
            for (List<Pieza> hijo : todosLosHijos) {
                int fitHijo = calcularFitness(hijo, n);
                todosIndividuos.add(new ResultadoIndividuo(hijo, fitHijo));
                asignaciones += 2;
            }

            System.out.println("Total competidores: " + todosIndividuos.size() +
                    " (" + poblacion.size() + " padres + " + todosLosHijos.size() + " hijos)");

            // Ordenar por fitness (mayor a menor) - Los mejores sobreviven
            Collections.sort(todosIndividuos, (a, b) -> b.fitness - a.fitness);
            asignaciones++;

            // Reemplazo: mantener solo los mejores (tamano de poblacion original)
            // Eliminando repetidos
            poblacion.clear();
            Set<String> usados = new HashSet<>();

            for (int i = 0; i < todosIndividuos.size() && poblacion.size() < tamPoblacion; i++) {
                comparaciones += 2;
                String clave = cromosomaToKey(todosIndividuos.get(i).cromosoma);
                if (!usados.contains(clave)) {
                    poblacion.add(todosIndividuos.get(i).cromosoma);
                    usados.add(clave);
                    asignaciones++;
                }
            }

            // Si no hay suficientes unicos, generar nuevos aleatorios
            while (poblacion.size() < tamPoblacion) {
                List<Pieza> nuevo = new ArrayList<>(board.listaPiezas);
                Collections.shuffle(nuevo, random);
                String clave = cromosomaToKey(nuevo);
                if (!usados.contains(clave)) {
                    poblacion.add(nuevo);
                    usados.add(clave);
                }
            }

            // Recalcular fitnesses
            fitnesses = new int[poblacion.size()];
            for (int i = 0; i < poblacion.size(); i++) {
                comparaciones++;
                fitnesses[i] = calcularFitness(poblacion.get(i), n);
                asignaciones++;
            }

            // Mostrar sobrevivientes
            System.out.println("\nSobrevivientes de generacion " + gen + ":");
            for (int i = 0; i < poblacion.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + cromosomaToString(poblacion.get(i)) +
                        " fitness: " + fitnesses[i]);
            }

            // Mostrar mejor de esta generacion
            int mejorFitGen = fitnesses[0];
            System.out
                    .println("\n>> Mejor fitness de generacion " + gen + ": " + mejorFitGen + " / " + fitnessObjetivo);
            System.out.println();

            // Verificar si encontramos solucion optima
            comparaciones++;
            if (mejorFitGen == fitnessObjetivo) {
                System.out.println("*** SOLUCION OPTIMA ENCONTRADA EN GENERACION " + gen + " ***");
                aplicarSolucion(board, poblacion.get(0));
                imprimirTop3(n);
                return true;
            }
        }

        // Aplicar la mejor solucion encontrada
        if (!mejoresResultados.isEmpty()) {
            Collections.sort(mejoresResultados, (a, b) -> b.fitness - a.fitness);
            aplicarSolucion(board, mejoresResultados.get(0).cromosoma);
        }

        // Imprimir los 3 mejores resultados
        imprimirTop3(n);

        return board.checkTablero();
    }

    /**
     * Genera una clave unica para un cromosoma (para detectar repetidos)
     */
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

    /**
     * Aplica mutacion e imprime el resultado.
     * Solo se aplica si MEJORA el fitness (segun requisito del PDF).
     */
    private List<Pieza> aplicarMutacionConImpresion(List<Pieza> individuo, int fitnessAntes, int n, String nombre) {
        // Crear copia para mutacion
        List<Pieza> mutado = new ArrayList<>(individuo);
        asignaciones++;

        // Aplicar mutacion (swap de dos posiciones)
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

        // Si mejora la puntuacion, aplicar la mutacion; si no, descartar
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

    /**
     * Actualiza la lista de mejores resultados encontrados
     */
    private void actualizarMejoresResultados(List<List<Pieza>> poblacion, int[] fitnesses) {
        for (int i = 0; i < poblacion.size(); i++) {
            comparaciones++;
            mejoresResultados.add(new ResultadoIndividuo(poblacion.get(i), fitnesses[i]));
            asignaciones++;
        }

        // Eliminar duplicados y mantener solo los mejores 10
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

    /**
     * Imprime los 3 mejores resultados al finalizar las 10 generaciones
     */
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

    /**
     * Convierte un cromosoma a string para impresion
     */
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
            // Para tableros grandes, mostrar solo resumen
            return "[" + cromosoma.size() + " piezas]";
        }
    }

    /**
     * Imprime un tablero a partir de un cromosoma
     */
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

    /**
     * Selecciona el indice del mejor individuo
     */
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

    /**
     * Selecciona el segundo mejor individuo (excluyendo uno)
     */
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

    /**
     * Inicializa la poblacion con permutaciones aleatorias SIN REPETIDOS.
     * Cumple con el requisito: "No puede existir poblacion repetida"
     */
    private List<List<Pieza>> inicializarPoblacionSinRepetidos(Tablero board, int tamPoblacion) {
        List<List<Pieza>> poblacion = new ArrayList<>();
        Set<String> usados = new HashSet<>();
        asignaciones += 2;

        int intentos = 0;
        int maxIntentos = tamPoblacion * 100;

        while (poblacion.size() < tamPoblacion && intentos < maxIntentos) {
            comparaciones++;
            intentos++;

            List<Pieza> individuo = new ArrayList<>(board.listaPiezas);
            Collections.shuffle(individuo, random);
            asignaciones++;

            String clave = cromosomaToKey(individuo);
            comparaciones++;
            if (!usados.contains(clave)) {
                poblacion.add(individuo);
                usados.add(clave);
                asignaciones += 2;
            }
        }

        return poblacion;
    }

    /**
     * Calcula el fitness de un individuo (permutacion de piezas).
     * 
     * FUNCION DE APTITUD: Cuenta los lados que calzan entre piezas adyacentes.
     * - Conexion horizontal valida: pieza.right == vecino.left
     * - Conexion vertical valida: pieza.down == vecino.up
     * 
     * @return Numero de conexiones validas (0 a 2*n*(n-1))
     */
    private int calcularFitness(List<Pieza> individuo, int n) {
        int fitness = 0;
        asignaciones++;

        for (int i = 0; i < n; i++) {
            comparaciones++;
            for (int j = 0; j < n; j++) {
                comparaciones++;

                int idx = i * n + j;
                Pieza actual = individuo.get(idx);
                asignaciones += 2;

                // Verificar conexion con pieza de la derecha (lados que calzan)
                comparaciones++;
                if (j < n - 1) {
                    Pieza derecha = individuo.get(idx + 1);
                    asignaciones++;
                    comparaciones++;
                    if (actual.getRight() == derecha.getLeft()) {
                        fitness++;
                        asignaciones++;
                    }
                }

                // Verificar conexion con pieza de abajo (lados que calzan)
                comparaciones++;
                if (i < n - 1) {
                    Pieza abajo = individuo.get(idx + n);
                    asignaciones++;
                    comparaciones++;
                    if (actual.getDown() == abajo.getUp()) {
                        fitness++;
                        asignaciones++;
                    }
                }
            }
        }

        return fitness;
    }

    /**
     * Cruce PMX (Partially Mapped Crossover) para permutaciones.
     * 
     * GARANTIZA que no se repiten ni omiten piezas en los hijos.
     * Este cruce es especifico para problemas de permutacion.
     */
    private List<List<Pieza>> crucePMX(List<Pieza> padre1, List<Pieza> padre2) {
        int n = padre1.size();
        asignaciones++;

        // Seleccionar dos puntos de corte
        int punto1 = random.nextInt(n);
        int punto2 = random.nextInt(n);
        asignaciones += 2;

        comparaciones++;
        if (punto1 > punto2) {
            int temp = punto1;
            punto1 = punto2;
            punto2 = temp;
            asignaciones += 3;
        }

        List<Pieza> hijo1 = crearHijoPMX(padre1, padre2, punto1, punto2);
        List<Pieza> hijo2 = crearHijoPMX(padre2, padre1, punto1, punto2);
        asignaciones += 2;

        List<List<Pieza>> hijos = new ArrayList<>();
        hijos.add(hijo1);
        hijos.add(hijo2);
        asignaciones++;

        return hijos;
    }

    /**
     * Crea un hijo usando el cruce PMX.
     * Copia un segmento del padre1 y rellena el resto con piezas del padre2
     * en orden, asegurando que no se repitan.
     */
    private List<Pieza> crearHijoPMX(List<Pieza> padre1, List<Pieza> padre2, int punto1, int punto2) {
        int n = padre1.size();
        Pieza[] hijo = new Pieza[n];
        boolean[] usado = new boolean[n];
        asignaciones += 3;

        // Copiar segmento del padre1
        for (int i = punto1; i <= punto2; i++) {
            comparaciones++;
            hijo[i] = padre1.get(i);
            asignaciones++;
            // Marcar la pieza como usada
            for (int j = 0; j < n; j++) {
                comparaciones++;
                if (padre2.get(j) == hijo[i]) {
                    usado[j] = true;
                    asignaciones++;
                    break;
                }
            }
        }

        // Llenar el resto con piezas del padre2 en orden (sin repetir)
        int idxPadre2 = 0;
        asignaciones++;
        for (int i = 0; i < n; i++) {
            comparaciones++;
            comparaciones++;
            if (i >= punto1 && i <= punto2) {
                continue; // Ya fue llenado desde padre1
            }

            // Buscar siguiente pieza no usada del padre2
            while (usado[idxPadre2]) {
                comparaciones++;
                idxPadre2++;
                asignaciones++;
            }
            comparaciones++;

            hijo[i] = padre2.get(idxPadre2);
            usado[idxPadre2] = true;
            idxPadre2++;
            asignaciones += 3;
        }

        // Convertir array a lista
        List<Pieza> resultado = new ArrayList<>();
        asignaciones++;
        for (Pieza p : hijo) {
            resultado.add(p);
            asignaciones++;
        }

        return resultado;
    }

    /**
     * Aplica una solucion (permutacion de piezas) al tablero
     */
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
