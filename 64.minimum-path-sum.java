/*
 * @lc app=leetcode id=64 lang=java
 *
 * [64] Minimum Path Sum
 */

// @lc code=start
class Solution {
    public int minPathSum(int[][] grid) {
        if (grid.length == 0 || grid[0].length == 0) {
            return -1;
        }
        int m = grid.length, n = grid[0].length;
        int[][] dp = new int[m][n];
        for (int i = m - 1; i >= 0; i --) {
            for (int j = n - 1; j >= 0; j--) {
                if (i == m - 1 && j == n - 1) {
                    dp[i][j] = grid[i][j];
                }
                else if (i == m - 1) {
                    dp[i][j] = dp[i][j + 1] + grid[i][j];
                }
                else if (j == n - 1) {
                    dp[i][j] = dp[i + 1][j] + grid[i][j];
                }
                else {
                    dp[i][j] = grid[i][j] + Math.min(dp[i + 1][j], dp[i][j + 1]);
                }
            }
        }
        return dp[0][0];
    }
}
// @lc code=end

