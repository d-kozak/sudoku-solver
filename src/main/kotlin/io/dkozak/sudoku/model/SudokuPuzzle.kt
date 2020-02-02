package io.dkozak.sudoku.model

import io.dkozak.sudoku.model.utils.isFilled

/**
 * Once cell in the puzzle
 */
interface SudokuCell {
    /**
     * value of the cell, -1 for empty cell
     */
    var value: Byte

    /**
     * empty cell check, just for convenience (not to have to compare with -1)
     */
    fun isEmpty(): Boolean = value == (-1).toByte()
}

/**
 * Representation of a sudoku puzzle
 */
interface SudokuPuzzle<CellType : SudokuCell> {

    /**
     * size of the puzzle
     */
    val size: Int

    /**
     * size of one region - should be sqrt of size
     */
    val regionSize: Int

    /**
     * the actual content of the puzzle
     */
    val content: Array<Array<CellType>>

    /**
     * get value at given coordinates
     */
    operator fun get(row: Int, col: Int): CellType = content[row][col]

    /**
     * set value at given coordinates
     */
    operator fun set(row: Int, col: Int, value: Byte)


    /**
     * all cells from the puzzle
     */
    fun allCells() = allCellsIndexed().map { it.third }

    /**
     * all cells in the puzzle with their position
     */
    fun allCellsIndexed(): Sequence<Triple<Int, Int, CellType>> = sequence {
        for (row in 0 until size)
            for (col in 0 until size)
                yield(Triple(row, col, this@SudokuPuzzle[row, col]))

    }


    /**
     * topleft corner cells from all regions
     */
    fun allRegions(): Sequence<Pair<Int, Int>> = sequence {
        for (row in 0 until size step regionSize) {
            for (col in 0 until size step regionSize) {
                yield(row to col)
            }
        }
    }

    /**
     * all cells at given row
     */
    fun row(row: Int) = rowIndexed(row).map { it.third }


    /**
     * all cells at given row with their position
     */
    fun rowIndexed(row: Int): Sequence<Triple<Int, Int, CellType>> = sequence {
        for (col in 0 until size)
            yield(Triple(row, col, this@SudokuPuzzle[row, col]))
    }

    fun col(col: Int) = colIndexed(col).map { it.third }

    /**
     * all cells at given column with their position
     */
    fun colIndexed(col: Int): Sequence<Triple<Int, Int, CellType>> = sequence {
        for (row in 0 until size)
            yield(Triple(row, col, this@SudokuPuzzle[row, col]))
    }

    fun region(row: Int, col: Int) = regionIndexed(row, col).map { it.third }

    /**
     * all cells at given region with their position
     */
    fun regionIndexed(row: Int, col: Int): Sequence<Triple<Int, Int, CellType>> = sequence {
        val startRow = (row / regionSize) * regionSize
        val startCol = (col / regionSize) * regionSize
        for (i in startRow until startRow + regionSize)
            for (j in startCol until startCol + regionSize)
                yield(Triple(i, j, this@SudokuPuzzle[i, j]))
    }

    /**
     * @param allowEmptyCells whether empty cells are allowed
     * @true if the puzzle is valid, that is all constraints are satisfied
     */
    fun isValid(allowEmptyCells: Boolean): Boolean = validate(allowEmptyCells).isEmpty()

    /**
     * @return list of found errors
     */
    fun validate(allowEmptyCells: Boolean): List<String> = SudokuValidator(this, allowEmptyCells).validate()


    /**
     * Tries to validate the puzzle
     * @throws IllegalStateException if any errors are found
     */
    fun validateOrFail(allowEmptyCells: Boolean) {
        val errors = validate(allowEmptyCells)
        if (errors.isNotEmpty()) {
            throw IllegalStateException(toPrintableString() + "\n" + errors.joinToString("\n"))
        }
    }

    /**
     * @return a set of candidate numbers for a specific cell
     */
    fun numbersFor(i: Int, j: Int): List<Byte> {
        val possibleNumbers = MutableList(size) { (it + 1).toByte() }
        for (cell in row(i))
            if (cell.isFilled()) possibleNumbers.remove(cell.value)
        for (cell in col(j)) {
            if (cell.isFilled()) possibleNumbers.remove(cell.value)
        }

        for (cell in region(i, j)) {
            if (cell.isFilled()) possibleNumbers.remove(cell.value)
        }
        return possibleNumbers
    }

    /**
     * Generates a human readable string that can for example be printed into the console.
     * toString() was not overrided, because interfaces are not allowed to overwrite methods
     * coming from Any.
     */
    fun toPrintableString(): String = buildString {
        for ((i, row) in content.withIndex()) {
            if (i % 3 == 0) {
                repeat(size + regionSize - 2) {
                    append("---")
                }
                appendln()
            }
            for ((j, cell) in row.withIndex()) {
                if (j % 3 == 0) append("|")
                append(' ')
                append(cell)
                append(' ')

            }
            appendln("|")
        }
        repeat(size + regionSize - 2) {
            append("---")
        }
        appendln()
    }

}


