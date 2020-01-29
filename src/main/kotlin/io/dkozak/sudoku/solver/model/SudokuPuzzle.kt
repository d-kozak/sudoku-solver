package io.dkozak.sudoku.solver.model


data class SudokuPuzzle(
        val rows: Array<IntArray> = Array(DEFAULT_SIZE) { IntArray(DEFAULT_SIZE) }
) {

    val puzzleSize: Int = rows.size

    companion object {
        const val DEFAULT_SIZE = 9
    }

    init {
        val errors = validate()
        if (errors.isNotEmpty()) throw IllegalStateException(errors.toString())
    }


    fun isValid(): Boolean = validate().isEmpty()

    fun validate(): List<String> {
        val errors = mutableListOf<String>()
        for ((i, row) in rows.withIndex()) {
            if (row.size != puzzleSize) errors.add("The size of ${i}th row is not $puzzleSize ")
            for ((j, num) in row.withIndex()) {
                if (num !in 0..9) errors.add("Cell at [$i][$j] contains number $num, which is not from 1..9")
            }
        }
        return errors
    }

    override fun toString(): String = buildString {
        appendln("Sudoku Puzzle:")
        for ((i, row) in rows.withIndex()) {
            if (i % 3 == 0) {
                repeat(puzzleSize + puzzleSize / 3 - 2) {
                    append("---")
                }
                appendln()
            }
            for ((j, num) in row.withIndex()) {
                if (j % 3 == 0) append("|")
                append(' ')
                append(if (num != 0) num else ' ')
                append(' ')

            }
            appendln("|")
        }
        repeat(puzzleSize + puzzleSize / 3 - 2) {
            append("---")
        }
        appendln()
    }


}