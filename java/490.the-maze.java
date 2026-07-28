/*
 * @lc app=leetcode id=490 lang=java
 *
 * [490] The Maze
 */

// @lc code=start
class Solution {
    public boolean hasPath(int[][] maze, int[] start, int[] destination) {
        // 记录visit过的坐标
        boolean[][] visited = new boolean[maze.length][maze[0].length];
        final int[][] directions = new int[][]{{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        return helper(maze, start[0], start[1], destination[0], destination[1], visited, directions);
    }
    private boolean helper(int[][] maze, int startX, int startY, int destinationX, int destinationY, boolean[][] visited, int[][] directions) {
        if (startX < 0 || startY < 0 || startX >= maze.length || startY >= maze[startX].length) {
            return false;
        }
        if (visited[startX][startY]) {
            return false;
        }
        visited[startX][startY] = true;
        if (startX == destinationX && startY == destinationY) {
            return true;
        }
        for (int[] direction: directions) {
            // 一直移动到下一个墙或者不能再移动为止
            int currX = startX + direction[0], currY = startY + direction[1];
            while (currX >= 0 && currY >= 0 && currX < maze.length && currY < maze[currX].length && maze[currX][currY] != 1) {
                currX += direction[0];
                currY += direction[1];
            }
            currX -= direction[0];
            currY -= direction[1];
            // 尝试从下一个地点出发
            if (helper(maze, currX, currY, destinationX, destinationY, visited, directions)) {
                return true;
            }
        }
        return false;
    }
}
// @lc code=end

