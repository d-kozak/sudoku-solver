package io.dkozak.sudoku.solver

import io.dkozak.sudoku.model.OptionsAwareSudokuCell
import io.dkozak.sudoku.model.OptionsAwareSudokuPuzzle
import kotlinx.coroutines.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

/**
 * An idea of how a parallel coroutine based solver could look like
 */
class OptionsAwareParallelSolver(override val initialPuzzle: OptionsAwareSudokuPuzzle) : SudokuSolver<OptionsAwareSudokuCell, OptionsAwareSudokuPuzzle> {

    override fun solve(puzzle: OptionsAwareSudokuPuzzle): OptionsAwareSudokuPuzzle? = runBlocking(Dispatchers.Default) { solveHelper(puzzle) }

    private suspend fun solveHelper(puzzle: OptionsAwareSudokuPuzzle): OptionsAwareSudokuPuzzle? = withContext(Dispatchers.Default) {
        val maybeSolution = OptionsAwareExactSolver(puzzle).solve(puzzle)
        if (maybeSolution != null) return@withContext maybeSolution

        val emptyCells = puzzle.allCellsIndexed()
                .filterNot { it.third.isSet }
                .toList()
        val (i, j, bestCell) = emptyCells
                .minBy { it.third.content.cardinality() }
                ?: return@withContext null //  if there is no good cell for update, this path leads to nowhere

        val results = bestCell.allOptions()
                .map { option ->
                    async {
                        val copy = puzzle.copy()
                        copy[i, j] = option
                        logger.info { "Guessing $option for [$i][$j], remains ${emptyCells.size} empty cells" }
                        solveHelper(copy)
                    }
                }.awaitAll()


        results.filterNotNull().firstOrNull()
    }
}