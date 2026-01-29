# Algoritmo Genético - Rompecabezas de Piezas Encajables

## 📋 Índice
1. [Introducción al Algoritmo Genético](#introducción-al-algoritmo-genético)
2. [Representación del Cromosoma](#representación-del-cromosoma)
3. [Función de Aptitud (Fitness)](#función-de-aptitud-fitness)
4. [Parámetros y Configuración](#parámetros-y-configuración)
5. [Flujo General del Algoritmo](#flujo-general-del-algoritmo)
6. [Inicialización de la Población](#inicialización-de-la-población)
7. [Selección de Padres](#selección-de-padres)
8. [Cruce PMX (Partially Mapped Crossover)](#cruce-pmx-partially-mapped-crossover)
9. [Mutación con Aceptación Condicional](#mutación-con-aceptación-condicional)
10. [Reemplazo por Competencia (Padres vs Hijos)](#reemplazo-por-competencia-padres-vs-hijos)
11. [Mantenimiento de los Mejores Resultados](#mantenimiento-de-los-mejores-resultados)
12. [Criterio de Parada](#criterio-de-parada)
13. [Análisis de Complejidad](#análisis-de-complejidad)
14. [Ventajas, Limitaciones y Mejoras](#ventajas-limitaciones-y-mejoras)

---

## 🧬 Introducción al Algoritmo Genético

Un **algoritmo genético** es una metaheurística inspirada en la evolución natural:

1. **Población** de soluciones (individuos)
2. **Selección** de los mejores
3. **Cruce** para combinar información
4. **Mutación** para introducir diversidad
5. **Repetir** por generaciones

El objetivo es **aproximar** una solución óptima sin explorar todo el espacio de búsqueda.

### Diferencias Clave con Otros Algoritmos

| Algoritmo | Estrategia | Garantía | Velocidad |
|-----------|------------|----------|-----------|
| Fuerza Bruta | Exhaustiva | Óptima | Muy lenta |
| Voraz | Local óptimo | No | Muy rápida |
| Genético | Evolutiva | No (pero buena aproximación) | Rápida / Media |

---

## 🧩 Representación del Cromosoma

Cada individuo representa **una permutación** de todas las piezas del tablero.

### Conceptos

- **Cromosoma**: Lista ordenada de piezas
- **Gen**: Una posición dentro del cromosoma
- **Alelo**: Cada lado de una pieza (up, right, down, left)

### Mapeo al Tablero

Para un tablero n×n, el cromosoma se asigna así:

```
Posición 0 → [0][0]
Posición 1 → [0][1]
...
Posición n-1 → [0][n-1]
Posición n → [1][0]
...
```

**Ejemplo 3×3**:
```
Cromosoma = [P0, P1, P2, P3, P4, P5, P6, P7, P8]

Tablero:
[P0] [P1] [P2]
[P3] [P4] [P5]
[P6] [P7] [P8]
```

---

## 🎯 Función de Aptitud (Fitness)

La función de fitness mide cuántas conexiones correctas existen entre piezas adyacentes.

### Conexiones válidas

- **Horizontal**: pieza[i].right == pieza[i+1].left
- **Vertical**: pieza[i].down == pieza[i+n].up

### Fórmula

Para tablero n×n:

- Total de conexiones horizontales posibles: n·(n-1)
- Total de conexiones verticales posibles: n·(n-1)

**Fitness máximo**:

$$
\text{fitness}_{max} = 2n(n-1)
$$

### Implementación

```java
private int calcularFitness(List<Pieza> individuo, int n) {
    int fitness = 0;

    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            int idx = i * n + j;
            Pieza actual = individuo.get(idx);

            // Conexión derecha
            if (j < n - 1) {
                Pieza derecha = individuo.get(idx + 1);
                if (actual.getRight() == derecha.getLeft()) {
                    fitness++;
                }
            }

            // Conexión abajo
            if (i < n - 1) {
                Pieza abajo = individuo.get(idx + n);
                if (actual.getDown() == abajo.getUp()) {
                    fitness++;
                }
            }
        }
    }

    return fitness;
}
```

---

## ⚙️ Parámetros y Configuración

El algoritmo usa valores definidos por una tabla en el PDF (según tamaño del tablero):

### Tamaño de Población Inicial

```java
private int obtenerPoblacionInicial(int size) {
    if (size <= 3) return 3;
    if (size <= 5) return 5;
    if (size <= 10) return 10;
    if (size <= 15) return 15;
    return 30;
}
```

### Cantidad de Hijos por Generación

```java
private int obtenerCantidadHijos(int size) {
    if (size <= 3) return 6;
    if (size <= 5) return 10;
    if (size <= 10) return 20;
    if (size <= 15) return 30;
    return 60;
}
```

### Generaciones

- **Siempre 10 generaciones** (requisito del proyecto)

---

## 🔁 Flujo General del Algoritmo

```
1. Inicializar población sin repetidos
2. Para cada generación (1..10):
   a. Evaluar fitness de la población
   b. Guardar los mejores
   c. Generar hijos mediante cruces
   d. Aplicar mutación (solo si mejora)
   e. Competencia: padres vs hijos
   f. Seleccionar los mejores para la nueva población
   g. Verificar si fitness óptimo
3. Mostrar top 3 resultados
```

---

## 🧪 Inicialización de la Población

### Requisito clave

- **No puede haber individuos repetidos**

### Implementación

```java
private List<List<Pieza>> inicializarPoblacionSinRepetidos(Tablero board, int tamPoblacion) {
    List<List<Pieza>> poblacion = new ArrayList<>();
    Set<String> usados = new HashSet<>();

    while (poblacion.size() < tamPoblacion) {
        List<Pieza> individuo = new ArrayList<>(board.listaPiezas);
        Collections.shuffle(individuo, random);

        String clave = cromosomaToKey(individuo);
        if (!usados.contains(clave)) {
            poblacion.add(individuo);
            usados.add(clave);
        }
    }

    return poblacion;
}
```

### Clave única del cromosoma

```java
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
```

---

## 🧠 Selección de Padres

Se seleccionan los **dos mejores individuos** de la población actual.

```java
private int seleccionarMejor(int[] fitnesses) {
    int mejor = 0;
    for (int i = 1; i < fitnesses.length; i++) {
        if (fitnesses[i] > fitnesses[mejor]) {
            mejor = i;
        }
    }
    return mejor;
}

private int seleccionarSegundoMejor(int[] fitnesses, int excluir) {
    int mejor = (excluir == 0) ? 1 : 0;
    for (int i = 0; i < fitnesses.length; i++) {
        if (i != excluir && fitnesses[i] > fitnesses[mejor]) {
            mejor = i;
        }
    }
    return mejor;
}
```

**Estrategia**: Selección determinista (elitismo directo)

---

## 🔀 Cruce PMX (Partially Mapped Crossover)

El cruce PMX garantiza:

- No repetir piezas
- No omitir piezas
- Mantener permutación válida

### Proceso

1. Elegir dos puntos de corte
2. Copiar segmento del padre 1 al hijo
3. Rellenar el resto con el orden del padre 2 sin repetir

### Implementación

```java
private List<List<Pieza>> crucePMX(List<Pieza> padre1, List<Pieza> padre2) {
    int n = padre1.size();

    int punto1 = random.nextInt(n);
    int punto2 = random.nextInt(n);

    if (punto1 > punto2) {
        int temp = punto1;
        punto1 = punto2;
        punto2 = temp;
    }

    List<Pieza> hijo1 = crearHijoPMX(padre1, padre2, punto1, punto2);
    List<Pieza> hijo2 = crearHijoPMX(padre2, padre1, punto1, punto2);

    List<List<Pieza>> hijos = new ArrayList<>();
    hijos.add(hijo1);
    hijos.add(hijo2);

    return hijos;
}
```

### Construcción del Hijo

```java
private List<Pieza> crearHijoPMX(List<Pieza> padre1, List<Pieza> padre2,
                                int punto1, int punto2) {
    int n = padre1.size();
    Pieza[] hijo = new Pieza[n];
    boolean[] usado = new boolean[n];

    // Copiar segmento
    for (int i = punto1; i <= punto2; i++) {
        hijo[i] = padre1.get(i);
        for (int j = 0; j < n; j++) {
            if (padre2.get(j) == hijo[i]) {
                usado[j] = true;
                break;
            }
        }
    }

    // Rellenar el resto
    int idxPadre2 = 0;
    for (int i = 0; i < n; i++) {
        if (i >= punto1 && i <= punto2) continue;

        while (usado[idxPadre2]) {
            idxPadre2++;
        }

        hijo[i] = padre2.get(idxPadre2);
        usado[idxPadre2] = true;
        idxPadre2++;
    }

    // Convertir a lista
    List<Pieza> resultado = new ArrayList<>();
    for (Pieza p : hijo) {
        resultado.add(p);
    }

    return resultado;
}
```

---

## 🔧 Mutación con Aceptación Condicional

La mutación consiste en **intercambiar dos genes** (swap).

### Regla estricta

**Solo se acepta si mejora el fitness**.

```java
private List<Pieza> aplicarMutacionConImpresion(List<Pieza> individuo,
        int fitnessAntes, int n, String nombre) {
    List<Pieza> mutado = new ArrayList<>(individuo);

    int idx1 = random.nextInt(mutado.size());
    int idx2 = random.nextInt(mutado.size());

    if (idx1 != idx2) {
        Pieza temp = mutado.get(idx1);
        mutado.set(idx1, mutado.get(idx2));
        mutado.set(idx2, temp);
    }

    int fitnessDespues = calcularFitness(mutado, n);

    if (fitnessDespues > fitnessAntes) {
        return mutado;  // ✅ Aceptar
    } else {
        return individuo;  // ❌ Rechazar
    }
}
```

**Consecuencia**: Mantiene calidad, pero puede reducir diversidad.

---

## 🏁 Reemplazo por Competencia (Padres vs Hijos)

Después de generar hijos, todos compiten:

1. **Padres + Hijos** en una lista
2. Ordenar por fitness
3. Seleccionar los mejores únicos

```java
List<ResultadoIndividuo> todosIndividuos = new ArrayList<>();

// Padres
for (int i = 0; i < poblacion.size(); i++) {
    todosIndividuos.add(new ResultadoIndividuo(poblacion.get(i), fitnesses[i]));
}

// Hijos
for (List<Pieza> hijo : todosLosHijos) {
    int fitHijo = calcularFitness(hijo, n);
    todosIndividuos.add(new ResultadoIndividuo(hijo, fitHijo));
}

Collections.sort(todosIndividuos, (a, b) -> b.fitness - a.fitness);

// Mantener mejores
poblacion.clear();
Set<String> usados = new HashSet<>();

for (int i = 0; i < todosIndividuos.size() && poblacion.size() < tamPoblacion; i++) {
    String clave = cromosomaToKey(todosIndividuos.get(i).cromosoma);
    if (!usados.contains(clave)) {
        poblacion.add(todosIndividuos.get(i).cromosoma);
        usados.add(clave);
    }
}
```

---

## 🥇 Mantenimiento de los Mejores Resultados

Se conserva un historial de los **10 mejores individuos** globales.

```java
private void actualizarMejoresResultados(List<List<Pieza>> poblacion, int[] fitnesses) {
    for (int i = 0; i < poblacion.size(); i++) {
        mejoresResultados.add(new ResultadoIndividuo(poblacion.get(i), fitnesses[i]));
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
```

Al final, se imprime el **Top 3**:

```java
private void imprimirTop3(int n) {
    Collections.sort(mejoresResultados, (a, b) -> b.fitness - a.fitness);

    for (int i = 0; i < Math.min(3, mejoresResultados.size()); i++) {
        ResultadoIndividuo resultado = mejoresResultados.get(i);
        System.out.println("--- LUGAR " + (i + 1) + " ---");
        System.out.println("Puntuacion: " + resultado.fitness);
        System.out.println("Cromosoma: " + cromosomaToString(resultado.cromosoma));
        System.out.println("Tablero:");
        imprimirTableroDesdecromosoma(resultado.cromosoma, n);
    }
}
```

---

## ⏹️ Criterio de Parada

El algoritmo termina cuando:

- **Se cumple el fitness óptimo**

$$
fitness = 2n(n-1)
$$

- **O se completan 10 generaciones**

```java
if (mejorFitGen == fitnessObjetivo) {
    // Solución óptima encontrada
    return true;
}
```

---

## 📊 Análisis de Complejidad

Sea:
- $N = n^2$ (número de piezas)
- $P$ = tamaño de población
- $H$ = número de hijos por generación
- $G = 10$ generaciones

### Cálculo de Fitness

Cada evaluación es $O(N)$ porque recorre el tablero completo.

### Por generación

- Evaluar población: $P · O(N)$
- Generar hijos: $H · (Cruce + Fitness + Mutación)$
  - Cruce PMX: $O(N)$
  - Fitness hijo: $O(N)$
  - Mutación: $O(N)$
- Competencia: $(P+H) log(P+H)$ (ordenamiento)

**Total aproximado**:

$$
T_{gen} = O((P + H) · N) + O((P+H) \log(P+H))
$$

**Total del algoritmo** (10 generaciones):

$$
T = 10 · T_{gen}
$$

### Complejidad Final

$$
T = O((P + H) · N)
$$

Como $P$ y $H$ están acotados por la tabla del PDF, el algoritmo escala casi linealmente con $N$.

---

## ✅ Ventajas, Limitaciones y Mejoras

### Ventajas

✅ Escalabilidad para tableros grandes
✅ Evita el crecimiento factorial
✅ Genera buenas aproximaciones
✅ Incluye elitismo y selección determinista

### Limitaciones

❌ No garantiza solución óptima
❌ Mutación estricta puede reducir diversidad
❌ Selección solo por mejores puede causar convergencia prematura

### Mejoras Potenciales

1. **Mutación probabilística** (aceptar aunque no mejore)
2. **Selección por torneo o ruleta** (más diversidad)
3. **Elitismo parcial** (conservar X mejores, pero no todos)
4. **Cruce alternativo** (OX, CX)
5. **Parada temprana por estancamiento**

---

## 🎓 Conclusión

El algoritmo genético ofrece un **balance entre calidad y eficiencia**. Aunque no garantiza el óptimo global, es una solución práctica para tableros grandes donde la fuerza bruta es imposible y el voraz puede fallar.

Este enfoque aprovecha:
- **Exploración** (mutación y cruce)
- **Explotación** (selección de mejores)
- **Control de diversidad** (sin repetidos)

Ideal como **metaheurística general** para problemas de permutación complejos.

---

**Documento generado para el proyecto de Algoritmos de Rompecabezas**  
*Implementación: Algoritmo Genético*  
*Fecha: 2026*
