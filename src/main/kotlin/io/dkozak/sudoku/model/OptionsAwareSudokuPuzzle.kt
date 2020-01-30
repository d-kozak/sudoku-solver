package io.dkozak.sudoku.model

import mu.KotlinLogging
import java.util.*

fun OptionsAwareSudokuPuzzle.toSimpleSudokuPuzzle(): SimpleSudokuPuzzle {
    val puzzle = SimpleSudokuPuzzle(this.size)

    for ((i, j, cell) in allCellsIndexed()) {
        puzzle[i, j] = cell.value
    }

    return puzzle
}

class OptionsAwareSudokuCell(val size: Int) : SudokuCell {

    override val isEmpty: Boolean
        get() = !isSet

    override fun clear() {
        content.clear()
        isSet = false
    }

    val content: BitSet = BitSet(size)

    companion object {
        fun empty(size: Int): OptionsAwareSudokuCell {
            val cell = OptionsAwareSudokuCell(size)
            for (i in 0 until size) {
                cell.content.set(i)
            }
            return cell
        }
    }

    var isSet = false

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


    fun allOptions(): List<Int> {
        val res = mutableListOf<Int>()
        for (i in 0 until size) {
            if (content[i]) res.add(i + 1)
        }
        return res
    }

    override fun toString(): String = if (isEmpty) " " else value.toString()
}

private val logger = KotlinLogging.logger { }

class OptionsAwareSudokuPuzzle(override val content: Array<Array<OptionsAwareSudokuCell>>) : SudokuPuzzle<OptionsAwareSudokuCell> {

    constructor(size: Int) : this(Array(size) { Array(size) { OptionsAwareSudokuCell.empty(size) } })

    override val size = content.size

    override val regionSize: Int = Math.sqrt(size.toDouble()).toInt()

    init {
        validateOrFail(true)
    }

    override fun set(row: Int, col: Int, value: Int) {
        logger.info { "Settings [$row][$col] to $value" }
        for (cell in row(row))
            cell.content.clear(value - 1)
        for (cell in col(col))
            cell.content.clear(value - 1)
        for (cell in region(row, col))
            cell.content.clear(value - 1)
        content[row][col].value = value
        validateOrFail(true)
        logger.info { "Success" }
    }
}
