package io.dkozak.sudoku.solver

import assertk.assertThat
import assertk.assertions.isNotNull
import io.dkozak.sudoku.solver.io.loadPuzzle
import org.junit.jupiter.api.Test


class SimpleBacktrackingSolverTest {

    val solver = SimpleBacktrackingSolver()

    @Test
    fun simple() {
        val puzzle = loadPuzzle("src/test/resources/puzzles/first.sudoku")
        val solution = solver.solve(puzzle)
        assertThat(solution).isNotNull()
        println(solution)
    }
}

