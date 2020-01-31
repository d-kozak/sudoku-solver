# Sudoku solver
Sudoku solver. Complete architecture document can be found [here](./architecture.md).

## Technical description
* Written in Kotlin 
* Build using Gradle

## Run
You can run the app directly using gradle.
```
gradle run --args="path-to-puzzle [solver-to-use]"
```
For example
```
gradle run --args="src/test/resources/puzzles/second.sudoku dfs"
```
Arguments
* path-to-puzzle - path to the file containing the puzzle
* solver to use, available options are **exact**, **bfs** and **dfs**


## Deploy
To deploy the app, you can use shadowJar.
```
gradle shadowJar
```
It will create a jar archive _build/libs/sudoku-solver-1.0-SNAPSHOT.jar_, which can then be executed.
```
 java -jar build/libs/sudoku-solver-1.0-SNAPSHOT.jar path-to-puzzle [solver-to-use]
```
 