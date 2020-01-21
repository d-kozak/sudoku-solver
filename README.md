# Sudoku solver

Sudoku solver written in Java. The plan is to have multiple techniques you can choose from 
and to provide a comparision between them.

## Ideas
We could apply brute force and just try all inputs, but there is quite a lot of them (upper bound 9^81).
1. brute force v1 - enumerate all possible inputs 
2. brute force v2 - try to fill it in from top to bottom, backtrack when sudoku rule is broken encountered 

So probably we should do something smarter. 

1. sort the fields based on those with minimal amount of options(if there are fields with only single possible value, it can be a benefit), start from them
2. some "clever" state space search? with what heuristic?