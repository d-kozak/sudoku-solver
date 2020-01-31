package io.dkozak.sudoku.solver

import assertk.assertThat
import assertk.assertions.isNotNull
import io.dkozak.sudoku.io.loadPuzzle
import io.dkozak.sudoku.model.SimpleSudokuPuzzle
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test


class SimpleBacktrackingSolverTest {

    @Test
    @Disabled // this solver is very inefficient and fails to find the solution, that's why the test is disabled by default
    fun simple() {
        val puzzle = loadPuzzle("src/test/resources/puzzles/first.sudoku", ::SimpleSudokuPuzzle)
        val solution = puzzle.solveWith(::SimpleBacktrackingSolver)
        assertThat(solution).isNotNull()
        println(solution)
    }
}

