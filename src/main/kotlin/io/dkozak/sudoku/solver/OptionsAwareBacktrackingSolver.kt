package io.dkozak.sudoku.solver

import io.dkozak.sudoku.model.OptionsAwareSudokuPuzzle
import java.util.*

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
        }

        return null
    }
}