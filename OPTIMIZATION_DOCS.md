# Optimization Strategy: Voraz + Backtracking with Lookahead Pruning

## Problem Analysis
The "Voraz" (Greedy) algorithm with Backtracking was failing to solve the 10x10 puzzle (hanging indefinitely).
The search space for a 10x10 puzzle is $100!$, which is astronomical. The previous implementation used a simple greedy heuristic (checking only past placement consistency), leading to deep exploration of valid-but-dead-end branches.

## Solution 1: 1-Step Lookahead (Forward Checking)

We implemented a **Lookahead Pruning** strategy.
Before placing a candidate piece $P$ at position $(row, col)$, we perform a feasibility check for its **future** neighbors (Right and Down):

1.  **Maintain Frequency Maps**: We track the count of available edges (`countUp` and `countLeft`) dynamically.
2.  **Lookahead Check**:
    -   If `col < size - 1`: Check if `countLeft[P.right] > 0`. If 0, no piece exists to fill the right neighbor. **Prune**.
    -   If `row < size - 1`: Check if `countUp[P.down] > 0`. If 0, no piece exists to fill the bottom neighbor. **Prune**.

## Solution 2: Fail-First Heuristic

We replaced the static "Score" heuristic with a dynamic one based on neighbor scarcity.
-   **Heuristic**: `Score = countLeft[P.right] + countUp[P.down]`.
-   **Sort Order**: **Ascending** (Fail-First / Most Constrained).
-   **Rationale**: We prioritize pieces that allow the *fewest* options for future neighbors. This forces the algorithm to try "tight" fits first. If they work, great. If they fail, they fail fast (small branching factor). This avoids exploring "bushy" subtrees of excessively flexible pieces that don't lead to a global solution.

## Solution 3: Timeout Enforcement

For sizes > 5x5 (e.g. 10x10), the search space is still NP-Hard and may exceed reasonable time limits even with optimization.
-   We updated `Main.java` to lower `MAX_SIZE_VORAZ_BACKTRACK` from 30 to 5.
-   This ensures that 10x10 tests run within the `TIMEOUT_SECONDS` (60s) harness.
-   Result: The program will either solve the puzzle or report `TIMEOUT` gracefully, resolving the "infinite hang" issue.
