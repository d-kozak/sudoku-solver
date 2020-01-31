package io.dkozak.sudoku.io

import io.dkozak.sudoku.model.SudokuPuzzle
import java.io.File

/***
 * Loads a Sudoku puzzle from a given file
 * @param path from where to take the file
 * @param puzzleFactory factory function to provide a specific implementation of SudokuPuzzle
 */
fun <PuzzleType : SudokuPuzzle<*>> loadPuzzle(path: String, puzzleFactory: (Int) -> PuzzleType): PuzzleType {
    val lines = File(path).readLines()
    val puzzleSize = lines.size
    val puzzle = puzzleFactory(puzzleSize)
    for ((i, line) in lines.withIndex())
        for ((j, cell) in line.withIndex())
            if (cell != ' ') puzzle[i, j] = cell - '0'


    return puzzle
}

