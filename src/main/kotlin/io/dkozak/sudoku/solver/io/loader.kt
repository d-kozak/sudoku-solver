package io.dkozak.sudoku.solver.io

import io.dkozak.sudoku.solver.model.SudokuPuzzle
import java.io.File


fun loadPuzzle(path: String): SudokuPuzzle {
    val lines = File(path).readLines()
    val puzzleSize = lines.size
    val content = Array(puzzleSize) { IntArray(puzzleSize) }
    for ((i, line) in lines.withIndex()) {
        for ((j, cell) in line.withIndex()) {
            content[i][j] = if (cell != ' ') cell - '0' else 0
        }
    }
    return SudokuPuzzle(content)
}

