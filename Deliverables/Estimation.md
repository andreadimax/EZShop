1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
20
21
22
23
24
25
26
27
28
29
30
31
32
33
34
35
36
37
38
39
40
41
42
43
44
45
46
47
48
49
50
51
# Project Estimation  
Authors:
Date:
Version:
# Contents
- [Estimate by product decomposition]
- [Estimate by activity decomposition ]
# Estimation approach
<Consider the EZGas  project as described in YOUR requirement document, assume that you are going to develop the project INDEPENDENT of the deadlines of the course>
# Estimate by product decomposition
### 
|             | Estimate                        |             
| ----------- | ------------------------------- |  
| NC =  Estimated number of classes to be developed   |   21   |             
|  A = Estimated average size per class, in LOC       |    150  | 
| S = Estimated size of project, in LOC (= NC * A) |  3150  |
| E = Estimated effort, in person hours (here use productivity 10 LOC per person hour)  |     315      |   
| C = Estimated cost, in euro (here use 1 person hour cost = 30 euro) | 9450€ | 
| Estimated calendar time, in calendar weeks (Assume team of 4 people, 8 hours per day, 5 days per week ) |    2 calendar weekss   (10 day of active work)        |               
# Estimate by activity decomposition
### 
|         Activity name    | Estimated effort (person hours)   |             
| ----------- | ------------------------------- | 
| Requirements | 72 |
| Design | 32 |
| Coding | 95 |
| Testing | 100 |
###
Insert here Gantt chart with above activities

Requirements: only 2 people work at the same time, since low parallel work is possible in this phase.
Design: only 2 people working at the same time,  since low parallel work is possible in this phase.
Coding and Testing: Estimation Is based on a team of 4 people working at the project 8 hours per day, 5 days per week, writing code and tests at the same time divided in 2 sub-teams of 2 persons each.

```
@startgantt
saturday are closed
sunday are closed

Project starts 2021-03-08
[Requirements] lasts 3 days

[Design] lasts 2 days
[Design] starts at [Requirements]'s end

[Coding] lasts 5 days
[Coding] starts at [Design]'s end

[Testing] lasts 5 days
[Testing] starts at [Design]'s end

@endgantt
```
