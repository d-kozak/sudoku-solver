package io.dkozak.sudoku.solver

import io.dkozak.sudoku.model.OptionsAwareSudokuPuzzle
import mu.KotlinLogging
import java.util.*

private val logger = KotlinLogging.logger { }

class OptionsAwareBacktrackingSolver(startPuzzle: OptionsAwareSudokuPuzzle) {

    val queue: Queue<OptionsAwareSudokuPuzzle> = LinkedList()

    init {
        queue.add(startPuzzle)
    }

    fun solve(): OptionsAwareSudokuPuzzle? {
        while (queue.isNotEmpty()) {
            val current = queue.poll()

            val maybeSolution = OptionsAwareExactSolver(current).solve()
            if (maybeSolution != null) return maybeSolution


            val (i, j, bestCell) = current.allCellsIndexed()
                    .filterNot { it.third.isSet }
                    .minBy { it.third.content.cardinality() }
                    ?: continue //  if there is no good cell for update, this path leads to nowhere

            for (option in bestCell.allOptions()) {
                val copy = current.copy()
                copy[i, j] = option
                logger.info { "Guessing $option for [$i][$j]" }
                queue.add(copy)
            }
        }

        return null
    }
}