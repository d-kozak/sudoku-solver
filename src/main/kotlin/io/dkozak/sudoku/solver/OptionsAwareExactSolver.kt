package io.dkozak.sudoku.solver

import io.dkozak.sudoku.model.OptionsAwareSudokuCell
import io.dkozak.sudoku.model.OptionsAwareSudokuPuzzle
import io.dkozak.sudoku.model.utils.isNotEmpty
import mu.KotlinLogging
import java.util.*

private val logger = KotlinLogging.logger { }

/**
 * Tries to solve the puzzle without any guessing by using the following rules
 * 1) if there is a cell with only one option left, set it
 * 2) if there is only one option for a given number in a row, col or region, set it
 */
class OptionsAwareExactSolver(override val initialPuzzle: OptionsAwareSudokuPuzzle) : SudokuSolver<OptionsAwareSudokuCell, OptionsAwareSudokuPuzzle> {

    var printPartialSolution: Boolean = false

    constructor(initialPuzzle: OptionsAwareSudokuPuzzle, printPartialSolution: Boolean) : this(initialPuzzle) {
        this.printPartialSolution = printPartialSolution
    }

    val queue: Queue<Triple<Int, Int, Byte>> = LinkedList()
    val numSlots = Array(initialPuzzle.size) { -1 to -1 }

    override fun solve(puzzle: OptionsAwareSudokuPuzzle): OptionsAwareSudokuPuzzle? {
        scanPuzzle(puzzle)
        while (queue.isNotEmpty()) {
            while (queue.isNotEmpty()) {
                val (row, col, value) = queue.poll()
                if (puzzle[row, col].isNotEmpty()) continue
                puzzle[row, col] = value
            }
            scanPuzzle(puzzle)
        }

        return if (puzzle.isValid(false)) puzzle else {
            if (printPartialSolution) logger.info { "Managed to reach partially completed puzzle:\n ${puzzle.toPrintableString()}" }
            null
        }
    }

    private fun processCell(row: Int, col: Int, cell: OptionsAwareSudokuCell) {
        if (cell.isNotEmpty()) return
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
                queue.add(Triple(slot.first, slot.second, (i + 1).toByte()))
            }
            numSlots[i] = -1 to -1
        }
    }

    private fun scanPuzzle(puzzle: OptionsAwareSudokuPuzzle) {

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

            val onlyOption = cell.onlyOption
            if (onlyOption != (-1).toByte() && cell.isEmpty()) {
                queue.add(Triple(row, col, onlyOption))
            }
        }
    }
}