/*
 * @lc app=leetcode id=505 lang=java
 *
 * [505] The Maze II
 */

// @lc code=start
class Solution {
    // Dijkstra 算法
    public int shortestDistance(int[][] maze, int[] start, int[] destination) {
        final int[][] DIRECTIONS = new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        if (maze == null || maze.length == 0 || maze[0].length == 0) {
            return -1;
        }
        int[][] shortestDis = new int[maze.length][maze[0].length];
        for (int[] coors : shortestDis) {
            Arrays.fill(coors, Integer.MAX_VALUE);
        }
        PriorityQueue<int[]> resPQ = new PriorityQueue<int[]>((a, b) -> {
            return a[2] - b[2];
        });
        
        // int[0] 代表 x，int[1]代表 y，int[2]代表离最开始点的距离
        resPQ.offer(new int[]{start[0], start[1], 0});
        
        while (!resPQ.isEmpty()) {
            int[] currCloest = resPQ.poll();
            // 代表已经遍历过
            if (shortestDis[currCloest[0]][currCloest[1]] < currCloest[2]) {
                continue;
            }
            shortestDis[currCloest[0]][currCloest[1]] = currCloest[2];
            // 如果到达终点，说明这时候的就是最短路径
            if (currCloest[0] == destination[0] && currCloest[1] == destination[1]) {
                return currCloest[2];
            }
            for (int[] direction: DIRECTIONS) {
                int currDis = currCloest[2];
                int newX = currCloest[0] + direction[0], newY = currCloest[1] + direction[1];
                currDis++;
                if (newX < 0 || newY < 0 || newX >= maze.length || newY >= maze[newX].length || maze[newX][newY] == 1) {
                    continue;
                }
                while (true) {
                    int nextX = newX + direction[0], nextY = newY + direction[1];
                    if (nextX < 0 || nextY < 0 || nextX >= maze.length || nextY >= maze[nextX].length || maze[nextX][nextY] == 1) {
                        break;
                    }     
                    newX = nextX;
                    newY = nextY;
                    currDis++;
                }
                if (shortestDis[newX][newY] < currDis) {
                    continue;
                }
                resPQ.offer(new int[]{newX, newY, currDis});
            }
        }
        return -1;
    }
}
// @lc code=end

