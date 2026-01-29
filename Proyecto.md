# Proyecto: Análisis de Algoritmos - Análisis de Complejidad Algorítmica
[cite_start]**Profesora:** Ana Lorena Valerio Solís [cite: 1]
[cite_start]**Lenguaje:** Java (Paradigma Orientado a Objetos) [cite: 5, 231]

---

## 1. Objetivos del Proyecto
* [cite_start]Implementar un algoritmo NP de forma eficiente[cite: 5].
* [cite_start]Analizar algoritmos mediante mediciones empíricas y analíticas[cite: 6].
* [cite_start]Desarrollar responsabilidad individual, comunicación y trabajo en equipo[cite: 9].

---

## 2. Definición del Problema: El Rompecabezas
[cite_start]El problema consiste en armar un rompecabezas cuadrado donde los bordes numéricos de piezas adyacentes deben coincidir[cite: 13, 23].

### Especificaciones de las Piezas
* [cite_start]**Composición:** Cada pieza tiene 4 valores numéricos leídos en dirección de las manecillas del reloj[cite: 19].
* [cite_start]**Rango Numérico:** Dos alternativas para análisis: 0 a 9 y 0 a 15[cite: 20].
* [cite_start]**Creación:** Deben ser aleatorias, verificando siempre que exista solución[cite: 15].
* [cite_start]**Excepción:** Para el tamaño $3\times3$, las piezas son fijas ("quemadas en el código")[cite: 17].
* [cite_start]**Estado inicial:** Las piezas inician en una estructura desordenada[cite: 24].

### Tamaños a Evaluar (Todos cuadrados)
[cite_start]$3\times3$, $5\times5$, $10\times10$, $15\times15$, $30\times30$, $60\times60$, $100\times100$[cite: 22].

---

## 3. Estrategias de Resolución (Algoritmos)
[cite_start]Se deben programar tres algoritmos para comparar su rendimiento[cite: 72]:

1. [cite_start]**Fuerza Bruta (Backtracking):** Realiza todas las combinaciones posibles hasta encontrar la solución[cite: 73].
2. [cite_start]**Avance Rápido (Heurística):** * Toma decisiones óptimas locales[cite: 74].
    * [cite_start]Selecciona la posición donde menos piezas encajen (más restrictiva)[cite: 74].
    * [cite_start]Debe permitir "vuelta atrás" (backtracking) ante decisiones incorrectas[cite: 75].
    * [cite_start]Se pueden agrupar piezas comunes previamente[cite: 76].
3. **Algoritmo Genético:**
    * [cite_start]**Población Inicial:** Aleatoria, sin repetidos, cumpliendo restricciones[cite: 81].
    * [cite_start]**Cromosoma:** Compuesto por genes (piezas) y alelos (los 4 números)[cite: 83].
    * [cite_start]**Función Aptitud (Fitness):** Evalúa qué tan bien calzan los lados de las piezas[cite: 88, 90].
    * [cite_start]**Cruce:** Selección de dos padres para generar dos hijos válidos[cite: 92, 93].
    * [cite_start]**Mutación:** Se aplica en caso de poblaciones repetidas; se mantiene si mejora la puntuación y se descarta si empeora[cite: 98, 99].
    * [cite_start]**Ciclo:** 10 generaciones para todos los tamaños[cite: 103].

### Tabla de Configuración Genética (0..9 y 0..15)
| Tamaño | Población Inicial | Cantidad de Hijos |
| :--- | :--- | :--- |
| $3\times3$ | 3 | 6 |
| $5\times5$ | 5 | 10 |
| $10\times10$ | 10 | 20 |
| $15\times15$ | 15 | 30 |
| $30\times30$ | 30 | 60 |
| $60\times60$ | 30 | 60 |
| $100\times100$ | 30 | 60 |
[cite_start]*(Datos según fuentes [cite: 95, 96])*

---

## 4. Requerimientos de Salida y Consultas
El programa debe imprimir en consola:
* [cite_start]Respuesta encontrada o solución parcial[cite: 112, 116].
* [cite_start]Variables de medición: Memoria, tiempo (3 decimales), asignaciones, comparaciones y total de instrucciones[cite: 117, 140].
* [cite_start]**Específicos Genético:** Cruces realizados (Padre 1, Padre 2, Hijo 1, Hijo 2 con sus puntajes), mutaciones aplicadas y las 3 mejores poblaciones finales [cite: 118, 120-134].
* [cite_start]**Específicos Fuerza Bruta:** Cantidad de alternativas evaluadas y cantidad de podas realizadas[cite: 135, 136].

---

## 5. Mediciones y Análisis (Obligatorio para Documentación)

### 1. Medición Empírica
[cite_start]Debe realizarse para cada algoritmo y para ambas combinaciones numéricas (0..9 y 0..15)[cite: 151, 153]:
* [cite_start]Asignaciones y Comparaciones ($<, >, ==, !=$)[cite: 141, 142].
* [cite_start]Líneas de código totales vs. líneas ejecutadas[cite: 143, 144].
* [cite_start]Tiempo de ejecución y memoria consumida[cite: 151].

### 2. Factor de Crecimiento
[cite_start]Calcular el factor de crecimiento entre tallas (ej: de 5 a 10, de 5 a 30) para todas las variables anteriores[cite: 157].

### 3. Clasificación de Complejidad
Clasificar cada algoritmo en notaciones:
* [cite_start]**Theta ($\Theta$), O Grande ($O$), y Omega ($\Omega$)** según mediciones empíricas[cite: 145].
* [cite_start]**Medición Analítica:** Análisis línea por línea del código de armado en el peor de los casos para determinar el $O$ Grande[cite: 170].

### 4. Medición Gráfica
[cite_start]Un solo gráfico comparativo de "Líneas ejecutadas" para los algoritmos, separando las alternativas 0..9 y 0..15[cite: 173].

---

## 6. Formato de Entrega y Administración
* [cite_start]**Fecha límite:** 30 de enero de 2026, 11:30 p.m.[cite: 233].
* [cite_start]**Documentación Externa:** Portada, Introducción, Análisis de requerimientos, Solución (clases, lógica, mejoras), Análisis de resultados (tablas y gráficos), Conclusiones y Bibliografía (mínimo 4 referencias APA/IEEE) [cite: 183-222].
* [cite_start]**Código:** Uso de `lowerCamelCase` [cite: 226][cite_start], documentación interna en cada función [cite: 229][cite_start], un solo proyecto[cite: 178].
* [cite_start]**Evaluación:** 60% Documentación, 30% Programación, 5% Coevaluación, 5% Autoevaluación [cite: 240-243].