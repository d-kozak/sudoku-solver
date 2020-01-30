package io.dkozak.sudoku.io

import assertk.assertThat
import assertk.assertions.isEmpty
import io.dkozak.sudoku.model.SimpleSudokuPuzzle
import io.dkozak.sudoku.model.SudokuPuzzle
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
}