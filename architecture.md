# Architecture
This document contains the architecture of the solver and the decisions why it was made this way. 

## Implementation language
Even though the original requirement was to make it in Java, I afterwards negotiated it to Kotlin/JVM. Here is my reasoning behind that decision.
Of course, there  is no single language that is best in everything. Programming languages are just tools that developers use to solve problems.
So each of them might be better for one thing and worse for another. However, some languages are quite similar to each other, hence they are suitable for 
similar kinds of problems. And this is exactly the case with Java and Kotlin/JVM. And since these two languages are quite similar and 
can be used to solve similar problems, it actually makes sense to compare them. And I find Kotlin superior to Java in many ways. Some of them are the following.
* Better support for functional programming
* Extension methods
* Cleaner and more consise code
* Type inference
* Very little boilerplate compared to Java
* Possibility to create internal DSLs thanks to lambda's with receivers

## General approach
My approach was iterative and prototype-based. I decided to implement a very simple inefficient solver first and then gradually 
improve it until the result was satisfying. Finally, I ended up with 4 different solving strategies using 2 different models of the puzzle.
One might argue that I could probably do a deeper analysis beforehand and then go directly for the most efficient approach I could figure out (which is what I would 
do at work if I were presented with similar task), but there was a reason why I chose my approach. I took this task as a chance to exercise 
my programming skills, so I actually wanted to implement  multiple solutions. Also, this way I could really "feel" the different in terms of 
time and space complexity. And on top of that, I enjoyed programming them,  it was fun.
  
##  Puzzle representation
I decided to use two different representations of the puzzle. Since they represent the same entity, I decided to create a common abstraction [SudokuPuzzle](src/main/kotlin/io/dkozak/sudoku/model/SudokuPuzzle.kt)
above them to share some high level operations between them.
* [SimpleSudokuPuzzle](src/main/kotlin/io/dkozak/sudoku/model/SimpleSudokuPuzzle.kt)
This is the most direct approach how to represent the puzzle. It uses a two dimensional integer array, where each cell either has a value from interval 1 to 9
or -1 signalling an empty cell. Once cell needs constant amount of space -> 1 integer and there is _PuzzleSize^2_ such cells, 
therefore the total size in memory is _PuzzleSize^2_. Reads and writes take _O(1)_ time.
* [OptionsAwareSudokuPuzzle](src/main/kotlin/io/dkozak/sudoku/model/OptionsAwareSudokuPuzzle.kt)
When solving the puzzle, the question "Which value can be assigned to a specific cell?" has to be answered many times. Therefore, I decided to create a second 
representation, in which each cells keeps tracks of all values that can be assigned to it. Every time a value is assigned to any cell, 
all cells in corresponding row, col and region are updated accordingly. One might think that this representation has to consume much more space, 
but in fact the information whether certain value can be assigned to a cell or not can be encoded in a single bit. Therefore only _PuzzleSize_ bits are needed for 
each cell, so the memory size is still the same - _PuzzleSize^2_. However, the set operation is not constant anymore, but it has to traverse over _3*PuzzleSize_ 
cells and update them. And also it also takes _PuzzleSize_ time to collect all possible values that can be assigned to the cell by iterating over the bits. 
However, the traversal that is performed when assigning a value to a cell has to happen anyway when solving the puzzle, so moving it into the model itself
just makes the solver code cleaner while adding only a small overhead.

## Test data
Two puzzles were already provided as part of the assignment. On top of that, I decided to add one more - an empty puzzle, which is an edge case.
* [Simple](src/test/resources/puzzles/first.sudoku)
* [Difficult](src/test/resources/puzzles/second.sudoku)
* [Empty](src/test/resources/puzzles/empty.sudoku)

However, this is definitely not enough for a proper testing. Therefore, I also decided to add a [fuzz test](src/test/kotlin/io/dkozak/sudoku/solver/DfsFuzzTest.kt), which works in the following way.
It starts with fully solved [Simple](src/test/resources/puzzles/first.sudoku) puzzle and than it performs 1000 iterations. In each of them, 
a subset of the fully solved puzzle with randomly chosen amount of filled cells is given to the solver, which then tries to solve it again.  

### Data format
I decided to keep the data format as simple as possible. The _.sudoku_ file format contains _PuzzleSize_ rows, each of them containg _PuzzleSize_ cells.
The values for each of these cells can be numbers _from 1 to PuzzleSize_ or they can be empty.   

## Solving algorithms
There are many ways how a sudoku puzzle can be solved. The first aproach one might choose is **brute force** - simply try to fill the remaining cells with 
all possible combinations of numbers and check if the result is a valid sudoku. However, this approach has a problem - There are simply too many states to check.
For a typical sudoku, there are 9 rows and 9 columns, each of them containing values _from 1 to 9_. Therefore there are _9^2_ cells and each of them can have 9 values.
Therefore the total number of ways to fill the puzzle is _9^81_ and in general it is _PuzzleSize^(PuzzleSize^2)_. Of course, real puzzles have some cells filled in 
already, but still the number of options is pretty high. Therefore the brute force is not a good approach for this problem. 
This one I haven't even tried to implement, knowing that it is way too ineffective. 

One observation about sudoku is that even though thare are so many states to check, a lot of them are not valid. To skip the invalid ones, one might try to fill
in the puzle only with valid numbers, which is what my first solver [SimpleBacktrackingSolver](src/main/kotlin/io/dkozak/sudoku/solver/SimpleBacktrackingSolver.kt) 
was doing. It used the simple [SimpleSudokuPuzzle](src/main/kotlin/io/dkozak/sudoku/model/SimpleSudokuPuzzle.kt) representation and tried to fill it in by using 
the following approach. It iterated over the puzzle from left to right from top to bottom and for each cell, it computed all possible values that can be assigned to it, and iteratively tried all of them while recursively solving the remaining puzzle. 
From a programmer's perspective, it is probably the simplest thing to do. However, this method is still very ineffective. Even though only a single model of the puzzle
is needed if backtracking is used, this method still checks way too many paths leading to invalid states. And on top of that, even though only one model is needed, 
the method is filling the puzzle in recursively and the depth of the recursion can be up to _PuzzleSize^2_, that is the amount of cells in the puzzle. This solver 
failed to provide a solution even for the [Simple](src/test/resources/puzzles/first.sudoku) puzzle.

The second approach I tried was to solve the puzzle in a more 'human way' -> that is in a way how a human would try to solve it. The 
[OptionsAwareExactSolver](src/main/kotlin/io/dkozak/sudoku/solver/OptionsAwareExactSolver.kt) uses the [OptionsAwareSudokuPuzzle](src/main/kotlin/io/dkozak/sudoku/model/OptionsAwareSudokuPuzzle.kt)
model and works in the following way. It keeps filling in all cells which can have only a single value assigned to them. When it halts, the puzzle is either fully
solved or only cells, whose value cannot be exactly determined, remain empty. This solver manages to solve the [Simple](src/test/resources/puzzles/first.sudoku) puzzle,
however it fails for the [Difficult](src/test/resources/puzzles/second.sudoku) one, because there are not enough numbers given to make an exact decision each time.

The final approach is a combination of the two previous. The idea is to fill in as much cells as possible using [OptionsAwareExactSolver](src/main/kotlin/io/dkozak/sudoku/solver/OptionsAwareExactSolver.kt)
in each step and if any empty cells remain, find the one with lowest amount of options and try all of them. I implemented two different solvers based on this idea,
[OptionsAwareBfsSolver](src/main/kotlin/io/dkozak/sudoku/solver/OptionsAwareBfsSolver.kt) and [OptionsAwareDfsSolver](src/main/kotlin/io/dkozak/sudoku/solver/OptionsAwareDfsSolver.kt).
As the names suggest, their only difference is in the way the state space traversal is handled. Bfs solver keeps a queue of all solutions to check, while
dfs solver recursively calls itself on every decision point. Both of these solvers managed to solve both [Simple](src/test/resources/puzzles/first.sudoku) and 
[Difficult](src/test/resources/puzzles/second.sudoku) puzzles. However, only the dfs solver manages to solve the [Empty](src/test/resources/puzzles/empty.sudoku) puzzle.
I believe that the reason is the following. For puzzles with only a few numbers filled in, there exist many solutions. And since dfs reaches filled in puzzles sooner
than bfs, it will reach a solution faster. In general, bfs gives us the 'optimal' solution, meaning a solution which required the minimal amount of steps,
while dfs gives us no guarantees and can even get stuck in an infinite loop. However, for the sudoku problem, all valid solutions are at the same depth 
(because the same amount of numbers is needed for each of them) and the infinite loop also cannot happen, because in each branch either a valid solution is found 
or the puzzle becomes invalid after at most as many calls as is the number of empty cells.
        
## Conclusion
Based on the reasoning above, I decided to use the [OptionsAwareDfsSolver](src/main/kotlin/io/dkozak/sudoku/solver/OptionsAwareDfsSolver.kt) as the default
solver in the program. This solver first fills in all cells, whose value can be exactly determined, and if any empty cells remain, it picks the one with 
the lowest number of options and tries all of them recursively in a depth first manner. It managed to solve all three test cases - [Simple](src/test/resources/puzzles/first.sudoku), 
[Difficult](src/test/resources/puzzles/second.sudoku), [Empty](src/test/resources/puzzles/empty.sudoku) and it also passes the [fuzz test](src/test/kotlin/io/dkozak/sudoku/solver/DfsFuzzTest.kt).

## Ideas for extension
-[ ] Performance measurements - Measure the execution time and memory consumption for different solvers and compare them.
-[ ] More advanced search space traversals - Try to use a heuristic (maybe even random choice) to prioritize options at the decision points. 
However, it is important to keep the cost of computing heuristic function low.
-[ ] Parallel solver - If there are multiple choices, try them concurrently (up to the number of cores, since this task is computational intensive). 