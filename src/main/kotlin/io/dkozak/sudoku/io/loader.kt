package io.dkozak.sudoku.io

import io.dkozak.sudoku.model.SudokuPuzzle
import java.io.File


fun <PuzzleType : SudokuPuzzle<*>> loadPuzzle(path: String, puzzleFactory: (Int) -> PuzzleType): PuzzleType {
    val lines = File(path).readLines()
    val puzzleSize = lines.size
    val puzzle = puzzleFactory(puzzleSize)
    for ((i, line) in lines.withIndex()) {
        for ((j, cell) in line.withIndex()) {
            if (cell != ' ') puzzle[i, j] = cell - '0'
        }
    }
    return puzzle
}

