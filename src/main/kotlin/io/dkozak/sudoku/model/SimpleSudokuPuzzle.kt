package io.dkozak.sudoku.model

/**
 * Simple sudoku cell, holding only the integer value
 */
data class SimpleSudokuCell(override var value: Byte = -1) : SudokuCell {
    override val isEmpty: Boolean
        get() = value == (-1).toByte()

    override fun toString(): String = if (isEmpty) " " else value.toString()

    override fun clear() {
        value = -1
    }
}

/**
 * Simple representation, keeping only the integer values in each cell
 */
data class SimpleSudokuPuzzle(
        /**
         * Individual rows of the puzzle
         */
        override val content: Array<Array<SimpleSudokuCell>>
) : SudokuPuzzle<SimpleSudokuCell> {


    constructor(sudokuSize: Int) : this(Array(sudokuSize) { Array(sudokuSize) { SimpleSudokuCell() } })

    override val size: Int = content.size

    override val regionSize: Int = Math.sqrt(size.toDouble()).toInt()

    override fun set(row: Int, col: Int, value: Byte) {
        content[row][col].value = value
    }

    init {
        validateOrFail(true)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SimpleSudokuPuzzle) return false

        if (!content.contentDeepEquals(other.content)) return false

        return true
    }

    override fun hashCode(): Int {
        return content.contentDeepHashCode()
    }

    override fun toString(): String {
        return "SimpleSudokuPuzzle\n ${toPrintableString()}"
    }

}