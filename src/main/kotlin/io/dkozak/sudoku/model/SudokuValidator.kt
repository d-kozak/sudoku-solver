package io.dkozak.sudoku.model


/**
 * Validation logic for sudoku puzzle
 *
 */
internal class SudokuValidator<CellType : SudokuCell>(val puzzle: SudokuPuzzle<CellType>, val allowEmptyCells: Boolean = false) {
    val errors = mutableListOf<String>()

    fun validateSequence(prefix: String, sequence: Sequence<Triple<Int, Int, CellType>>) {
        val numLocations = mutableMapOf<Int, MutableSet<Pair<Int, Int>>>()
        for ((i, j, cell) in sequence) {
            if (cell.isEmpty && !allowEmptyCells)
                errors.add("$prefix: [$i][$j] is empty")
            else if (!cell.isEmpty) {
                numLocations.computeIfAbsent(cell.value) { mutableSetOf() }
                        .add(i to j)
            }
        }

        for ((num, locations) in numLocations) {
            if (locations.size > 1)
                errors.add("$prefix: Number ${num + 1} is present in multiple cells $locations")
        }
    }


    fun validate(): List<String> {

        if (Math.sqrt(puzzle.size.toDouble()).toInt() != puzzle.regionSize)
            errors.add("Region size ${puzzle.regionSize} shoudl be sqrt of size ${puzzle.size}")

        for ((i, row) in puzzle.content.withIndex()) {
            if (row.size != puzzle.size)
                errors.add("Row $i does not have expected size ${puzzle.size}")
        }

        for (i in 0 until puzzle.size) {
            validateSequence("Row ${i + 1}", puzzle.rowIndexed(i))
            validateSequence("Col ${i + 1}", puzzle.colIndexed(i))
        }
        for ((i, j) in puzzle.allRegions())
            validateSequence("Region [${i + 1}..${i + puzzle.regionSize}][${j + 1}..${j + puzzle.regionSize}]", puzzle.regionIndexed(i, j))
        return errors
    }
}