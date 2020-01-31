package io.dkozak.sudoku.solver

import io.dkozak.sudoku.io.loadPuzzle
import io.dkozak.sudoku.model.OptionsAwareSudokuPuzzle
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail


class OptionsAwareBfsSolverTest {

    @Test
    fun `solve first`() {
        val puzzle = loadPuzzle("src/test/resources/puzzles/first.sudoku", ::OptionsAwareSudokuPuzzle)
        val solution = OptionsAwareBfsSolver(puzzle).solve()
        solution?.validateOrFail(false) ?: fail("solution should be found")
        println(solution)
    }

    @Test
    fun `solve second`() {
        val puzzle = loadPuzzle("src/test/resources/puzzles/second.sudoku", ::OptionsAwareSudokuPuzzle)
        val solution = OptionsAwareBfsSolver(puzzle).solve()
        solution?.validateOrFail(false) ?: fail("solution should be found")
        println(solution)
    }

    @Test
    fun `solve empty`() {
        val puzzle = loadPuzzle("src/test/resources/puzzles/empty.sudoku", ::OptionsAwareSudokuPuzzle)
        val solution = OptionsAwareBfsSolver(puzzle).solve()
        solution?.validateOrFail(false) ?: fail("solution should be found")
        println(solution)
    }
}