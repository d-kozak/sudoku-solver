package io.dkozak.sudoku.io

import assertk.assertThat
import assertk.assertions.isEmpty
import io.dkozak.sudoku.model.OptionsAwareSudokuPuzzle
import io.dkozak.sudoku.model.SimpleSudokuPuzzle
import io.dkozak.sudoku.model.SudokuPuzzle
import io.dkozak.sudoku.solver.OptionsAwareExactSolver
import org.junit.jupiter.api.Test

class LoaderTest {

    private fun <PuzzleType : SudokuPuzzle<*>> checkValidity(path: String, puzzleFactory: (Int) -> PuzzleType) {
        val puzzle = loadPuzzle(path, puzzleFactory)
        assertThat(puzzle.validate(true))
                .isEmpty()
    }

    @Test
    fun `load first`() {
        checkValidity("src/test/resources/puzzles/first.sudoku", ::SimpleSudokuPuzzle)
    }

    @Test
    fun `load second`() {
        checkValidity("src/test/resources/puzzles/second.sudoku", ::SimpleSudokuPuzzle)
    }

    @Test
    fun `load first new`() {
        val puzzle = loadPuzzle("src/test/resources/puzzles/first.sudoku", ::OptionsAwareSudokuPuzzle)
        OptionsAwareExactSolver(puzzle)
                .solve()

    }

    @Test
    fun `load second new`() {
        val puzzle = loadPuzzle("src/test/resources/puzzles/second.sudoku", ::OptionsAwareSudokuPuzzle)
        OptionsAwareExactSolver(puzzle)
                .solve()
    }
}