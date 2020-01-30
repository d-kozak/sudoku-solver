package io.dkozak.sudoku.solver

import io.dkozak.sudoku.model.SimpleSudokuPuzzle

/**
 * Generic interface for a sudoku solver
 */
interface SudokuSolver {
    fun solve(puzzle: SimpleSudokuPuzzle): SimpleSudokuPuzzle?
}