package io.dkozak.sudoku.model

import io.dkozak.sudoku.model.utils.isFilled
import mu.KotlinLogging
import java.util.*
import kotlin.math.sqrt

/**
 * Representation of a sudoku cell which stores all possible values that can be assigned to the cell.
 */
class OptionsAwareSudokuCell(val size: Byte) : SudokuCell {

    companion object {
        /**
         * Creates an empty cell
         */
        fun empty(size: Byte): OptionsAwareSudokuCell {
            val cell = OptionsAwareSudokuCell(size)
            for (i in 0 until size) {
                cell.content.set(i)
            }
            return cell
        }
    }

    /**
     * Each assignable value has it's bit set to 1
     */
    val content: BitSet = BitSet(size.toInt())

    override var value: Byte = -1

    /**
     * @return the only possible option that can be assigned to the cell based on the content of the bitset
     * or -1, if the number of options != 1
     */
    val onlyOption: Byte
        get() = if (isEmpty() && content.cardinality() == 1) (content.nextSetBit(0) + 1).toByte() else -1

    /**
     * returns all assignable options in a list
     */
    fun allOptions(): List<Byte> {
        check(isEmpty()) { "options for a filled cell should never be enumerated, there is no need for that" }
        val res = mutableListOf<Byte>()
        for (i in 0 until size) {
            if (content[i]) res.add((i + 1).toByte())
        }
        return res
    }


    override fun toString(): String = if (isEmpty()) " " else value.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OptionsAwareSudokuCell) return false

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value.toInt()
    }
}

private val logger = KotlinLogging.logger { }

/**
 * In this representation, all possible options for each cell are stored
 */
class OptionsAwareSudokuPuzzle(override val content: Array<Array<OptionsAwareSudokuCell>>) : SudokuPuzzle<OptionsAwareSudokuCell> {

    constructor(size: Int) : this(Array(size) { Array(size) { OptionsAwareSudokuCell.empty(size.toByte()) } })

    override val size = content.size

    override val regionSize: Int = sqrt(size.toDouble()).toInt()

    init {
        validateOrFail(true)
    }

    /**
     * The set as more complex, as it has to remove the inserted number from other cells in the row, col and region
     */
    override fun set(row: Int, col: Int, value: Byte) {
        check(value in 1..size) { "value $value should be within range 1..${size}" }
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
            if (cell.isFilled())
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
