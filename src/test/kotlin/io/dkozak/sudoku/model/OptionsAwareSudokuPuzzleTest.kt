package io.dkozak.sudoku.model


class OptionsAwareSudokuPuzzleTest : SudokuPuzzleTest() {
    override fun factory(size: Int): SudokuPuzzle<*> = OptionsAwareSudokuPuzzle(size)
}