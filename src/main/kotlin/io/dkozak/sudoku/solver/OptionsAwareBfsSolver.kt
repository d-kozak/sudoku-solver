package io.dkozak.sudoku.solver

import io.dkozak.sudoku.model.OptionsAwareSudokuCell
import io.dkozak.sudoku.model.OptionsAwareSudokuPuzzle
import mu.KotlinLogging
import java.util.*

private val logger = KotlinLogging.logger { }

/**
 * Uses ExactSolver as long as possible.
 * If the exact solver fails to fully complete the puzzle,
 * then the cell with fewest options is used and all the options are checked in a bfs manner.
 */
class OptionsAwareBfsSolver(override val initialPuzzle: OptionsAwareSudokuPuzzle) : SudokuSolver<OptionsAwareSudokuCell, OptionsAwareSudokuPuzzle> {
    /**
     * Queue of all remaining configurations to check
     */
    val queue: Queue<OptionsAwareSudokuPuzzle> = LinkedList()

    /**
     * @return the solved puzzle or null if no solution is found
     */
    override fun solve(puzzle: OptionsAwareSudokuPuzzle): OptionsAwareSudokuPuzzle? {
        queue.add(puzzle)
        while (queue.isNotEmpty()) {
            val current = queue.poll()

            val maybeSolution = OptionsAwareExactSolver(current).solve(current)
            if (maybeSolution != null) return maybeSolution


            val emptyCells = current.allCellsIndexed()
                    .filter { it.third.isEmpty() }
                    .toList()
            val (i, j, bestCell) = emptyCells
                    .minBy { it.third.content.cardinality() }
                    ?: continue //  if there is no good cell for update, this path leads to nowhere

            for (option in bestCell.allOptions()) {
                val copy = current.copy()
                copy[i, j] = option
                logger.info { "Guessing $option for [$i][$j], remains ${emptyCells.size} empty cells" }
                queue.add(copy)
            }
        }

        return null
    }
}