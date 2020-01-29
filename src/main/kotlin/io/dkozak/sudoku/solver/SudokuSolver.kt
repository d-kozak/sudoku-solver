package io.dkozak.sudoku.solver

import io.dkozak.sudoku.solver.model.SudokuPuzzle

/**
 * Generic interface for a sudoku solver
 */
interface SudokuSolver {
    fun solve(puzzle: SudokuPuzzle): SudokuPuzzle?
}