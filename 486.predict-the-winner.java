/*
 * @lc app=leetcode id=486 lang=java
 *
 * [486] Predict the Winner
 */

// @lc code=start
class Solution {
    public boolean PredictTheWinner(int[] nums) {
        int n = nums.length;
        int[][] dp = new int[n][n];
        for (int i = 0; i < n; i++) {
            dp[i][i] = nums[i];
        }
        for (int len = 1; len < n; len++) {
            for (int i = 0, j = len; j < n; i++, j++) {
                dp[i][j] = Math.max(nums[i] - dp[i + 1][j], nums[j] - dp[i][j - 1]);
            }
        }
        return dp[0][n - 1] >= 0;
    }
    // public boolean PredictTheWinner(int[] nums) {
    //     if (nums.length <= 2) {
    //         return true;
    //     }
    //     return PredictTheWinnerHelper(nums, 0, nums.length - 1, 0, 0);
    // }

    // private boolean PredictTheWinnerHelper(int[] nums, int start, int end, int scoreA, int scoreB) {
    //     if (end - start < 0) {
    //         return scoreA > scoreB;
    //     }
    //     if (end - start == 0) {
    //         return scoreA + nums[start] > scoreB;
    //     }
    //     // 交换玩家分数，确保另一个玩家一定不会赢，不然当前玩家必输
    //     return !PredictTheWinnerHelper(nums, start + 1, end, scoreB, scoreA + nums[start]) || !PredictTheWinnerHelper(nums, start, end - 1, scoreB, scoreA + nums[end]);
    // }
}
// @lc code=end

