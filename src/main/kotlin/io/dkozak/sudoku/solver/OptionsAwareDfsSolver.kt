package io.dkozak.sudoku.solver

import io.dkozak.sudoku.model.OptionsAwareSudokuPuzzle
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

class OptionsAwareDfsSolver(val puzzle: OptionsAwareSudokuPuzzle) {


    fun solve(): OptionsAwareSudokuPuzzle? {
        val maybeSolution = OptionsAwareExactSolver(puzzle).solve()
        if (maybeSolution != null) return maybeSolution

        val emptyCells = puzzle.allCellsIndexed()
                .filterNot { it.third.isSet }
                .toList()
        val (i, j, bestCell) = emptyCells
                .minBy { it.third.content.cardinality() }
                ?: return null //  if there is no good cell for update, this path leads to nowhere

        for (option in bestCell.allOptions()) {
            val copy = puzzle.copy()
            copy[i, j] = option
            logger.info { "Guessing $option for [$i][$j], remains ${emptyCells.size} empty cells" }
            val maybeSolution = OptionsAwareDfsSolver(copy).solve()
            if (maybeSolution != null) return maybeSolution
        }

        return null
    }
}