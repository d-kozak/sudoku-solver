package io.dkozak.sudoku.solver

import io.dkozak.sudoku.io.loadPuzzle
import io.dkozak.sudoku.model.OptionsAwareSudokuPuzzle
import org.junit.jupiter.api.Test

class OptionsAwareDfsSolverTest {
    @Test
    fun `load first new`() {
        val puzzle = loadPuzzle("src/test/resources/puzzles/first.sudoku", ::OptionsAwareSudokuPuzzle)
        val solution = OptionsAwareDfsSolver(puzzle).solve()
        solution?.validateOrFail(false) ?: org.junit.jupiter.api.fail("solution should be found")
        println(solution)
    }

    @Test
    fun `load second new`() {
        val puzzle = loadPuzzle("src/test/resources/puzzles/second.sudoku", ::OptionsAwareSudokuPuzzle)
        val solution = OptionsAwareDfsSolver(puzzle).solve()
        solution?.validateOrFail(false) ?: org.junit.jupiter.api.fail("solution should be found")
        println(solution)
    }

    @Test
    fun `solve empty`() {
        val puzzle = loadPuzzle("src/test/resources/puzzles/empty.sudoku", ::OptionsAwareSudokuPuzzle)
        val solution = OptionsAwareDfsSolver(puzzle).solve()
        solution?.validateOrFail(false) ?: org.junit.jupiter.api.fail("solution should be found")
        println(solution)
    }
}