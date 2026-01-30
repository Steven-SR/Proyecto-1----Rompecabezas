import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Clase principal para ejecutar y comparar todos los algoritmos de resolucion
 * del rompecabezas de piezas encajables.
 * 
 * Algoritmos implementados:
 * 1. Fuerza Bruta (Backtracking puro)
 * 2. Voraz sin Backtracking
 * 3. Voraz con Backtracking
 * 4. Algoritmo Genetico
 * 
 * Genera un archivo de resumen al finalizar: resultados.txt
 */
public class Main {

    // ========================================================================
    // CONFIGURACION - Modifica estas variables segun tus necesidades
    // ========================================================================

    /** Habilitar pruebas de puzzles grandes (60x60, 100x100) */
    static final boolean ENABLE_LARGE_PUZZLES = true;

    /** Tiempo maximo de ejecucion por prueba (en segundos) */
    static final int TIMEOUT_SECONDS = 60;

    /** Habilitar pruebas de Fuerza Bruta (muy lento para puzzles grandes) */
    static final boolean TEST_FUERZA_BRUTA = true;

    /** Habilitar pruebas de Voraz SIN backtracking */
    static final boolean TEST_VORAZ_SIN_BACKTRACKING = true;

    /** Habilitar pruebas de Voraz CON backtracking */
    static final boolean TEST_VORAZ_CON_BACKTRACKING = true;

    /** Habilitar pruebas de Algoritmo Genetico */
    static final boolean TEST_GENETICO = true;

    /** Tamanos de tablero a probar */
    static final int[] SIZES_SMALL = { 3, 5, 10, 15, 30 };
    static final int[] SIZES_LARGE = { 60, 100 };

    /** Rangos de numeros segun el PDF del proyecto */
    static final int[] RANGOS = { 9, 15 };

    /** Tamanos maximos recomendados para cada algoritmo */
    static final int MAX_SIZE_FUERZA_BRUTA = 5; // O(N*N!) es muy lento
    static final int MAX_SIZE_VORAZ_BACKTRACK = 5; // Con backtracking puede ser lento

    // ========================================================================
    // ALMACENAMIENTO DE RESULTADOS
    // ========================================================================

    /** Lista de resultados para el archivo de resumen */
    static List<ResultadoPrueba> resultados = new ArrayList<>();

    /**
     * Clase para almacenar los resultados de cada prueba
     */
    static class ResultadoPrueba {
        String algoritmo;
        int tamano;
        int rango;
        double tiempoMs;
        long comparaciones;
        long asignaciones;
        long memoriaBytes;
        String estado; // RESUELTO, NO_RESUELTO, TIMEOUT, ERROR

        ResultadoPrueba(String algoritmo, int tamano, int rango) {
            this.algoritmo = algoritmo;
            this.tamano = tamano;
            this.rango = rango;
            this.estado = "PENDIENTE";
        }
    }

    // ========================================================================
    // METODO PRINCIPAL
    // ========================================================================

    public static void main(String[] args) {
        System.out.println("=====================================================================");
        System.out.println("     PROYECTO: ANALISIS DE ALGORITMOS - ROMPECABEZAS                ");
        System.out.println("     Profesora: Ana Lorena Valerio Solis                            ");
        System.out.println("=====================================================================");
        System.out.println();
        System.out.println("Configuracion actual:");
        System.out.println("  - Timeout por prueba: " + TIMEOUT_SECONDS + " segundos");
        System.out.println("  - Puzzles grandes (60x60, 100x100): " + (ENABLE_LARGE_PUZZLES ? "SI" : "NO"));
        System.out.println("  - Fuerza Bruta: "
                + (TEST_FUERZA_BRUTA ? "SI (max " + MAX_SIZE_FUERZA_BRUTA + "x" + MAX_SIZE_FUERZA_BRUTA + ")" : "NO"));
        System.out.println("  - Voraz sin backtracking: " + (TEST_VORAZ_SIN_BACKTRACKING ? "SI" : "NO"));
        System.out.println("  - Voraz con backtracking: " + (TEST_VORAZ_CON_BACKTRACKING ? "SI" : "NO"));
        System.out.println("  - Genetico: " + (TEST_GENETICO ? "SI" : "NO"));
        System.out.println();

        // Construir lista de tamanos a probar
        int[] sizes = buildSizeArray();

        // ====================================================================
        // FUERZA BRUTA
        // ====================================================================
        if (TEST_FUERZA_BRUTA) {
            System.out.println("\n");
            System.out.println("#####################################################################");
            System.out.println("#                    ALGORITMO: FUERZA BRUTA                        #");
            System.out.println("#####################################################################");

            for (int rango : RANGOS) {
                System.out.println("\n--- Rango de valores: 0.." + rango + " ---");
                for (int size : sizes) {
                    if (size <= MAX_SIZE_FUERZA_BRUTA) {
                        ejecutarPruebaFuerzaBruta(size, rango);
                    } else {
                        System.out.println("\n[OMITIDO] Tablero " + size + "x" + size +
                                " - Fuerza bruta no es viable para este tamano");
                        ResultadoPrueba r = new ResultadoPrueba("Fuerza Bruta", size, rango);
                        r.estado = "OMITIDO";
                        r.tiempoMs = -1;
                        resultados.add(r);
                    }
                }
            }
        }

        // ====================================================================
        // VORAZ SIN BACKTRACKING
        // ====================================================================
        if (TEST_VORAZ_SIN_BACKTRACKING) {
            System.out.println("\n");
            System.out.println("#####################################################################");
            System.out.println("#               ALGORITMO: VORAZ (SIN BACKTRACKING)                 #");
            System.out.println("#####################################################################");

            for (int rango : RANGOS) {
                System.out.println("\n--- Rango de valores: 0.." + rango + " ---");
                for (int size : sizes) {
                    ejecutarPruebaVoraz(size, rango, false);
                }
            }
        }

        // ====================================================================
        // VORAZ CON BACKTRACKING
        // ====================================================================
        if (TEST_VORAZ_CON_BACKTRACKING) {
            System.out.println("\n");
            System.out.println("#####################################################################");
            System.out.println("#               ALGORITMO: VORAZ (CON BACKTRACKING)                 #");
            System.out.println("#####################################################################");

            for (int rango : RANGOS) {
                System.out.println("\n--- Rango de valores: 0.." + rango + " ---");
                for (int size : sizes) {
                    if (size <= MAX_SIZE_VORAZ_BACKTRACK) {
                        ejecutarPruebaVoraz(size, rango, true);
                    } else {
                        ejecutarPruebaConTimeout("Voraz+Backtrack", size, rango,
                                () -> ejecutarVorazInterno(size, rango, true));
                    }
                }
            }
        }

        // ====================================================================
        // ALGORITMO GENETICO
        // ====================================================================
        if (TEST_GENETICO) {
            System.out.println("\n");
            System.out.println("#####################################################################");
            System.out.println("#                   ALGORITMO: GENETICO                             #");
            System.out.println("#####################################################################");

            for (int rango : RANGOS) {
                System.out.println("\n--- Rango de valores: 0.." + rango + " ---");
                for (int size : sizes) {
                    ejecutarPruebaGenetico(size, rango);
                }
            }
        }

        // ====================================================================
        // GENERAR ARCHIVO DE RESUMEN
        // ====================================================================
        generarArchivoResumen();

        System.out.println("\n");
        System.out.println("=====================================================================");
        System.out.println("                    EJECUCION COMPLETADA                             ");
        System.out.println("=====================================================================");
        System.out.println("Archivo de resumen generado: resultados.txt");
    }

    // ========================================================================
    // METODOS DE EJECUCION DE PRUEBAS
    // ========================================================================

    /**
     * Construye el array de tamanos segun la configuracion
     */
    private static int[] buildSizeArray() {
        if (ENABLE_LARGE_PUZZLES) {
            int[] combined = new int[SIZES_SMALL.length + SIZES_LARGE.length];
            System.arraycopy(SIZES_SMALL, 0, combined, 0, SIZES_SMALL.length);
            System.arraycopy(SIZES_LARGE, 0, combined, SIZES_SMALL.length, SIZES_LARGE.length);
            return combined;
        }
        return SIZES_SMALL;
    }

    /**
     * Ejecuta una prueba con timeout usando ExecutorService
     */
    private static void ejecutarPruebaConTimeout(String nombreAlgoritmo, int size, int rango,
            Callable<ResultadoPrueba> tarea) {

        System.out.println("\n=== " + nombreAlgoritmo + " - Tablero " + size + "x" + size +
                " (rango 0.." + rango + ") ===");
        System.out.println("[Ejecutando con timeout de " + TIMEOUT_SECONDS + " segundos...]");

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<ResultadoPrueba> future = executor.submit(tarea);

        try {
            ResultadoPrueba resultado = future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            resultados.add(resultado);
            imprimirResultado(resultado);
        } catch (TimeoutException e) {
            future.cancel(true);
            System.out.println("[TIMEOUT] La prueba excedio " + TIMEOUT_SECONDS + " segundos");
            ResultadoPrueba r = new ResultadoPrueba(nombreAlgoritmo, size, rango);
            r.estado = "TIMEOUT";
            r.tiempoMs = TIMEOUT_SECONDS * 1000.0;
            resultados.add(r);
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
            ResultadoPrueba r = new ResultadoPrueba(nombreAlgoritmo, size, rango);
            r.estado = "ERROR";
            resultados.add(r);
        } finally {
            executor.shutdownNow();
        }
    }

    /**
     * Ejecuta prueba de Fuerza Bruta
     */
    private static void ejecutarPruebaFuerzaBruta(int size, int rango) {
        System.out.println("\n=== Fuerza Bruta - Tablero " + size + "x" + size +
                " (rango 0.." + rango + ") ===");

        ResultadoPrueba resultado = new ResultadoPrueba("Fuerza Bruta", size, rango);

        try {
            // Preparacion para medicion de memoria
            System.gc();
            Runtime runtime = Runtime.getRuntime();
            long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

            // Crear y preparar tablero
            Tablero tablero = new Tablero(size, rango);
            tablero.createTablero();
            tablero.scrambleTablero();

            // Resolver
            FuerzaBruta solver = new FuerzaBruta();
            long startTime = System.nanoTime();
            boolean solved = solver.solve(tablero);
            long endTime = System.nanoTime();

            // Medicion de memoria
            long memoryAfter = runtime.totalMemory() - runtime.freeMemory();

            // Guardar resultados
            resultado.tiempoMs = (endTime - startTime) / 1_000_000.0;
            resultado.comparaciones = solver.getComparaciones();
            resultado.asignaciones = solver.getAsignaciones();
            resultado.memoriaBytes = Math.max(0, memoryAfter - memoryBefore);
            resultado.estado = solved ? "RESUELTO" : "NO_RESUELTO";

            imprimirResultado(resultado);

        } catch (Exception e) {
            resultado.estado = "ERROR";
            System.out.println("[ERROR] " + e.getMessage());
        }

        resultados.add(resultado);
    }

    /**
     * Ejecuta prueba de Voraz (con o sin backtracking)
     */
    private static void ejecutarPruebaVoraz(int size, int rango, boolean conBacktracking) {
        String nombre = conBacktracking ? "Voraz+Backtrack" : "Voraz";
        System.out.println("\n=== " + nombre + " - Tablero " + size + "x" + size +
                " (rango 0.." + rango + ") ===");

        ResultadoPrueba resultado = ejecutarVorazInterno(size, rango, conBacktracking);
        imprimirResultado(resultado);
        resultados.add(resultado);
    }

    /**
     * Metodo interno para ejecutar Voraz (usado tambien con timeout)
     */
    private static ResultadoPrueba ejecutarVorazInterno(int size, int rango, boolean conBacktracking) {
        String nombre = conBacktracking ? "Voraz+Backtrack" : "Voraz";
        ResultadoPrueba resultado = new ResultadoPrueba(nombre, size, rango);

        try {
            // Preparacion para medicion de memoria
            System.gc();
            Runtime runtime = Runtime.getRuntime();
            long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

            // Crear y preparar tablero
            Tablero tablero = new Tablero(size, rango);
            tablero.createTablero();
            tablero.scrambleTablero();

            // Resolver
            Voraz solver = new Voraz(conBacktracking);
            long startTime = System.nanoTime();
            boolean solved = solver.solve(tablero);
            long endTime = System.nanoTime();

            // Medicion de memoria
            long memoryAfter = runtime.totalMemory() - runtime.freeMemory();

            // Guardar resultados
            resultado.tiempoMs = (endTime - startTime) / 1_000_000.0;
            resultado.comparaciones = solver.getComparaciones();
            resultado.asignaciones = solver.getAsignaciones();
            resultado.memoriaBytes = Math.max(0, memoryAfter - memoryBefore);
            resultado.estado = solved ? "RESUELTO" : "NO_RESUELTO";

        } catch (Exception e) {
            resultado.estado = "ERROR";
        }

        return resultado;
    }

    /**
     * Ejecuta prueba de Algoritmo Genetico
     */
    private static void ejecutarPruebaGenetico(int size, int rango) {
        System.out.println("\n=== Genetico - Tablero " + size + "x" + size +
                " (rango 0.." + rango + ") ===");

        // Para puzzles grandes, usar timeout
        if (size > 30) {
            ejecutarPruebaConTimeout("Genetico", size, rango,
                    () -> ejecutarGeneticoInterno(size, rango));
            return;
        }

        ResultadoPrueba resultado = ejecutarGeneticoInterno(size, rango);
        imprimirResultado(resultado);
        resultados.add(resultado);
    }

    /**
     * Metodo interno para ejecutar Genetico (usado tambien con timeout)
     */
    private static ResultadoPrueba ejecutarGeneticoInterno(int size, int rango) {
        ResultadoPrueba resultado = new ResultadoPrueba("Genetico", size, rango);

        try {
            // Preparacion para medicion de memoria
            System.gc();
            Runtime runtime = Runtime.getRuntime();
            long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

            // Crear y preparar tablero
            Tablero tablero = new Tablero(size, rango);
            tablero.createTablero();
            tablero.scrambleTablero();

            // Resolver (suprimir salida del genetico para el main)
            Genetico solver = new Genetico();

            // Redirigir stdout temporalmente para evitar mucha salida
            java.io.PrintStream originalOut = System.out;
            System.setOut(new java.io.PrintStream(new java.io.OutputStream() {
                public void write(int b) {
                }
            }));

            long startTime = System.nanoTime();
            boolean solved = solver.solve(tablero);
            long endTime = System.nanoTime();

            // Restaurar stdout
            System.setOut(originalOut);

            // Medicion de memoria
            long memoryAfter = runtime.totalMemory() - runtime.freeMemory();

            // Guardar resultados
            resultado.tiempoMs = (endTime - startTime) / 1_000_000.0;
            resultado.comparaciones = solver.getComparaciones();
            resultado.asignaciones = solver.getAsignaciones();
            resultado.memoriaBytes = Math.max(0, memoryAfter - memoryBefore);
            resultado.estado = solved ? "RESUELTO" : "NO_RESUELTO";

        } catch (Exception e) {
            resultado.estado = "ERROR";
        }

        return resultado;
    }

    /**
     * Imprime el resultado de una prueba
     */
    private static void imprimirResultado(ResultadoPrueba r) {
        System.out.println("  Estado: " + r.estado);
        if (r.tiempoMs >= 0) {
            System.out.println(String.format("  Tiempo: %.3f ms", r.tiempoMs));
            System.out.println("  Comparaciones: " + r.comparaciones);
            System.out.println("  Asignaciones: " + r.asignaciones);
            System.out.println("  Operaciones totales: " + (r.comparaciones + r.asignaciones));
            System.out.println(String.format("  Memoria: %.2f KB", r.memoriaBytes / 1024.0));
        }
    }

    // ========================================================================
    // GENERACION DE ARCHIVO DE RESUMEN
    // ========================================================================

    /**
     * Genera el archivo resultados.txt con el resumen de todas las pruebas
     */
    private static void generarArchivoResumen() {
        String filename = "resultados.txt";

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Encabezado
            writer.println("================================================================================");
            writer.println("                    RESUMEN DE PRUEBAS - ROMPECABEZAS                           ");
            writer.println("================================================================================");
            writer.println();
            writer.println("Fecha de ejecucion: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            writer.println("Timeout configurado: " + TIMEOUT_SECONDS + " segundos");
            writer.println();

            // Agrupar por algoritmo
            String[] algoritmos = { "Fuerza Bruta", "Voraz", "Voraz+Backtrack", "Genetico" };

            for (String algo : algoritmos) {
                List<ResultadoPrueba> resultadosAlgo = new ArrayList<>();
                for (ResultadoPrueba r : resultados) {
                    if (r.algoritmo.equals(algo)) {
                        resultadosAlgo.add(r);
                    }
                }

                if (resultadosAlgo.isEmpty())
                    continue;

                writer.println();
                writer.println("--------------------------------------------------------------------------------");
                writer.println("ALGORITMO: " + algo);
                writer.println("--------------------------------------------------------------------------------");
                writer.println();
                writer.println(String.format("%-8s | %-6s | %-12s | %-14s | %-14s | %-12s | %-12s",
                        "Tamano", "Rango", "Tiempo(ms)", "Comparaciones", "Asignaciones", "Memoria(KB)", "Estado"));
                writer.println("-".repeat(95));

                for (ResultadoPrueba r : resultadosAlgo) {
                    if (r.tiempoMs >= 0) {
                        writer.println(String.format("%-8s | %-6s | %12.3f | %14d | %14d | %12.2f | %-12s",
                                r.tamano + "x" + r.tamano,
                                "0-" + r.rango,
                                r.tiempoMs,
                                r.comparaciones,
                                r.asignaciones,
                                r.memoriaBytes / 1024.0,
                                r.estado));
                    } else {
                        writer.println(String.format("%-8s | %-6s | %12s | %14s | %14s | %12s | %-12s",
                                r.tamano + "x" + r.tamano,
                                "0-" + r.rango,
                                "-",
                                "-",
                                "-",
                                "-",
                                r.estado));
                    }
                }
            }

            // Resumen estadistico
            writer.println();
            writer.println();
            writer.println("================================================================================");
            writer.println("                           RESUMEN ESTADISTICO                                  ");
            writer.println("================================================================================");
            writer.println();

            for (String algo : algoritmos) {
                int resueltos = 0;
                int noResueltos = 0;
                int timeouts = 0;
                int omitidos = 0;
                int errores = 0;
                double tiempoTotal = 0;

                for (ResultadoPrueba r : resultados) {
                    if (r.algoritmo.equals(algo)) {
                        switch (r.estado) {
                            case "RESUELTO":
                                resueltos++;
                                break;
                            case "NO_RESUELTO":
                                noResueltos++;
                                break;
                            case "TIMEOUT":
                                timeouts++;
                                break;
                            case "OMITIDO":
                                omitidos++;
                                break;
                            case "ERROR":
                                errores++;
                                break;
                        }
                        if (r.tiempoMs > 0)
                            tiempoTotal += r.tiempoMs;
                    }
                }

                int total = resueltos + noResueltos + timeouts + omitidos + errores;
                if (total == 0)
                    continue;

                writer.println(algo + ":");
                writer.println("  - Resueltos: " + resueltos + "/" + total);
                writer.println("  - No resueltos: " + noResueltos);
                writer.println("  - Timeouts: " + timeouts);
                writer.println("  - Omitidos: " + omitidos);
                writer.println("  - Errores: " + errores);
                writer.println(String.format("  - Tiempo total: %.3f ms", tiempoTotal));
                writer.println();
            }

            writer.println();
            writer.println("================================================================================");
            writer.println("                              FIN DEL REPORTE                                   ");
            writer.println("================================================================================");

            System.out.println("\n[OK] Archivo de resumen generado: " + filename);

        } catch (IOException e) {
            System.out.println("[ERROR] No se pudo generar el archivo de resumen: " + e.getMessage());
        }
    }
}
