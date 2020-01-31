package io.dkozak.sudoku.model

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import io.dkozak.sudoku.io.loadPuzzle
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test


/**
 * Abstract test class verifying the interface of SudokuPuzzle
 * For each concrete puzzle, on subclass should be created and a given factory method implemented,
 * this way the common interface can be tested for all implementation without copy pasting the code
 */
abstract class SudokuPuzzleTest {

    abstract fun factory(size: Int): SudokuPuzzle<*>

    @Nested
    inner class ToStringTest {

        @Test
        fun simple() {
            val puzzle = loadPuzzle("src/test/resources/puzzles/first.sudoku", ::factory)
            assertThat(puzzle.toPrintableString())
                    .isEqualTo("""------------------------------
|    1  3 | 8       | 4     5 |
|    2  4 | 6     5 |         |
|    8  7 |         | 9  3    |
------------------------------
| 4  9    | 3     6 |         |
|       1 |         | 5       |
|         | 7     1 |    9  3 |
------------------------------
|    6  9 |         | 7  4    |
|         | 2     7 | 6  8    |
| 1     2 |       8 | 3  5    |
------------------------------
""")
        }
    }

    @Nested
    inner class EqualsTest {
        @Test
        fun `simple comparison`() {
            val simple1 = loadPuzzle("src/test/resources/puzzles/first.sudoku", ::factory)
            val simple2 = loadPuzzle("src/test/resources/puzzles/first.sudoku", ::factory)
            val second = loadPuzzle("src/test/resources/puzzles/second.sudoku", ::factory)
            assertThat(simple1).isEqualTo(simple2)
            assertThat(simple2).isEqualTo(simple1)
            assertThat(simple1).isNotEqualTo(second)
            assertThat(second).isNotEqualTo(simple1)
        }

        @Test
        fun `compare and change`() {
            val simple1 = loadPuzzle("src/test/resources/puzzles/first.sudoku", ::factory)
            val simple2 = loadPuzzle("src/test/resources/puzzles/first.sudoku", ::factory)
            assertThat(simple1).isEqualTo(simple2)
            assertThat(simple2).isEqualTo(simple1)
            simple2[1, 1] = 3
            assertThat(simple1).isNotEqualTo(simple2)
            assertThat(simple2).isNotEqualTo(simple1)
        }
    }
}