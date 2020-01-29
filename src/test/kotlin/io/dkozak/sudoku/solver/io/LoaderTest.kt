package io.dkozak.sudoku.solver.io

import assertk.assertThat
import assertk.assertions.isTrue
import org.junit.jupiter.api.Test

class LoaderTest {

    fun checkValidity(path: String) {
        val puzzle = loadPuzzle(path)
        assertThat(puzzle.isValid())
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
}