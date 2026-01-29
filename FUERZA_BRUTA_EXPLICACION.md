# Algoritmo de Fuerza Bruta - Rompecabezas de Piezas Encajables

## 📋 Índice
1. [Introducción al Problema](#introducción-al-problema)
2. [Representación de Datos](#representación-de-datos)
3. [Fundamentos del Algoritmo](#fundamentos-del-algoritmo)
4. [Implementación Detallada](#implementación-detallada)
5. [Análisis de Complejidad](#análisis-de-complejidad)
6. [Optimizaciones y Técnicas](#optimizaciones-y-técnicas)
7. [Instrumentación y Medición](#instrumentación-y-medición)
8. [Casos de Uso y Limitaciones](#casos-de-uso-y-limitaciones)

---

## 🧩 Introducción al Problema

### El Rompecabezas de Piezas Encajables

Este proyecto resuelve un tipo especial de rompecabezas donde:

- **Tablero**: Una cuadrícula de n×n posiciones
- **Piezas**: Cada pieza tiene 4 lados con números
- **Objetivo**: Organizar todas las piezas de forma que los lados adyacentes coincidan

#### Estructura de una Pieza

Cada pieza tiene **4 números** que representan sus lados:

```
      [arriba]
         ↑
[izq] ← ▣ → [der]
         ↓
      [abajo]
```

**Ejemplo de pieza**: `[3, 7, 2, 5]`
- Arriba: 3
- Derecha: 7  
- Abajo: 2
- Izquierda: 5

### Reglas de Encaje

Para que dos piezas sean compatibles, los lados que se tocan deben tener **el mismo número**:

**Compatibilidad Horizontal** (izquierda-derecha):
```
Pieza A [_,7,_,_] | Pieza B [_,_,_,7]
        ← derecha=7 | izquierda=7 →
                ✓ VÁLIDO
```

**Compatibilidad Vertical** (arriba-abajo):
```
Pieza A [_,_,5,_]
          abajo=5
            ↓
Pieza B [5,_,_,_]
        arriba=5
        ✓ VÁLIDO
```

### Ejemplo Completo: Tablero 2×2

**Tablero Resuelto**:
```
[1,3,2,0] [2,5,4,3]
[4,7,6,0] [6,2,8,7]
```

**Verificaciones**:
- Pieza[0][0].derecha (3) = Pieza[0][1].izquierda (3) ✓
- Pieza[0][0].abajo (2) = Pieza[1][0].arriba (2) ✗ 
  - **ERROR**: 2 ≠ 4

Este tablero **NO está resuelto** correctamente.

---

## 💾 Representación de Datos

### Clase `Pieza`

```java
public class Pieza {
    private int up;     // Lado superior
    private int down;   // Lado inferior
    private int left;   // Lado izquierdo
    private int right;  // Lado derecho
    
    public Pieza(int up, int right, int down, int left) {
        this.up = up;
        this.right = right;
        this.down = down;
        this.left = left;
    }
    
    // Getters para acceder a cada lado
    public int getUp() { return up; }
    public int getRight() { return right; }
    public int getDown() { return down; }
    public int getLeft() { return left; }
}
```

**Características importantes**:
- Orden específico: `(arriba, derecha, abajo, izquierda)`
- Inmutabilidad: No hay setters, los valores no cambian
- Representación visual: `[up, right, down, left]`

### Clase `Tablero`

```java
public class Tablero {
    public Pieza[][] tablero;      // Matriz de piezas
    public List<Pieza> listaPiezas; // Colección de todas las piezas
    public int size;                // Tamaño n×n
    public int rangoNum;            // Rango de números (0..rangoNum)
    
    // Constructor
    public Tablero(int size, int rangoNum) {
        this.size = size;
        this.pieces = size * size;
        this.rangoNum = rangoNum;
        this.tablero = new Pieza[size][size];
    }
}
```

**Operaciones clave**:
- `createTablero()`: Genera un tablero válido inicialmente
- `scrambleTablero()`: Desordena las piezas aleatoriamente
- `setPieza(row, col, pieza)`: Coloca una pieza en una posición
- `removePieza(row, col)`: Quita una pieza de una posición
- `checkTablero()`: Verifica si el tablero está correctamente resuelto

---

## 🔍 Fundamentos del Algoritmo

### ¿Qué es Fuerza Bruta?

**Fuerza Bruta** (Brute Force) es una estrategia algorítmica que:
1. **Explora todas las posibles soluciones** del espacio de búsqueda
2. **Verifica cada una** hasta encontrar la correcta
3. **No usa heurísticas** ni optimizaciones inteligentes

#### Analogía: Abriendo una Cerradura de Combinación

Imagina una cerradura con 3 dígitos (000-999):
- **Fuerza Bruta**: Probar 000, 001, 002, ..., 999 (todas las combinaciones)
- **Tiempo**: 1000 intentos en el peor caso
- **Garantía**: Encontrará la combinación correcta si existe

### Backtracking: La Técnica Clave

El algoritmo usa **Backtracking** (retroceso), que es una técnica de fuerza bruta optimizada:

```
BACKTRACKING:
1. Hacer una elección
2. Explorar recursivamente
3. Si no funciona → DESHACER la elección (backtrack)
4. Probar la siguiente opción
```

#### Visualización del Backtracking

Para un tablero 2×2:

```
Estado Inicial (vacío):
[_,_,_,_] [_,_,_,_]
[_,_,_,_] [_,_,_,_]

Paso 1: Probar pieza A en [0][0]
[A,_,_,_] [_,_,_,_]
[_,_,_,_] [_,_,_,_]
         ↓ recursión

Paso 2: Probar pieza B en [0][1]
[A,_,_,_] [B,_,_,_]
[_,_,_,_] [_,_,_,_]
         ↓ recursión

Paso 3: No hay pieza válida para [1][0]
         ↓ BACKTRACK

Paso 4: Deshacer B, probar pieza C en [0][1]
[A,_,_,_] [C,_,_,_]
[_,_,_,_] [_,_,_,_]
         ↓ recursión
         ... continúa
```

---

## 🛠️ Implementación Detallada

### Método 1: `canPlace()` - Validación de Restricciones

Este método verifica si una pieza puede colocarse legalmente en una posición.

```java
public boolean canPlace(Tablero board, int row, int col, Pieza piece) {
    // Caso 1: Primera posición [0][0] - siempre válida
    if (row == 0 && col == 0) {
        return true;
    }
    
    // Caso 2: Primera fila (row == 0) - solo verifica izquierda
    if (row == 0) {
        Pieza leftPiece = board.tablero[row][col - 1];
        return leftPiece != null && 
               piece.getLeft() == leftPiece.getRight();
    }
    
    // Caso 3: Primera columna (col == 0) - solo verifica arriba
    if (col == 0) {
        Pieza upPiece = board.tablero[row - 1][col];
        return upPiece != null && 
               piece.getUp() == upPiece.getDown();
    }
    
    // Caso 4: Resto del tablero - verifica arriba e izquierda
    Pieza leftPiece = board.tablero[row][col - 1];
    Pieza upPiece = board.tablero[row - 1][col];
    
    boolean leftMatches = leftPiece != null && 
                          piece.getLeft() == leftPiece.getRight();
    boolean upMatches = upPiece != null && 
                        piece.getUp() == upPiece.getDown();
    
    return leftMatches && upMatches;
}
```

#### Análisis por Casos

**CASO 1: Posición [0][0]** (esquina superior izquierda)
```
[?] [ ] [ ]
[ ] [ ] [ ]
[ ] [ ] [ ]
```
- No hay vecinos → **cualquier pieza es válida**
- Costo: O(1) - solo 2 comparaciones

**CASO 2: Primera fila** (row = 0, col > 0)
```
[✓] [?] [ ]
[ ] [ ] [ ]
[ ] [ ] [ ]
```
- Solo valida con el vecino **izquierdo**
- Condición: `pieza.izquierda == vecino_izq.derecha`
- Costo: O(1)

**CASO 3: Primera columna** (col = 0, row > 0)
```
[✓] [ ] [ ]
[?] [ ] [ ]
[ ] [ ] [ ]
```
- Solo valida con el vecino **superior**
- Condición: `pieza.arriba == vecino_arriba.abajo`
- Costo: O(1)

**CASO 4: Posiciones internas**
```
[✓] [✓] [ ]
[✓] [?] [ ]
[ ] [ ] [ ]
```
- Valida con vecino **izquierdo** Y vecino **superior**
- Condiciones:
  - `pieza.izquierda == vecino_izq.derecha`
  - `pieza.arriba == vecino_arriba.abajo`
- Ambas deben cumplirse
- Costo: O(1)

#### Instrumentación del Código

Cada operación relevante incrementa contadores:

```java
comparaciones += 2;  // row == 0 && col == 0
if (row == 0 && col == 0) {
    return true;
}
```

**Tipos de contadores**:
- `comparaciones`: Operaciones de comparación (`==`, `!=`, `<`, `>`, etc.)
- `asignaciones`: Asignaciones de variables (`=`)

### Método 2: `solveBoard()` - Motor del Backtracking

Este es el **corazón del algoritmo**. Resuelve el tablero recursivamente.

```java
public boolean solveBoard(Tablero board, int row, int col, 
                         List<Pieza> availablePieces) {
    // BASE CASE: ¿Hemos llenado todo el tablero?
    if (row >= board.size) {
        return true; // ¡Éxito! Todas las posiciones están llenas
    }
    
    // Calcular la siguiente posición a llenar
    int nextCol = col + 1;
    int nextRow = row;
    
    if (nextCol >= board.size) {  // Fin de fila
        nextCol = 0;               // Volver a primera columna
        nextRow = row + 1;         // Avanzar a siguiente fila
    }
    
    // RECURSIVE CASE: Probar cada pieza disponible
    for (int i = 0; i < availablePieces.size(); i++) {
        Pieza piece = availablePieces.get(i);
        
        // ¿Esta pieza encaja aquí?
        if (canPlace(board, row, col, piece)) {
            
            // PASO 1: HACER LA ELECCIÓN
            board.setPieza(row, col, piece);
            availablePieces.remove(i);
            
            // PASO 2: EXPLORAR RECURSIVAMENTE
            if (solveBoard(board, nextRow, nextCol, availablePieces)) {
                return true; // ¡Encontramos solución!
            }
            
            // PASO 3: BACKTRACK (deshacer)
            board.removePieza(row, col);
            availablePieces.add(i, piece);
        }
        
        intentos++; // Contador de intentos
    }
    
    // No encontramos solución desde esta configuración
    podas++; // Contador de ramas podadas
    return false;
}
```

#### Flujo de Ejecución Detallado

**Ejemplo paso a paso** para tablero 2×2:

```
LLAMADA INICIAL: solveBoard(board, 0, 0, [A,B,C,D])

┌─────────────────────────────────────┐
│ NIVEL 0: Posición [0][0]           │
└─────────────────────────────────────┘

Estado: [_] [_]    Disponibles: [A,B,C,D]
        [_] [_]

Iteración 1: Probar pieza A
  ✓ canPlace(A) = true
  → Colocar A en [0][0]
  → Remover A de disponibles
  
  Estado: [A] [_]    Disponibles: [B,C,D]
          [_] [_]
  
  → LLAMADA RECURSIVA: solveBoard(board, 0, 1, [B,C,D])
  
  ┌─────────────────────────────────────┐
  │ NIVEL 1: Posición [0][1]           │
  └─────────────────────────────────────┘
  
  Iteración 1: Probar pieza B
    ✓ canPlace(B) = true (B.left == A.right)
    → Colocar B en [0][1]
    → Remover B de disponibles
    
    Estado: [A] [B]    Disponibles: [C,D]
            [_] [_]
    
    → LLAMADA RECURSIVA: solveBoard(board, 1, 0, [C,D])
    
    ┌─────────────────────────────────────┐
    │ NIVEL 2: Posición [1][0]           │
    └─────────────────────────────────────┘
    
    Iteración 1: Probar pieza C
      ✗ canPlace(C) = false (C.up != A.down)
      → NO colocar C
    
    Iteración 2: Probar pieza D
      ✗ canPlace(D) = false (D.up != A.down)
      → NO colocar D
    
    → Ninguna pieza funciona
    → podas++
    → RETURN false (BACKTRACK)
    
  ← REGRESO AL NIVEL 1
  
  ✗ La recursión retornó false
  → BACKTRACK: Deshacer B
  → Remover B de [0][1]
  → Restaurar B a disponibles
  
  Estado: [A] [_]    Disponibles: [B,C,D]
          [_] [_]
  
  Iteración 2: Probar pieza C
    ✓ canPlace(C) = true (C.left == A.right)
    → Colocar C en [0][1]
    → Remover C de disponibles
    
    Estado: [A] [C]    Disponibles: [B,D]
            [_] [_]
    
    → LLAMADA RECURSIVA: solveBoard(board, 1, 0, [B,D])
    
    ┌─────────────────────────────────────┐
    │ NIVEL 2: Posición [1][0]           │
    └─────────────────────────────────────┘
    
    Iteración 1: Probar pieza B
      ✓ canPlace(B) = true (B.up == A.down)
      → Colocar B en [1][0]
      
      Estado: [A] [C]    Disponibles: [D]
              [B] [_]
      
      → LLAMADA RECURSIVA: solveBoard(board, 1, 1, [D])
      
      ┌─────────────────────────────────────┐
      │ NIVEL 3: Posición [1][1]           │
      └─────────────────────────────────────┘
      
      Iteración 1: Probar pieza D
        ✓ canPlace(D) = true
        → Colocar D en [1][1]
        
        Estado: [A] [C]    Disponibles: []
                [B] [D]
        
        → LLAMADA RECURSIVA: solveBoard(board, 2, 0, [])
        
        ┌─────────────────────────────────────┐
        │ NIVEL 4: BASE CASE                 │
        └─────────────────────────────────────┘
        
        row (2) >= board.size (2)
        → RETURN true ✓✓✓ ¡SOLUCIÓN ENCONTRADA!
        
      ← RETORNO EXITOSO PROPAGÁNDOSE...
      ← NIVEL 3: return true
      ← NIVEL 2: return true
      ← NIVEL 1: return true
      ← NIVEL 0: return true

┌─────────────────────────────────────┐
│ ¡ALGORITMO COMPLETADO!              │
│ Solución: [A][C]                    │
│           [B][D]                    │
└─────────────────────────────────────┘
```

#### Conceptos Clave del Backtracking

**1. Espacio de Estados**

Cada configuración del tablero es un "estado":

```
Estado 1: [A][_]    Estado 2: [A][B]    Estado 3: [A][C]
          [_][_]              [_][_]              [_][_]
```

El algoritmo explora un **árbol de estados**.

**2. Poda (Pruning)**

Cuando no hay piezas válidas para una posición, **cortamos esa rama**:

```
        [A][_]
         /  \
       [B]  [C]
       / \   / \
      X  X  [D] ...
      ↑
    Poda: No hay piezas válidas
    No exploramos más abajo
```

**3. Backtracking vs Fuerza Bruta Pura**

| Característica | Fuerza Bruta Pura | Backtracking |
|---------------|-------------------|--------------|
| Explora | Todas las permutaciones | Solo configuraciones válidas |
| Poda | No | Sí (cuando falla validación) |
| Eficiencia | Menos eficiente | Más eficiente |
| Complejidad | O(N!) | O(N·N!) peor caso, pero poda mucho |

### Método 3: `solve()` - Punto de Entrada

Este método inicializa el proceso y prepara el tablero.

```java
public boolean solve(Tablero board) {
    // 1. REINICIAR CONTADORES
    comparaciones = 0;
    asignaciones = 0;
    intentos = 0;
    podas = 0;
    
    // 2. LIMPIAR EL TABLERO
    // Remover todas las piezas para empezar desde cero
    for (int i = 0; i < board.size; i++) {
        for (int j = 0; j < board.size; j++) {
            board.removePieza(i, j);
        }
    }
    
    // 3. PREPARAR LISTA DE PIEZAS DISPONIBLES
    // Crear una copia de las piezas para no modificar el original
    List<Pieza> availablePieces = new ArrayList<>(board.listaPiezas);
    
    // 4. INICIAR EL BACKTRACKING DESDE [0][0]
    return solveBoard(board, 0, 0, availablePieces);
}
```

#### ¿Por qué Limpiar el Tablero?

El tablero puede venir **desordenado** desde `scrambleTablero()`:

```
Tablero desordenado:
[B] [D] [A]
[C] [_] [E]
[F] [G] [H]
```

Limpiamos todo para tener control total:

```
Tablero limpio:
[_] [_] [_]
[_] [_] [_]
[_] [_] [_]
```

Ahora el algoritmo decide **desde cero** dónde va cada pieza.

---

## 📊 Análisis de Complejidad

### Complejidad Temporal

#### Análisis del Método `canPlace()`

```java
T_canPlace(N) = O(1)
```

**Justificación**:
- Número fijo de comparaciones (máximo 6)
- No hay bucles ni recursión
- Tiempo constante independiente del tamaño N

#### Análisis del Método `solveBoard()`

**Ecuación de recurrencia**:

```
T_solveBoard(N) = N · T_solveBoard(N-1) + O(N²)
```

**Donde**:
- `N`: Número de piezas disponibles
- `N · T(N-1)`: Probar cada una de las N piezas recursivamente con N-1 restantes
- `O(N²)`: Operaciones de `remove()` y `add()` en listas

**Expansión de la recurrencia**:

```
T(N) = N · T(N-1) + c·N²
T(N) = N · [(N-1)·T(N-2) + c·(N-1)²] + c·N²
T(N) = N·(N-1) · T(N-2) + c·N·(N-1)² + c·N²
     ...continúa expandiendo...
T(N) = N·(N-1)·(N-2)·...·1 · T(0) + Σ términos
T(N) = N! · c + términos menores
```

**Resultado**:
```
T_solveBoard(N) = O(N · N!)
```

#### Análisis del Método `solve()`

```
T_solve(N) = O(N) + O(N · N!)
T_solve(N) = O(N · N!)
```

**Componentes**:
1. Limpiar tablero: O(N) donde N = n²
2. Copiar lista: O(N)
3. Backtracking: O(N · N!)

El término dominante es `O(N · N!)`.

### Complejidad en Términos del Lado n

Si el tablero es n×n:
- N = n² (número total de piezas)
- **Complejidad**: O(n² · (n²)!)

**Tabla de crecimiento**:

| n | N | N! | Orden de magnitud |
|---|---|----|--------------------|
| 2 | 4 | 24 | Decenas |
| 3 | 9 | 362,880 | Cientos de miles |
| 4 | 16 | 2.09×10¹³ | Trillones |
| 5 | 25 | 1.55×10²⁵ | ¡Inmanejable! |

### Complejidad Espacial

**Uso de memoria**:

```
S(N) = O(N²) + O(N) + O(N)
S(N) = O(N²)
```

**Componentes**:
1. **Tablero**: `Pieza[n][n]` → O(n²) = O(N)
2. **Lista de piezas**: `List<Pieza>` → O(N)
3. **Pila de recursión**: Profundidad máxima N → O(N)

Total: O(N²) en términos de n, u O(N) en términos del número de piezas.

---

## ⚡ Optimizaciones y Técnicas

### 1. Poda por Validación Temprana

En lugar de generar todas las permutaciones y luego validar:

```java
// ❌ INEFICIENTE
generar_todas_permutaciones()
for cada permutación:
    if es_valida(permutación):
        return permutación

// ✅ EFICIENTE (con poda)
for cada pieza:
    if canPlace(pieza):  // Validar ANTES de continuar
        colocar(pieza)
        recursion()
```

**Beneficio**: Corta ramas inválidas inmediatamente.

### 2. Reducción del Espacio de Búsqueda

A medida que colocamos piezas, el espacio se reduce:

```
Posición [0][0]: N piezas disponibles
Posición [0][1]: N-1 piezas disponibles
Posición [0][2]: N-2 piezas disponibles
...
Posición [n-1][n-1]: 1 pieza disponible
```

**Beneficio**: El factor de ramificación disminuye con cada nivel.

### 3. Validación por Restricciones Locales

Solo verificamos vecinos ya colocados (izquierda y arriba), no todo el tablero:

```java
// Solo estas 2 verificaciones:
if (col > 0) verificar_izquierda();
if (row > 0) verificar_arriba();
```

**Beneficio**: O(1) por validación, no O(N).

### 4. Orden de Llenado Eficiente

Llenamos de **izquierda a derecha**, **arriba a abajo**:

```
Orden: 1 → 2 → 3
       ↓
       4 → 5 → 6
       ↓
       7 → 8 → 9
```

**Beneficio**: Maximiza el número de restricciones activas en cada paso.

### 5. Optimización de Estructuras de Datos

**Operación costosa**: `availablePieces.remove(i)` es O(N)

```java
// En cada paso del backtracking:
availablePieces.remove(i);      // O(N)
...recursión...
availablePieces.add(i, piece);  // O(N)
```

**Alternativa más eficiente** (no implementada aquí pero posible):
- Usar un array booleano para marcar piezas usadas
- Costo: O(1) por operación

```java
boolean[] usado = new boolean[N];
usado[i] = true;   // O(1)
...recursión...
usado[i] = false;  // O(1)
```

---

## 📈 Instrumentación y Medición

### Sistema de Contadores

El código incluye 4 contadores para análisis empírico:

```java
private long comparaciones = 0;  // Número de comparaciones
private long asignaciones = 0;   // Número de asignaciones
private long intentos = 0;       // Intentos de colocar piezas
private long podas = 0;          // Ramas podadas
```

### Cómo se Usan

**Ejemplo en `canPlace()`**:

```java
comparaciones += 2;  // Cuenta: row == 0 && col == 0
if (row == 0 && col == 0) {
    return true;
}
```

**Ejemplo en `solveBoard()`**:

```java
for (int i = 0; i < availablePieces.size(); i++) {
    Pieza piece = availablePieces.get(i);
    
    if (canPlace(board, row, col, piece)) {
        board.setPieza(row, col, piece);    
        availablePieces.remove(i);          
        
        if (solveBoard(...)) {
            return true;
        }
        
        board.removePieza(row, col);        
        availablePieces.add(i, piece);      
    }
    
    intentos++;  // ← Cuenta cada intento
}
podas++;  // ← Cuenta cuando no hay solución
```

### Métricas Calculadas

**Líneas Ejecutadas**:
```java
long lineasEjecutadas = comparaciones + asignaciones;
```

Esta es una **aproximación** del trabajo total realizado.

### Medición de Memoria

```java
System.gc();  // Forzar recolección de basura
Runtime runtime = Runtime.getRuntime();
long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

// ... ejecutar algoritmo ...

long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
long memoryUsed = memoryAfter - memoryBefore;
```

### Ejemplo de Salida

```
--- Estadísticas ---
Tiempo de ejecución: 145.327 ms
Comparaciones: 1,234,567
Asignaciones: 987,654
Líneas Ejecutadas (C + A): 2,222,221
Intentos fallidos: 45,678
Podas: 12,345
Memoria usada: 524288 bytes (512.00 KB)
```

---

## 🎯 Casos de Uso y Limitaciones

### Casos de Uso Apropiados

#### 1. Tableros Pequeños (n ≤ 4)

```
3×3 (9 piezas):  9! = 362,880 permutaciones
4×4 (16 piezas): 16! ≈ 2×10¹³ permutaciones
```

**Tiempo esperado**:
- 3×3: < 1 segundo
- 4×4: minutos a horas

#### 2. Verificación de Soluciones

Útil para **validar** que otras heurísticas (voraz, genético) encontraron la solución correcta.

#### 3. Análisis Teórico

Excelente para **estudiar el comportamiento** del problema:
- Efecto de la poda
- Peor vs mejor caso
- Distribución de soluciones

### Limitaciones Críticas

#### 1. Complejidad Factorial

**Problema**: O(N·N!) crece extremadamente rápido.

| Tablero | Piezas | Permutaciones | Tiempo estimado* |
|---------|--------|---------------|------------------|
| 3×3 | 9 | 362,880 | < 1 seg |
| 4×4 | 16 | 2.09×10¹³ | horas |
| 5×5 | 25 | 1.55×10²⁵ | años |
| 10×10 | 100 | 9.33×10¹⁵⁷ | **imposible** |

*Asumiendo 1 millón de permutaciones/segundo

#### 2. Escalabilidad Inexistente

**Para tableros grandes** (10×10, 30×30, 100×100):
- ❌ **Completamente inviable**
- Requiere algoritmos alternativos:
  - Voraz (greedy)
  - Genético
  - Simulated Annealing

#### 3. Uso de Memoria

```
Tablero 10×10:
- Matriz: 100 piezas × 16 bytes ≈ 1.6 KB
- Pila de recursión: 100 niveles × ~200 bytes ≈ 20 KB
- Lista de piezas: 100 × 8 bytes ≈ 0.8 KB

Total: ~22 KB (manejable)
```

Memoria **no es el problema**, es el **tiempo de ejecución**.

### Comparación con Otros Algoritmos

| Algoritmo | Complejidad | Tablero Max | Garantía |
|-----------|-------------|-------------|----------|
| **Fuerza Bruta** | O(N·N!) | 4×4 | Solución óptima |
| Voraz | O(N²) | 100×100+ | No garantiza |
| Genético | O(G·P·N) | 100×100+ | Aproximación |

**Donde**:
- N: Número de piezas
- G: Generaciones (genético)
- P: Población (genético)

### Cuándo Usar Fuerza Bruta

✅ **Usar cuando**:
- Tablero ≤ 4×4
- Necesitas solución **garantizada**
- Análisis teórico o académico
- Validación de otros algoritmos

❌ **NO usar cuando**:
- Tablero > 4×4
- Necesitas respuesta rápida
- Aproximación es suficiente
- Producción real

---

## 🔧 Mejoras Potenciales

### 1. Optimizar Operaciones de Lista

**Actual**: `remove()` y `add()` son O(N)

```java
availablePieces.remove(i);     // O(N) - desplaza elementos
availablePieces.add(i, piece); // O(N) - desplaza elementos
```

**Mejora**: Usar array booleano

```java
boolean[] usado = new boolean[N];

// Marcar como usado: O(1)
usado[originalIndex] = true;

// Desmarcar: O(1)
usado[originalIndex] = false;
```

**Impacto**: Reduce el término O(N²) a O(N) en la recurrencia.

### 2. Paralelización

Explorar diferentes ramas del árbol **en paralelo**:

```java
ExecutorService executor = Executors.newFixedThreadPool(4);

for (Pieza piece : availablePieces) {
    executor.submit(() -> {
        // Explorar esta rama en un thread separado
        solveBoard(copiaTablero, row, col, piezasRestantes);
    });
}
```

**Beneficio**: Aprovechar múltiples núcleos del CPU.

**Desafío**: Sincronización y gestión de memoria.

### 3. Memoización (Cache de Estados)

Guardar estados ya explorados:

```java
Map<String, Boolean> cache = new HashMap<>();

String estadoKey = generarClave(tablero, disponibles);
if (cache.containsKey(estadoKey)) {
    return cache.get(estadoKey);
}
```

**Problema**: El espacio de estados es tan grande que la memoria se llena.

### 4. Ordenamiento Heurístico de Piezas

En lugar de probar piezas en orden arbitrario, **ordenar por probabilidad de éxito**:

```java
// Priorizar piezas que tienen más coincidencias posibles
Collections.sort(availablePieces, (a, b) -> 
    puntuacion(b, row, col) - puntuacion(a, row, col)
);
```

**Beneficio**: Encuentra soluciones más rápido (aunque no mejora el peor caso).

### 5. Detección de Callejones sin Salida

Verificar si quedan piezas compatibles para posiciones futuras:

```java
// Antes de continuar, verificar que existe al menos una pieza
// válida para la siguiente posición crítica
if (!existePiezaCompatible(nextRow, nextCol, availablePieces)) {
    podas++;
    return false; // Podar esta rama temprano
}
```

---

## 📝 Conclusión

### Fortalezas del Algoritmo

✅ **Garantía de Optimalidad**
- Si existe solución, la encuentra
- No se pierde en óptimos locales

✅ **Simplicidad Conceptual**
- Fácil de entender e implementar
- Código directo y mantenible

✅ **Base para Comparación**
- Estándar de referencia para otros algoritmos
- Útil en análisis académico

### Debilidades del Algoritmo

❌ **Complejidad Factorial**
- Inviable para tableros > 4×4
- Tiempo de ejecución explosivo

❌ **Sin Heurísticas**
- No usa información del dominio
- Explora ciegamente

❌ **Redundancia**
- Puede explorar estados similares múltiples veces

### Aplicaciones Prácticas

1. **Educación**: Enseñar conceptos de backtracking
2. **Validación**: Verificar soluciones de otros algoritmos
3. **Análisis**: Estudiar la estructura del problema
4. **Benchmarking**: Medir desempeño relativo

### Lecciones Aprendidas

1. **Fuerza bruta tiene su lugar** en algoritmos pequeños
2. **La poda es crítica** para hacerlo remotamente viable
3. **Complejidad factorial es prohibitiva** para problemas reales
4. **Algoritmos alternativos son necesarios** para escalabilidad

---

## 📚 Referencias y Recursos

### Conceptos Relacionados

- **Backtracking**: Técnica de búsqueda exhaustiva con retroceso
- **Constraint Satisfaction Problem (CSP)**: Familia de problemas de satisfacción de restricciones
- **Permutaciones**: Ordenamientos posibles de un conjunto
- **Complejidad Factorial**: Crecimiento O(N!)

### Algoritmos Alternativos para este Problema

1. **Voraz (Greedy)**: O(N²) - rápido pero sin garantía
2. **Genético**: O(G·P·N) - aproximación evolutiva
3. **Simulated Annealing**: Metaheurística probabilística
4. **A* Search**: Búsqueda informada con heurística

### Lecturas Recomendadas

- "Introduction to Algorithms" (Cormen et al.) - Capítulo sobre Backtracking
- "Artificial Intelligence: A Modern Approach" (Russell & Norvig) - CSP
- "The Art of Computer Programming" (Knuth) - Permutaciones y combinatoria

---

## 🎓 Ejercicios Propuestos

### Nivel Básico

1. Modificar el código para contar cuántas **soluciones distintas** existen (no detenerse en la primera)
2. Implementar visualización paso a paso del backtracking
3. Calcular el **factor de ramificación promedio**

### Nivel Intermedio

4. Implementar la optimización con array booleano en lugar de `List.remove()`
5. Agregar la mejora de ordenamiento heurístico de piezas
6. Implementar detección de callejones sin salida

### Nivel Avanzado

7. Paralelizar el algoritmo usando threads
8. Implementar memoización con límite de memoria
9. Comparar empíricamente con los algoritmos Voraz y Genético

---

**Documento generado para el proyecto de Algoritmos de Rompecabezas**  
*Autor del código: Equipo del Proyecto*  
*Documentación: Asistente IA*  
*Fecha: 2026*
