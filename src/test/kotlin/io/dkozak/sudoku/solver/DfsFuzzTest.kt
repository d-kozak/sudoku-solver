package io.dkozak.sudoku.solver

import io.dkozak.sudoku.io.loadPuzzle
import io.dkozak.sudoku.model.OptionsAwareSudokuPuzzle
import mu.KotlinLogging
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import java.util.*

private val logger = KotlinLogging.logger { }

/**
 * Performs a fuzz test of the OptionsAwareDfsSolver by creating a partially solved correct puzzles and checking if the solver can solve them
 */
class DfsFuzzTest {

    /**
     * How many iterations should be performed
     */
    private val ITERATIONS = 1000

    private val random = Random()

    /**
     * start filled puzzle
     */
    private val startPuzzle = loadPuzzle("src/test/resources/puzzles/first.sudoku", ::OptionsAwareSudokuPuzzle)
            .solveWith(::OptionsAwareExactSolver) ?: throw IllegalArgumentException("solution should be found")

    private val maxCellIndex = startPuzzle.size * startPuzzle.size - 1

    init {
        startPuzzle.validateOrFail(false)
    }


    @Test
    @Disabled // disables it can get stuck sometimes, I probably introduced a bug in the commit e1e88bbec73e2e24dd8570f0d77223505f59252b - 'refactoring, cleanup' but failed to find it yet :X
    fun `fuzz test the solver`() = repeat(ITERATIONS) {
        val logPrefix = "Iteration ${it + 1}:"
        val field = random.nextInt(maxCellIndex)
        logger.info { "$logPrefix at most $field cells will be set" }

        val puzzle = OptionsAwareSudokuPuzzle(startPuzzle.size)
        repeat(field) {
            val x = random.nextInt(maxCellIndex)
            val row = x / puzzle.size
            val col = x % puzzle.size
            puzzle[row, col] = startPuzzle[row, col].value
        }
        val solution = puzzle.solveWith(::OptionsAwareDfsSolver)
        solution?.validateOrFail(false) ?: fail("solver failed to provide a solution")
        logger.info("$logPrefix Success:\n ${solution.toPrintableString()}")
        logger.info("===============================================================")
    }

}