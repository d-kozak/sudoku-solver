package io.dkozak.sudoku.model



class SimpleSudokuPuzzleTest : SudokuPuzzleTest() {
    override fun factory(size: Int): SudokuPuzzle<*> = SimpleSudokuPuzzle(size)
}