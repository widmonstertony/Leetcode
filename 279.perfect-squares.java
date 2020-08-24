/*
 * @lc app=leetcode id=279 lang=java
 *
 * [279] Perfect Squares
 */

// @lc code=start
class Solution {
    public int numSquares(int n) {
        // dp[i] 表示当前i值至少需要多少个square numbers加到它
        int[] dp = new int[n + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j * j <= i; j++) {
                 // 把每一个dp[i - j * j] + 1和当前值比较，取最小值
                dp[i] = Math.min(dp[i], dp[i - j * j] + 1);
            }
        }
        return dp[n];
    }
}
// @lc code=end

