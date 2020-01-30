package io.dkozak.sudoku.solver.model

import mu.KotlinLogging
import java.io.File
import java.util.*
import kotlin.math.sqrt

private fun SudokuRem.toSudokuPuzzle(): SimpleSudokuPuzzle {
    val puzzle = SimpleSudokuPuzzle(this.size)
    // todo finish
    return puzzle
}

fun loadSudokuRem(path: String): SudokuRem {
    val file = File(path)
    val rows = file.readLines()
    val sudoku = SudokuRem(rows.size)
    for ((i, row) in rows.withIndex()) {
        for ((j, cell) in row.withIndex()) {
            if (cell != ' ') sudoku[i, j] = cell - '0'
        }
    }
    sudoku.validate(true)
    return sudoku
}

fun solve(puzzle: SudokuRem): SudokuRem {
    val queue: Queue<Triple<Int, Int, Int>> = LinkedList()
    val numSlots = Array(puzzle.size) { -1 to -1 }

    fun processCell(row: Int, col: Int, cell: SudokuCellRem) {
        if (cell.finalValue != -1) return
        val options = cell.options
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

    fun processNumSlots() {
        for ((i, slot) in numSlots.withIndex()) {
            if (numSlots[i].first >= 0) {
                queue.add(Triple(slot.first, slot.second, i + 1))
            }
            numSlots[i] = -1 to -1
        }
    }

    fun scanPuzzle() {
        puzzle.forEachCellIndexed { row, col, cell ->
            if (row == 0) {
                puzzle.forColIndexed(col, ::processCell)
                processNumSlots()
            }
            if (col == 0) {
                puzzle.forRowIndexed(row, ::processCell)
                processNumSlots()
            }

            if (row % puzzle.regionSize == 0 && col % puzzle.regionSize == 0) {
                puzzle.forRegionIndexed(row, col, ::processCell)
                processNumSlots()
            }

            val option = cell.value
            if (option != null) {
                queue.add(Triple(row, col, option))
            }
        }
    }

    scanPuzzle()

    while (queue.isNotEmpty()) {
        while (queue.isNotEmpty()) {
            val (row, col, value) = queue.poll()
            if (puzzle[row, col].finalValue != -1) continue
            puzzle[row, col] = value
        }
        scanPuzzle()
    }

    puzzle.validate(false)
    println(puzzle.toSudokuPuzzle())
    return puzzle
}


data class SudokuCellRem(val content: BitSet, var finalValue: Int = -1) {
    constructor(value: Int, size: Int) : this(BitSet(size).also { it.set(value) }, size)

    val size: Int = content.size()

    companion object {
        fun empty(size: Int): SudokuCellRem {
            val bitSet = BitSet()
            for (i in 0 until size) {
                bitSet.set(i)
            }
            return SudokuCellRem(bitSet)
        }
    }

    fun set(value: Int) {
        finalValue = value - 1
        content.clear()
        content.set(value - 1)
    }

    val value: Int?
        get() {
            if (finalValue != -1) return null
            var res: Int? = null
            for (i in 0 until size)
                if (content[i]) {
                    if (res != null) return null
                    res = i + 1
                }
            return res
        }

    val options: Set<Int>
        get() {
            val res = mutableSetOf<Int>()
            for (i in 0 until size) {
                if (content[i]) res.add(i + 1)
            }
            return res
        }
}

private val logger = KotlinLogging.logger { }

class SudokuRem(val content: Array<Array<SudokuCellRem>>) {

    constructor(size: Int) : this(Array(size) { Array(size) { SudokuCellRem.empty(size) } })

    val size = content.size

    val regionSize: Int

    init {
        val regionSize = sqrt(size.toDouble())
        check(regionSize.toInt().toDouble() == regionSize) { "$size cannot be squared exactly, result was $regionSize" }
        this.regionSize = regionSize.toInt()

        validate(true)
    }

    fun validate(allowEmptyCell: Boolean) {
//        val errors = SudokuValidator(this, allowEmptyCell).validate()
//        if (errors.isNotEmpty()) throw IllegalStateException(this.toSudokuPuzzle().toString() + "\n" + errors.joinToString("\n"))
    }


    fun forEachCell(block: (SudokuCellRem) -> Unit) {
        forEachCellIndexed { _, _, sudokuCell -> block(sudokuCell) }
    }

    fun forEachCellIndexed(block: (Int, Int, SudokuCellRem) -> Unit) {
        for (row in 0 until size) {
            for (col in 0 until size) {
                block(row, col, content[row][col])
            }
        }
    }

    operator fun get(row: Int, col: Int) = content[row][col]

    operator fun set(row: Int, col: Int, value: Int) {
        logger.info { "Setting [$row][$col] to $value" }
        forRow(row) { it.content.clear(value - 1) }
        forCol(col) { it.content.clear(value - 1) }
        forRegion(row, col) { it.content.clear(value - 1) }
        content[row][col].set(value)
        validate(true)
    }

    fun forRow(row: Int, block: (SudokuCellRem) -> Unit) {
        forRowIndexed(row) { _, _, cell -> block(cell) }
    }

    fun forCol(col: Int, block: (SudokuCellRem) -> Unit) {
        forColIndexed(col) { _, _, cell -> block(cell) }
    }

    fun forRegion(row: Int, col: Int, block: (SudokuCellRem) -> Unit) {
        forRegionIndexed(row, col) { _, _, cell -> block(cell) }
    }

    fun forRowIndexed(row: Int, block: (Int, Int, SudokuCellRem) -> Unit) {
        check(row in 0 until size) { "$row out of bounds, [0,$size)" }
        for ((i, cell) in content[row].withIndex())
            block(row, i, cell)
    }

    fun forColIndexed(col: Int, block: (Int, Int, SudokuCellRem) -> Unit) {
        check(col in 0 until size) { "$col out of bounds, [0,$size)" }
        for ((i, row) in content.withIndex())
            block(i, col, row[col])
    }

    fun forRegionIndexed(row: Int, col: Int, block: (Int, Int, SudokuCellRem) -> Unit) {
        val x = (row / regionSize) * regionSize
        val y = (col / regionSize) * regionSize
        for (i in x until x + regionSize)
            for (j in y until y + regionSize)
                block(i, j, content[i][j])
    }

//    fun isValid(): Boolean = SudokuValidator(this).isValid()


}
