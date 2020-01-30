package io.dkozak.sudoku.model

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.dkozak.sudoku.io.loadPuzzle
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test


class SimpleSudokuPuzzleTest {
    @Nested
    inner class ToStringTest {

        @Test
        fun simple() {
            val puzzle = loadPuzzle("src/test/resources/puzzles/first.sudoku", ::SimpleSudokuPuzzle)
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
}