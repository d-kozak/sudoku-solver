package io.dkozak.sudoku.solver

import io.dkozak.sudoku.model.SimpleSudokuCell
import io.dkozak.sudoku.model.SimpleSudokuPuzzle

/**
 * Very simple backtracking solution.
 * Tries to solve the puzzle by iterating over the puzzle from left to right, top to bottom and recursively tries to set
 * each cell to all it's options until the puzzle is fully set
 * very inefficient
 */
class SimpleBacktrackingSolver(override val initialPuzzle: SimpleSudokuPuzzle) : SudokuSolver<SimpleSudokuCell, SimpleSudokuPuzzle> {


    /**
     * counts the number of invocations of the solveHelper method
     */
    private var count = 0

    /**
     * the maximum depth of recursion, which is the number of empty cells
     */
    private var maxDepth = 0

    override fun solve(puzzle: SimpleSudokuPuzzle): SimpleSudokuPuzzle? {
        count = 0
        maxDepth = 0

        for (cell in puzzle.allCells())
            if (cell.isEmpty())
                maxDepth++

        return solveHelper(puzzle, 1)
    }


    private fun solveHelper(puzzle: SimpleSudokuPuzzle, depth: Int): SimpleSudokuPuzzle? {
        count++
        if (depth <= 2)
            println(puzzle)
        if (count % 1_000_000 == 0)
            println("call ${count}, depth ${depth}/${maxDepth}")
        var emptyCellFound = false
        for ((i, j, cell) in puzzle.allCellsIndexed()) {
            if (cell.isEmpty()) {
                emptyCellFound = true
                val options = puzzle.numbersFor(i, j)
                if (options.isEmpty()) return null
                for (option in options) {
                    puzzle[i, j] = option
                    val maybeSolution = solveHelper(puzzle, depth + 1)
                    if (maybeSolution != null) return maybeSolution
                    puzzle[i, j] = -1
                }
            }
        }
        return if (!emptyCellFound) puzzle else null
    }
}