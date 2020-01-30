package io.dkozak.sudoku.solver

import assertk.assertThat
import assertk.assertions.isNotNull
import io.dkozak.sudoku.io.loadPuzzle
import io.dkozak.sudoku.model.SimpleSudokuPuzzle
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout


class SimpleBacktrackingSolverTest {

    val solver = SimpleBacktrackingSolver()

    @Test
    @Timeout(5)
    fun simple() {
        val puzzle = loadPuzzle("src/test/resources/puzzles/first.sudoku", ::SimpleSudokuPuzzle)
        val solution = solver.solve(puzzle)
        assertThat(solution).isNotNull()
        println(solution)
    }
}

