/*
 * @lc app=leetcode id=486 lang=java
 *
 * [486] Predict the Winner
 */

// @lc code=start
class Solution {
    public boolean PredictTheWinner(int[] nums) {
        int n = nums.length;
        // dp[i][j] 代表从i到j开始玩的话，当前玩家可以获得的比对方玩家多的分数的最大值
        int[][] dp = new int[n][n];
        // 如果只有一个数可以选，则当前玩家一定能获得当前分数，而对方获得0分
        for (int i = 0; i < n; i++) {
            dp[i][i] = nums[i];
        }
        // len表示为当前所选的数组的长度
        for (int len = 1; len < n; len++) {
            // i从0开始，j一直从当前长度直到数组最后一位
            for (int i = 0, j = len; j < n; i++, j++) {
                // 当前i，j的最大值
                // 如果选择i也就是开头那位，则减去对方在i之后到j的能获得比你多的分数的最大值，也就是dp[i + 1][j]
                // 如果选择j也就是末尾那位，则减去对方在j之前到i的能获得比你多的分数的最大值，也就是dp[i][j - 1]
                dp[i][j] = Math.max(nums[i] - dp[i + 1][j], nums[j] - dp[i][j - 1]);
            }
        }
        // 最后确认从开始到结束，当前玩家能获得的比对方玩家多的分数大于0，也就代表获胜了
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

