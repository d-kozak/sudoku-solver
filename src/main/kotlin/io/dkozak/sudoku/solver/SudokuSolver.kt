package io.dkozak.sudoku.solver

import io.dkozak.sudoku.model.SudokuCell
import io.dkozak.sudoku.model.SudokuPuzzle
import mu.KotlinLogging

/**
 * Wrapper function that masks the solver creation and execution to make the code cleaner
 * Solvers need to have their local state sometimes, that's why objects are more suitable than functions,
 * however the client code should not be bothered with object creation
 */
fun <CellType : SudokuCell, PuzzleType : SudokuPuzzle<CellType>> PuzzleType.solveWith(solverFactory: (PuzzleType) -> SudokuSolver<CellType, PuzzleType>): PuzzleType? {
    val solver = solverFactory(this)
    logger.info { "Solver $solver solving puzzle\n ${this.toPrintableString()} \n" }
    return solver.solve(this)
}

private val logger = KotlinLogging.logger { }

/**
 * Generic interface for a sudoku solver solving a puzzle in a specific representation
 */
interface SudokuSolver<CellType : SudokuCell, PuzzleType : SudokuPuzzle<CellType>> {
    /**
     * The initial puzzle the solver is starting from
     */
    val initialPuzzle: PuzzleType

    /**
     * Solve a specific puzzle
     * @param puzzle to solve, defaults to the initial puzzle
     */
    fun solve(puzzle: PuzzleType = initialPuzzle): PuzzleType?
}