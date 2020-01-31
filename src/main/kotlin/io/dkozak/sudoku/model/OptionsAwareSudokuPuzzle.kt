package io.dkozak.sudoku.model

import mu.KotlinLogging
import java.util.*

/**
 * Representation of a sudoku cell which stores all possible values that can be assigned to the cell.
 */
class OptionsAwareSudokuCell(val size: Int) : SudokuCell {

    companion object {
        /**
         * Creates an empty cell
         */
        fun empty(size: Int): OptionsAwareSudokuCell {
            val cell = OptionsAwareSudokuCell(size)
            for (i in 0 until size) {
                cell.content.set(i)
            }
            return cell
        }
    }

    override val isEmpty: Boolean
        get() = !isSet

    /**
     * Each assignable value has it's bit set to 1
     */
    val content: BitSet = BitSet(size)

    /**
     * true if the value has been assigned
     */
    var isSet = false

    override fun clear() {
        content.clear()
        isSet = false
    }

    /**
     *
     */
    override var value: Int
        set(value) {
            content.clear()
            isSet = true
            content.set(value - 1)
        }
        get() {
            var res: Int = -1
            for (i in 0 until size)
                if (content[i]) {
                    if (res != -1) return -1
                    res = i + 1
                }
            return res
        }


    /**
     * returns all assignable numbers in a list
     */
    fun allOptions(): List<Int> {
        val res = mutableListOf<Int>()
        for (i in 0 until size) {
            if (content[i]) res.add(i + 1)
        }
        return res
    }


    override fun toString(): String = if (isEmpty) " " else value.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OptionsAwareSudokuCell) return false

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value
    }
}

private val logger = KotlinLogging.logger { }

/**
 * In this representation, all possible options for each cell are stored
 */
class OptionsAwareSudokuPuzzle(override val content: Array<Array<OptionsAwareSudokuCell>>) : SudokuPuzzle<OptionsAwareSudokuCell> {

    constructor(size: Int) : this(Array(size) { Array(size) { OptionsAwareSudokuCell.empty(size) } })

    override val size = content.size

    override val regionSize: Int = Math.sqrt(size.toDouble()).toInt()

    init {
        validateOrFail(true)
    }

    /**
     * The set as more complex, as it has to remove the inserted number from other cells in the row, col and region
     */
    override fun set(row: Int, col: Int, value: Int) {
        for (cell in row(row))
            cell.content.clear(value - 1)
        for (cell in col(col))
            cell.content.clear(value - 1)
        for (cell in region(row, col))
            cell.content.clear(value - 1)
        content[row][col].value = value
    }

    /**
     * Makes a deep copy of the puzzle
     */
    fun copy(): OptionsAwareSudokuPuzzle {
        val res = OptionsAwareSudokuPuzzle(size)
        for ((row, col, cell) in allCellsIndexed()) {
            if (cell.isSet)
                res[row, col] = cell.value
        }
        return res
    }


    override fun toString(): String {
        return "OptionsAwareSudokuPuzzle\n ${toPrintableString()}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OptionsAwareSudokuPuzzle) return false

        if (!content.contentDeepEquals(other.content)) return false

        return true
    }

    override fun hashCode(): Int {
        return content.contentDeepHashCode()
    }


}


/**
 * Utility, converts OptionsAwareSudokuPuzzle to SimpleSudokuPuzzle
 */
fun OptionsAwareSudokuPuzzle.toSimpleSudokuPuzzle(): SimpleSudokuPuzzle {
    val puzzle = SimpleSudokuPuzzle(this.size)

    for ((i, j, cell) in allCellsIndexed()) {
        puzzle[i, j] = cell.value
    }

    return puzzle
}
