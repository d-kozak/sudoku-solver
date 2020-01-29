package io.dkozak.sudoku.solver

import io.dkozak.sudoku.solver.model.SudokuPuzzle

/**
 * Very simple backtracking solution, very inefficient
 */
class SimpleBacktrackingSolver : SudokuSolver {


    /**
     * counts the number of invocations of the solveHelper method
     */
    private var count = 0

    /**
     * the maximum depth of recursion, that is the number of empty cells
     */
    private var maxDepth = 0

    override fun solve(puzzle: SudokuPuzzle): SudokuPuzzle? {
        count = 0
        maxDepth = 0

        for (row in puzzle.rows) {
            for (cell in row) {
                if (cell == 0) {
                    maxDepth++
                }
            }
        }

        return solveHelper(puzzle, 1)
    }


    private fun solveHelper(puzzle: SudokuPuzzle, depth: Int): SudokuPuzzle? {
        count++
        if (depth <= 2)
            println(puzzle)
        if (count % 10_000_000 == 0)
            println("call ${count}, depth ${depth}/${maxDepth}")
        var problemFound = false
        for ((i, row) in puzzle.rows.withIndex()) {
            for ((j, cell) in row.withIndex()) {
                if (cell == 0) {
                    problemFound = true
                    val options = puzzle.numbersFor(i, j)
                    if (options.isEmpty()) return null
                    for (option in options) {
                        puzzle.rows[i][j] = option
                        val maybeSolution = solveHelper(puzzle, depth + 1)
                        if (maybeSolution != null) return maybeSolution
                        puzzle.rows[i][j] = 0
                    }
                }
            }
        }
        return if (!problemFound) puzzle else null
    }
}