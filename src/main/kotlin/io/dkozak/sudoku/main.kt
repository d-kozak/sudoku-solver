package io.dkozak.sudoku

import io.dkozak.sudoku.io.loadPuzzle
import io.dkozak.sudoku.model.OptionsAwareSudokuCell
import io.dkozak.sudoku.model.OptionsAwareSudokuPuzzle
import io.dkozak.sudoku.solver.OptionsAwareBfsSolver
import io.dkozak.sudoku.solver.OptionsAwareDfsSolver
import io.dkozak.sudoku.solver.OptionsAwareExactSolver
import io.dkozak.sudoku.solver.SudokuSolver

fun processArgs(args: Array<String>): Pair<OptionsAwareSudokuPuzzle, SudokuSolver<OptionsAwareSudokuCell, OptionsAwareSudokuPuzzle>> {
    check(args.isNotEmpty()) { "Usage ./solver path-to-puzzle [solver-to-use]" }
    val puzzle = loadPuzzle(args[0], ::OptionsAwareSudokuPuzzle)
    val solver = if (args.size == 1) OptionsAwareDfsSolver(puzzle)
    else when (val solverId = args[1]) {
        "dfs" -> OptionsAwareDfsSolver(puzzle)
        "bfs" -> OptionsAwareBfsSolver(puzzle)
        "exact" -> OptionsAwareExactSolver(puzzle, true)
        else -> throw IllegalArgumentException("Unknown solver $solverId")
    }
    return puzzle to solver
}

fun main(args: Array<String>) {
    val (puzzle, solver) = processArgs(args)
    println("Solving sudoku with solver $solver:")
    println(puzzle.toPrintableString())
    val solution = solver.solve(puzzle)
    if (solution != null) {
        println("Solution is:")
        println(solution.toPrintableString())
    } else println("No solution for puzzle found :(")
}