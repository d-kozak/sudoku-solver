package io.dkozak.sudoku.solver;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SudokuTest {
    @Test
    public void initializationTest() {
        var sudoku = new Sudoku();
        assertThat(sudoku.cells.length).isEqualTo(Sudoku.SUDOKU_SIZE);
    }
}