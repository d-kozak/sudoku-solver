package io.dkozak.sudoku.solver.io

import assertk.assertThat
import assertk.assertions.isTrue
import io.dkozak.sudoku.solver.model.SimpleSudokuPuzzle
import io.dkozak.sudoku.solver.model.loadSudokuRem
import io.dkozak.sudoku.solver.model.solve
import org.junit.jupiter.api.Test

class LoaderTest {

    fun checkValidity(path: String) {
        val puzzle = loadPuzzle(path, ::SimpleSudokuPuzzle)
        assertThat(puzzle.isValid(true))
                .isTrue()
    }

    @Test
    fun `load first`() {
        checkValidity("src/test/resources/puzzles/first.sudoku")
    }

    @Test
    fun `load second`() {
        checkValidity("src/test/resources/puzzles/second.sudoku")
    }

    @Test
    fun `load first new`() {
        val puzzle = loadSudokuRem("src/test/resources/puzzles/first.sudoku")
        solve(puzzle)
    }

    @Test
    fun `load second new`() {
        val puzzle = loadSudokuRem("src/test/resources/puzzles/second.sudoku")
        solve(puzzle)
    }
}