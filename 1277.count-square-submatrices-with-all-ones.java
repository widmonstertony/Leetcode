/*
 * @lc app=leetcode id=1277 lang=java
 *
 * [1277] Count Square Submatrices with All Ones
 */

// @lc code=start
class Solution {
    public int countSquares(int[][] matrix) {
        int s = 0;
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return 0;
        }
        // dp[i][j] 表示从左顶上角到达(i, j)位置并一定要包括(i, j)位置所能组成的最大正方形的边长
        int[][] dp = new int[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (i == 0 || j == 0) {
                    dp[i][j] = matrix[i][j];
                    s += dp[i][j];
                }
                else if (matrix[i][j] == 1) {
                    // 只有当前 (i, j) 位置为1，dp[i][j] 才有可能大于0
                    // 左边，上边，和左上边不能有0，所以取交集找其中最小的值
                    // 这个最小值，就是左边，上边，和左上都有足够的1组成的正方形的边长
                    // 再加上1也就是包括现在这个角，就是(i, j) 位置所能组成的最大正方形的边长
                    dp[i][j] = Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1])) + 1;
                    s += dp[i][j];
                }
                // 更新最大边长
            }
        }
        return s;
    }
}
// @lc code=end

