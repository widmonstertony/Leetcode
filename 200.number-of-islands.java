import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
/*
 * @lc app=leetcode id=200 lang=java
 *
 * [200] Number of Islands
 */

// @lc code=start
class Solution {
    public int numIslands(char[][] grid) {
        if (grid == null || grid.length == 0 || grid[0].length == 0) {
            return 0;
        }
        int m = grid.length, n = grid[0].length;
        boolean[][] visited = new boolean[m][n];
        int res = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (visited[i][j] || grid[i][j] == '0') {
                    continue;
                }
                // 总共岛的数量加一
                res++;
                
                // dfs解法
                // 把当前岛周围所有的岛都mark为visited
                dfsVisit(i, j, grid, visited);

                // bfs解法
                // Queue<Pair> queue = new LinkedList<Pair>();
                // queue.offer(new Pair(i, j));
                // while (!queue.isEmpty()) {
                //     Pair curr = queue.poll();
                //     int x = curr.getLeft();
                //     int y = curr.getRight();
                //     if (x < 0 || y < 0 || x >= grid.length || y >= grid[0].length || visited[x][y] || grid[x][y] == '0') {
                //         continue;
                //     }
                //     visited[x][y] = true;
                //     queue.offer(new Pair(x, y + 1));
                //     queue.offer(new Pair(x + 1, y));
                //     queue.offer(new Pair(x - 1, y));
                //     queue.offer(new Pair(x, y - 1));
                // }
            }
        }
        return res;
    }
    class Pair {
        int mLeft, mRight;
        Pair(int i, int j) {
            mLeft = i;
            mRight = j;
        }
        int getLeft() {
            return mLeft;
        }
        int getRight() {
            return mRight;
        }
    }
    private void dfsVisit(int i, int j, char[][] grid, boolean[][] visited) {
        if (i < 0 || j < 0 || i >= grid.length || j >= grid[0].length || visited[i][j] || grid[i][j] == '0') {
            return;
        }
        visited[i][j] = true;
        // dfs访问当前坐标的上下左右，并标记为visit
        dfsVisit(i, j + 1, grid, visited);
        dfsVisit(i + 1, j, grid, visited);
        dfsVisit(i - 1, j, grid, visited);
        dfsVisit(i, j - 1, grid, visited);
    }
}
// @lc code=end

