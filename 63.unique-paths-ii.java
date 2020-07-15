/*
 * @lc app=leetcode id=63 lang=java
 *
 * [63] Unique Paths II
 */

// @lc code=start
class Solution {
    public int uniquePathsWithObstacles(int[][] obstacleGrid) {
        int m = obstacleGrid.length, n = obstacleGrid[0].length;
        int[][] dp = new int[m][n];
        for (int i = m - 1; i >= 0; i--) {
            for (int j = n - 1; j >= 0; j--) {
                // 如果当前有石头，则一定没办法到达终点
                if (obstacleGrid[i][j] == 1) {
                    dp[i][j] = 0;
                }
                // 最右边和最底下只有一种方式到达终点
                // 如果之前有石头，则没有办法到终点
                else if (i == m - 1) {
                    if (j < n - 1 && dp[i][j + 1] == 0) {
                        dp[i][j] = 0;
                    }
                    else {
                        dp[i][j] = 1;
                    }
                }
                else if (j == n - 1) {
                    if (i < m - 1 && dp[i + 1][j] == 0) {
                        dp[i][j] = 0;
                    }
                    else {
                        dp[i][j] = 1;
                    }
                }
                else {
                    // 当前i，j点总共到达终点的方式数 等于从下面走的方式数加上从右边走的方式数
                    dp[i][j] = dp[i + 1][j] + dp[i][j + 1];
                }
            }
        }
        return dp[0][0];
    }
}
// @lc code=end

