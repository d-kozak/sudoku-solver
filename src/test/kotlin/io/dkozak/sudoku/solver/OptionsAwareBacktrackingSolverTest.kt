package io.dkozak.sudoku.solver

import io.dkozak.sudoku.io.loadPuzzle
import io.dkozak.sudoku.model.OptionsAwareSudokuPuzzle
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail


class OptionsAwareBacktrackingSolverTest {

    @Test
    fun `load first new`() {
        val puzzle = loadPuzzle("src/test/resources/puzzles/first.sudoku", ::OptionsAwareSudokuPuzzle)
        val solution = OptionsAwareBacktrackingSolver(puzzle).solve()
        solution?.validateOrFail(false) ?: fail("solution should be found")
        println(solution)
    }

    @Test
    fun `load second new`() {
        val puzzle = loadPuzzle("src/test/resources/puzzles/second.sudoku", ::OptionsAwareSudokuPuzzle)
        val solution = OptionsAwareBacktrackingSolver(puzzle).solve()
        solution?.validateOrFail(false) ?: fail("solution should be found")
        println(solution)
    }
}