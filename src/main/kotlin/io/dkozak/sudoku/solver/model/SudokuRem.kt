package io.dkozak.sudoku.solver.model

import java.io.File
import java.util.*
import kotlin.math.sqrt

private fun SudokuRem.toSudokuPuzzle(): SudokuPuzzle {
    val rows = Array(size) { IntArray(size) }

    forEachCellIndexed { i, j, cell ->
        rows[i][j] = cell.finalValue + 1
    }

    return SudokuPuzzle(rows)
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
    sudoku.validate()
    return sudoku
}

fun solve(puzzle: SudokuRem): SudokuRem {
    val queue: Queue<Triple<Int, Int, Int>> = LinkedList()

    puzzle.forEachCellIndexed { row, col, cell ->
        val option = cell.value
        if (option != null) {
            queue.add(Triple(row, col, option))
        }
    }


    while (queue.isNotEmpty()) {
        val (row, col, value) = queue.poll()
        puzzle[row, col] = value
        puzzle.forRowIndexed(row) { row, col, cell ->
            val option = cell.value
            if (option != null) {
                queue.add(Triple(row, col, option))
            }
        }
        puzzle.forColIndexed(col) { row, col, cell ->
            val option = cell.value
            if (option != null) {
                queue.add(Triple(row, col, option))
            }
        }

        puzzle.forRegionIndexed(row, col) { row, col, cell ->
            val option = cell.value
            if (option != null) {
                queue.add(Triple(row, col, option))
            }
        }
    }

    puzzle.validate()
    println(puzzle.toSudokuPuzzle())
    return puzzle
}


data class SudokuCell(val content: BitSet, var finalValue: Int = -1) {
    constructor(value: Int, size: Int) : this(BitSet(size).also { it.set(value) }, size)

    val size: Int = content.size()

    companion object {
        fun empty(size: Int): SudokuCell {
            val bitSet = BitSet()
            for (i in 0 until size) {
                bitSet.set(i)
            }
            return SudokuCell(bitSet)
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

class SudokuRem(val content: Array<Array<SudokuCell>>) {

    constructor(size: Int) : this(Array(size) { Array(size) { SudokuCell.empty(size) } })

    val size = content.size

    val regionSize: Int

    init {
        val regionSize = sqrt(size.toDouble())
        check(regionSize.toInt().toDouble() == regionSize) { "$size cannot be squared exactly, result was $regionSize" }
        this.regionSize = regionSize.toInt()

        validate()
    }

    fun validate() {
        val errors = SudokuValidator(this).validate()
        if (errors.isNotEmpty()) throw IllegalStateException(errors.toString())
    }


    fun forEachCell(block: (SudokuCell) -> Unit) {
        forEachCellIndexed { _, _, sudokuCell -> block(sudokuCell) }
    }

    fun forEachCellIndexed(block: (Int, Int, SudokuCell) -> Unit) {
        for (row in 0 until size) {
            for (col in 0 until size) {
                block(row, col, content[row][col])
            }
        }
    }

    operator fun set(row: Int, col: Int, value: Int) {
        forRow(row) { it.content.clear(value - 1) }
        forCol(col) { it.content.clear(value - 1) }
        forRegion(row, col) { it.content.clear(value - 1) }
        content[row][col].set(value)
    }

    fun forRow(row: Int, block: (SudokuCell) -> Unit) {
        forRowIndexed(row) { _, _, cell -> block(cell) }
    }

    fun forCol(col: Int, block: (SudokuCell) -> Unit) {
        forColIndexed(col) { _, _, cell -> block(cell) }
    }

    fun forRegion(row: Int, col: Int, block: (SudokuCell) -> Unit) {
        forRegionIndexed(row, col) { _, _, cell -> block(cell) }
    }

    fun forRowIndexed(row: Int, block: (Int, Int, SudokuCell) -> Unit) {
        check(row in 0 until size) { "$row out of bounds, [0,$size)" }
        for ((i, cell) in content[row].withIndex())
            block(row, i, cell)
    }

    fun forColIndexed(col: Int, block: (Int, Int, SudokuCell) -> Unit) {
        check(col in 0 until size) { "$col out of bounds, [0,$size)" }
        for ((i, row) in content.withIndex())
            block(i, col, row[col])
    }

    fun forRegionIndexed(row: Int, col: Int, block: (Int, Int, SudokuCell) -> Unit) {
        val x = (row / regionSize) * regionSize
        val y = (col / regionSize) * regionSize
        for (i in x until x + regionSize)
            for (j in y until y + regionSize)
                block(i, j, content[i][j])
    }

    fun isValid(): Boolean = SudokuValidator(this).isValid()


}

private class SudokuValidator(val puzzle: SudokuRem) {
    val errors = mutableSetOf<String>()

    fun isValid(): Boolean = validate().isEmpty()

    fun validateSequence(prefix: String, callback: ((Int, Int, SudokuCell) -> Unit) -> Unit) {
        val numLocations = mutableMapOf<Int, MutableSet<Pair<Int, Int>>>()
        callback { i, j, cell ->
            val options = cell.options
            for (option in options) {
                val numOptions = numLocations.computeIfAbsent(option) { mutableSetOf() }
                numOptions.add(i to j)
            }
            if (options.isEmpty()) {
                errors.add("$prefix: [$i][$j] : cell has no options")
            }
        }

        for ((num, locations) in numLocations) {
            if (locations.size > 1)
                errors.add("$prefix: Number $num is present in multiple cells $locations")
        }
    }


    fun validate(): List<String> {
        val errors = mutableListOf<String>()
        for ((i, row) in puzzle.content.withIndex()) {
            if (row.size != puzzle.size) errors.add("Row $i does not have expected size ${puzzle.size}, it has ${row.size}")
        }

        for (i in 0 until puzzle.size) {
            validateSequence("Row $i") { block -> puzzle.forRowIndexed(i, block) }
            validateSequence("Col $i") { block -> puzzle.forColIndexed(i, block) }
        }

        for (i in 0 until puzzle.size step puzzle.regionSize)
            for (j in 0 until puzzle.size step puzzle.regionSize)
                validateSequence("Region [$i..${i + puzzle.regionSize}][$j..${j + puzzle.regionSize}]") { block -> puzzle.forRegionIndexed(i, j, block) }

        return errors
    }
}