package io.dkozak.sudoku.solver

import io.dkozak.sudoku.model.OptionsAwareSudokuCell
import io.dkozak.sudoku.model.OptionsAwareSudokuPuzzle
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

/**
 *
 * Uses ExactSolver as long as possible.
 * If the exact solver fails to fully complete the puzzle,
 * then the cell with fewest options is used and all the options are checked in a dfs manner.
 *
 * This is the only solver that succesfully handles boths and test examples and the empty example, that's why it is the solver of choice in the main method.
 */
class OptionsAwareDfsSolver(override val initialPuzzle: OptionsAwareSudokuPuzzle) : SudokuSolver<OptionsAwareSudokuCell, OptionsAwareSudokuPuzzle> {

    /**
     * @return the solved puzzle or null if no solution is found
     */
    override fun solve(puzzle: OptionsAwareSudokuPuzzle): OptionsAwareSudokuPuzzle? {
        val maybeSolution = OptionsAwareExactSolver(puzzle).solve(puzzle)
        if (maybeSolution != null) return maybeSolution

        val emptyCells = puzzle.allCellsIndexed()
                .filter { it.third.isEmpty() }
                .toList()
        val (i, j, bestCell) = emptyCells
                .minBy { it.third.content.cardinality() }
                ?: return null //  if there is no good cell for update, this path leads to nowhere

        for (option in bestCell.allOptions()) {
            val copy = puzzle.copy()
            copy[i, j] = option
//            logger.info { "Guessing $option for [$i][$j], remains ${emptyCells.size} empty cells" }
            val maybeSolution = OptionsAwareDfsSolver(copy).solve(copy)
            if (maybeSolution != null) return maybeSolution
        }

        return null
    }
}