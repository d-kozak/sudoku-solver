package io.dkozak.sudoku.model.utils

import io.dkozak.sudoku.model.SudokuCell

fun SudokuCell.isFilled() = !isEmpty()