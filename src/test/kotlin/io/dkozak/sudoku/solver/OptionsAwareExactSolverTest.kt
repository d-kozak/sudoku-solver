package io.dkozak.sudoku.solver

import assertk.assertThat
import assertk.assertions.isNull
import io.dkozak.sudoku.io.loadPuzzle
import io.dkozak.sudoku.model.OptionsAwareSudokuPuzzle
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail


class OptionsAwareExactSolverTest {

    @Test
    fun `solve first`() {
        val puzzle = loadPuzzle("src/test/resources/puzzles/first.sudoku", ::OptionsAwareSudokuPuzzle)
        val solution = puzzle.solveWith(::OptionsAwareExactSolver)
        solution?.validateOrFail(false) ?: fail("solution should be found")
        println(solution)
    }

    @Test
    fun `solve second`() {
        // the exact solver cannot handle "guessing", so there should be no solution
        val puzzle = loadPuzzle("src/test/resources/puzzles/second.sudoku", ::OptionsAwareSudokuPuzzle)
        assertThat(puzzle.solveWith(::OptionsAwareExactSolver)).isNull()
    }
}