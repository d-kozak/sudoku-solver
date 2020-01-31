package io.dkozak.sudoku.solver

import io.dkozak.sudoku.model.OptionsAwareSudokuCell
import io.dkozak.sudoku.model.OptionsAwareSudokuPuzzle
import java.util.*


/**
 * Tries to solve the puzzle without any guessing by using the following rules
 * 1) if there is a cell with only one option left, set it
 * 2) if there is only one option for a given number in a row, col or region, set it
 */
class OptionsAwareExactSolver(val puzzle: OptionsAwareSudokuPuzzle) {
    val queue: Queue<Triple<Int, Int, Int>> = LinkedList()
    val numSlots = Array(puzzle.size) { -1 to -1 }

    fun solve(): OptionsAwareSudokuPuzzle? {
        scanPuzzle()
        while (queue.isNotEmpty()) {
            while (queue.isNotEmpty()) {
                val (row, col, value) = queue.poll()
                if (puzzle[row, col].isSet) continue
                puzzle[row, col] = value
            }
            scanPuzzle()
        }

        return if (puzzle.isValid(false)) puzzle else null
    }

    private fun processCell(row: Int, col: Int, cell: OptionsAwareSudokuCell) {
        if (cell.isSet) return
        val options = cell.allOptions()
        if (options.size == 1) {
            val option = options.first()
            queue.add(Triple(row, col, option))
            numSlots[option - 1] = -2 to -2
        } else for (option in options) {
            if (numSlots[option - 1].first == -1)
                numSlots[option - 1] = row to col
            else if (numSlots[option - 1].first >= 0)
                numSlots[option - 1] = -2 to -2
        }
    }

    private fun processNumSlots() {
        for ((i, slot) in numSlots.withIndex()) {
            if (numSlots[i].first >= 0) {
                queue.add(Triple(slot.first, slot.second, i + 1))
            }
            numSlots[i] = -1 to -1
        }
    }

    private fun scanPuzzle() {

        for ((row, col, cell) in puzzle.allCellsIndexed()) {
            if (row == 0) {
                puzzle.colIndexed(col).forEach { processCell(it.first, it.second, it.third) }
                processNumSlots()
            }
            if (col == 0) {
                puzzle.rowIndexed(row).forEach { processCell(it.first, it.second, it.third) }
                processNumSlots()
            }

            if (row % puzzle.regionSize == 0 && col % puzzle.regionSize == 0) {
                puzzle.regionIndexed(row, col).forEach { processCell(it.first, it.second, it.third) }
                processNumSlots()
            }

            val option = cell.value
            if (option != -1 && !cell.isSet) {
                queue.add(Triple(row, col, option))
            }
        }
    }
}