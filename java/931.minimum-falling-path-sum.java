/*
 * @lc app=leetcode id=931 lang=java
 *
 * [931] Minimum Falling Path Sum
 */

// @lc code=start
class Solution {
    public int minFallingPathSum(int[][] A) {
        int[][] dp = new int[A.length][A[0].length];
        int minRes = Integer.MAX_VALUE;
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[0].length; j++) {
                // 初始化第一排
                if (i == 0) {
                    dp[i][j] = A[i][j];
                }
                else {
                    if (j == 0) {
                        dp[i][j] = Math.min(dp[i - 1][j], dp[i - 1][j + 1]) + A[i][j];
                    }
                    else if (j == A[i].length - 1) {
                        dp[i][j] = Math.min(dp[i - 1][j - 1], dp[i - 1][j]) + A[i][j];
                    }
                    else {
                        dp[i][j] = Math.min(dp[i - 1][j + 1], Math.min(dp[i - 1][j - 1], dp[i - 1][j])) + A[i][j];
                    }
                }
                if (i == A.length - 1) {
                    minRes = Math.min(minRes, dp[i][j]);
                }
            }
        }
        return minRes;
    }
}
// @lc code=end

