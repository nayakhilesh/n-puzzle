n-puzzle
========

Solver for an 'n' sized 15 puzzle (http://tinyurl.com/n37vrar)

Input Format:

The first line of the input contains an integer k, the size of the square grid. k * k lines follow each line containing an integer I on the tile starting from the top left to bottom right. The empty cell is represented by the number 0.

N = (k * k) - 1
0 <= I <= N

Output Format:

The first line contains an integer, M, the number of moves the algorithm has taken to solve the N-Puzzle. M lines follow. Each line indicating the movement of the empty cell (0).

A grid is considered solved if it is in the following configuration.

0 1 2
3 4 5
6 7 8

Sample Input:

3
0
3
8
4
1
7
2
6
5

Sample Output:

20
RIGHT
DOWN
...
...
...
