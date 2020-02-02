package io.dkozak.sudoku.model

import io.dkozak.sudoku.model.utils.isFilled


/**
 * Validation logic for sudoku puzzle
 *
 * Conditions:
 * 1) the region size (which is the square root of the puzzle size) has to be an int
 * 2) the puzzle has to be in a square shape
 * 3) the should be no conflicts in rows, cols and regions
 * 4) (optional) empty cells (puzzle is not yet fully solved) might or might not be allowed
 */
internal class SudokuValidator<CellType : SudokuCell>(val puzzle: SudokuPuzzle<CellType>, val allowEmptyCells: Boolean = false) {
    /**
     * All encountered errors
     */
    val errors = mutableListOf<String>()

    fun validate(): List<String> {
        checkRegionSize()
        checkRowSize()
        checkRowsAndCols()
        checkRegions()
        return errors
    }

    private fun checkRegions() {
        for ((i, j) in puzzle.allRegions())
            validateSequence("Region [${i + 1}..${i + puzzle.regionSize}][${j + 1}..${j + puzzle.regionSize}]", puzzle.regionIndexed(i, j))
    }

    private fun checkRowsAndCols() {
        for (i in 0 until puzzle.size) {
            validateSequence("Row ${i + 1}", puzzle.rowIndexed(i))
            validateSequence("Col ${i + 1}", puzzle.colIndexed(i))
        }
    }

    private fun checkRowSize() {
        for ((i, row) in puzzle.content.withIndex()) {
            if (row.size != puzzle.size)
                errors.add("Row $i does not have expected size ${puzzle.size}")
        }
    }

    private fun checkRegionSize() {
        if (Math.sqrt(puzzle.size.toDouble()).toInt() != puzzle.regionSize)
            errors.add("Region size ${puzzle.regionSize} shoudl be sqrt of size ${puzzle.size}")
    }

    private fun validateSequence(prefix: String, sequence: Sequence<Triple<Int, Int, CellType>>) {
        val numLocations = mutableMapOf<Byte, MutableSet<Pair<Int, Int>>>()
        for ((i, j, cell) in sequence) {
            if (cell.isEmpty() && !allowEmptyCells)
                errors.add("$prefix: [$i][$j] is empty")
            else if (cell.isFilled()) {
                numLocations.computeIfAbsent(cell.value) { mutableSetOf() }
                        .add(i to j)
            }
        }

        for ((num, locations) in numLocations) {
            if (locations.size > 1)
                errors.add("$prefix: Number ${num + 1} is present in multiple cells $locations")
        }
    }

}