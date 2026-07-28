/*
 * @lc app=leetcode id=292 lang=java
 *
 * [292] Nim Game
 */

// @lc code=start
class Solution {
    public boolean canWinNim(int n) {
        if (n > 66666) {
            return n % 4 != 0;
        }
        boolean[] dp = new boolean[n + 1];
        return canWinNimHelper(n, dp);
    }
    private boolean canWinNimHelper(int n, boolean[] dp) {
        if (dp[n] == true) {
            return true;
        }
        if (n <= 3) {
            return true;
        }
        dp[n - 1] = canWinNimHelper(n - 1, dp);
        dp[n - 2] = canWinNimHelper(n - 2, dp);
        dp[n - 3] = canWinNimHelper(n - 3, dp);
        return (!dp[n - 1] || !dp[n - 2] || !dp[n - 3]);
    }
}
// @lc code=end

