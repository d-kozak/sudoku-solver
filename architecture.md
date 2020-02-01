# Architecture
This document contains the architecture of the solver and the decisions why it was made this way. 

## Implementation language
Even though the original requirement was to make it in Java, I afterwards negotiated it to Kotlin/JVM. Here is my reasoning behind that decision.
Of course, there  is no single language that is best in everything. Programming languages are just tools that developers use to solve problems.
So each of them might be better for one thing or worse for another. However, some languages are quite similar to each other, hence they are suitable for 
similar kinds of problems. And this is exactly the case with Java and Kotlin/JVM. And since these two languages are quite similar and 
can be used to solve similar problems, it actually makes sense to compare. And I find Kotlin superior to Java in many ways. Some of them are the following.
* Better support for functional programming
* Extension methods
* Cleaner and more consise code
* Type inference
* Very little boilerplate compared to Java
* Possibility to create internal DSLs thanks to lambda's with receivers

## General approach
My approach was iterative and prototype-based. I decided to implement a very simple inefficient solver first and then gradually 
improve it until the result was satisfying. Finally, I ended up with 4 different solving strategies using two different models of the puzzle.
One might argue that I could probably do a deeper analysis beforehand and then go directly for the most efficient approach I could figure out(which is what I would do at work),
 but there was a reason why I chose my approach. I took this task as a chance to exercise my programming skills, so I actually wanted to implement 
 multiple solutions. Also, this way I could really "feel" the different in terms of time and space complexity. And on top of that, I enjoyed programming them,
  it was fun.
  
##  Puzzle representation
I decided to use two different representations of the puzzle. Since they represent the same entity, I decided to create a common abstraction [SudokuPuzzle](src/main/kotlin/io/dkozak/sudoku/model/SudokuPuzzle.kt)
above them to share some high level operations between them.
* [SimpleSudokuPuzzle](src/main/kotlin/io/dkozak/sudoku/model/SimpleSudokuPuzzle.kt)
This is the most direct approach how to represent the puzzle. It uses a two dimensional integer array, where each cell either has a value from interval 1 to 9
or -1 signalling an empty cell. 
* [OptionsAwareSudokuPuzzle](src/main/kotlin/io/dkozak/sudoku/model/OptionsAwareSudokuPuzzle.kt)
When solving the puzzle, the question "Which value can be assigned to a specific cell?" has to be answered many times. Therefore, I decided to create a second 
representation, in which each cells keeps tracks of all values that can be assigned to it. Every time a value is assigned to any cell, all cells in row, col and region
are updated accordingly.