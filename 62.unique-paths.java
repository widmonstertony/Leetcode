/*
 * @lc app=leetcode id=62 lang=java
 *
 * [62] Unique Paths
 */

// @lc code=start
class Solution {
    public int uniquePaths(int m, int n) {
        int[][] dp = new int[m][n];
        // 最右边和最底下只有一种方式到达终点
        for (int i = 0; i < m; i++) {
            dp[i][n - 1] = 1;
        }
        for (int j = 0; j < n; j++) {
            dp[m - 1][j] = 1;
        }
        for (int i = m - 2; i >= 0; i--) {
            for (int j = n - 2; j >= 0; j--) {
                // 当前i，j点总共到达终点的方式数 等于从下面走的方式数加上从右边走的方式数
                dp[i][j] = dp[i + 1][j] + dp[i][j + 1];
            }
        }
        return dp[0][0];
    }
}
// @lc code=end

