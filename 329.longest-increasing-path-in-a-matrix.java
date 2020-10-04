/*
 * @lc app=leetcode id=329 lang=java
 *
 * [329] Longest Increasing Path in a Matrix
 */

// @lc code=start
class Solution {
    final int[][] directions = new int[][] {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
    public int longestIncreasingPath(int[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return 0;
        }
        // dp[i][j]表示数组中以(i,j)为起点的最长递增路径的长度
        int[][] dp = new int[matrix.length][matrix[0].length];
        int maxLength = Integer.MIN_VALUE;
        // 以数组中每个位置都为起点调用递归来做，比较找出最大值
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                maxLength = Math.max(maxLength, dfs(matrix, i, j, dp));
            }
        }
        return maxLength;
    }
    private int dfs(int[][] matrix, int i, int j, int[][] dp) {
        if (i >= dp.length || j >= dp[0].length || i < 0 || j < 0) {
            return 0;
        }
        // 如果dp[x][y]不为0的话，直接返回dp[x][y]即可，不需要重复计算
        if (dp[i][j] != 0) {
            return dp[i][j];
        }
        int maxNext = 0;
        // 其四个相邻位置进行判断，如果相邻位置的值大于上一个位置，则对相邻位置继续调用递归
        for (int[] direction : directions) {
            int nextI = direction[0] + i, nextJ = direction[1] + j;
            if (nextI >= dp.length || nextJ >= dp[0].length || nextI < 0 || nextJ < 0) {
                continue;
            }
            if (matrix[nextI][nextJ] > matrix[i][j]) {
                maxNext = Math.max(maxNext, dfs(matrix, nextI, nextJ, dp)); 
            }
        }
        dp[i][j] = maxNext + 1;
        return dp[i][j];
    }
}
// @lc code=end

