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
* Code is much more consise and pragmatic
* Very little boirlerplate compared to Java
* Possibility to create internal DSLs thanks to lambda's with receivers