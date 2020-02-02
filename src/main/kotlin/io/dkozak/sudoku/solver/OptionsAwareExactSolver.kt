package io.dkozak.sudoku.solver

import io.dkozak.sudoku.model.OptionsAwareSudokuCell
import io.dkozak.sudoku.model.OptionsAwareSudokuPuzzle
import io.dkozak.sudoku.model.utils.isFilled
import mu.KotlinLogging
import java.util.*

private val logger = KotlinLogging.logger { }

/**
 * value for OptionsAwareExactSolver.knownLocations
 * no known location yet
 */
private val EMPTY = -1 to -1
/**
 * value for OptionsAwareExactSolver.knownLocations
 * ignore this number -> it is either set or there is more than one location where it can go
 */
private val IGNORE = -2 to -2

/**
 * value for OptionsAwareExactSolver.knownLocations
 * signals that only one locations is possible for this number
 */
private fun Pair<Int, Int>.oneKnownLocation() = first >= 0


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

    /**
     * queue of cells to be updated
     * todo maybe just update them directly instead of keeping the queue?
     */
    val queue: Queue<Triple<Int, Int, Byte>> = LinkedList()

    /**
     * keeps track of possible locations for each number 1..size
     */
    val knownLocations = Array(initialPuzzle.size) { EMPTY }

    override fun solve(puzzle: OptionsAwareSudokuPuzzle): OptionsAwareSudokuPuzzle? {
        scanPuzzle(puzzle)
        while (queue.isNotEmpty()) {
            while (queue.isNotEmpty()) {
                val (row, col, value) = queue.poll()
                if (puzzle[row, col].isFilled()) continue
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
        if (cell.isFilled()) return
        val options = cell.allOptions()
        if (options.size == 1) {
            val option = options.first()
            queue.add(Triple(row, col, option))
            knownLocations[option - 1] = IGNORE
        } else for (option in options) {
            if (knownLocations[option - 1] == EMPTY)
                knownLocations[option - 1] = row to col
            else if (knownLocations[option - 1].oneKnownLocation())
                knownLocations[option - 1] = IGNORE
        }
    }

    private fun processKnownLocations() {
        for ((i, slot) in knownLocations.withIndex()) {
            if (knownLocations[i].oneKnownLocation()) {
                queue.add(Triple(slot.first, slot.second, (i + 1).toByte()))
            }
            knownLocations[i] = EMPTY
        }
    }

    private fun scanPuzzle(puzzle: OptionsAwareSudokuPuzzle) {

        for ((row, col, cell) in puzzle.allCellsIndexed()) {
            if (row == 0) {
                puzzle.colIndexed(col).forEach { processCell(it.first, it.second, it.third) }
                processKnownLocations()
            }
            if (col == 0) {
                puzzle.rowIndexed(row).forEach { processCell(it.first, it.second, it.third) }
                processKnownLocations()
            }

            if (row % puzzle.regionSize == 0 && col % puzzle.regionSize == 0) {
                puzzle.regionIndexed(row, col).forEach { processCell(it.first, it.second, it.third) }
                processKnownLocations()
            }

            val onlyOption = cell.onlyOption
            if (onlyOption != (-1).toByte() && cell.isEmpty()) {
                queue.add(Triple(row, col, onlyOption))
            }
        }
    }
}