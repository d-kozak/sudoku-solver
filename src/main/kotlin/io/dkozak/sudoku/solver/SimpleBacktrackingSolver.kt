package io.dkozak.sudoku.solver

import io.dkozak.sudoku.solver.model.SimpleSudokuPuzzle

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

    override fun solve(puzzle: SimpleSudokuPuzzle): SimpleSudokuPuzzle? {
        count = 0
        maxDepth = 0

        for (cell in puzzle.allCells())
            if (cell.isEmpty)
                maxDepth++

        return solveHelper(puzzle, 1)
    }


    private fun solveHelper(puzzle: SimpleSudokuPuzzle, depth: Int): SimpleSudokuPuzzle? {
        count++
        if (depth <= 2)
            println(puzzle)
        if (count % 1_000_000 == 0)
            println("call ${count}, depth ${depth}/${maxDepth}")
        var problemFound = false
        for ((i, j, cell) in puzzle.allCellsIndexed()) {
            if (cell.isEmpty) {
                problemFound = true
                val options = puzzle.numbersFor(i, j)
                if (options.isEmpty()) return null
                for (option in options) {
                    puzzle[i, j] = option
                    val maybeSolution = solveHelper(puzzle, depth + 1)
                    if (maybeSolution != null) return maybeSolution
                    puzzle[i, j].clear()
                }
            }
        }
        return if (!problemFound) puzzle else null
    }
}