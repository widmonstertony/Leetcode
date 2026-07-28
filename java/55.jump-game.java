/*
 * @lc app=leetcode id=55 lang=java
 *
 * [55] Jump Game
 */

// @lc code=start
class Solution {
    public boolean canJump(int[] nums) {
        // dp[i] 代表从i最远能到哪
        // 可以优化成一个变量
        int[] dp = new int[nums.length];
        for (int i = 0; i < nums.length; i++) {
            if (i == 0) {
                dp[i] = nums[i];
            }
            else {
                // 如果不能从上一个到这个，说明无解
                if (i > dp[i - 1]) {
                    return false;
                }
                // 如果已经到终点了，直接返回true
                if (dp[i - 1] >= nums.length - 1) {
                    return true;
                }
                // 当前位置的最远距离，和上一个位置的最远距离选最大值
                dp[i] = Math.max(dp[i - 1], nums[i] + i);
            }
        }
        return true;
    }
    // public boolean canJump(int[] nums) {
    //     boolean[] dp = new boolean[nums.length];
    //     for (int i = nums.length - 1; i >= 0; i--) {
    //         if (i == nums.length - 1) {
    //             dp[i] = true;
    //         }
    //         else {
    //             for (int j = i + 1; j <= i + nums[i]; j++) {
    //                 if (j < nums.length && dp[j]) {
    //                     dp[i] = true;
    //                     break;
    //                 }
    //             }
    //         }
    //     }
    //     return dp[0];
    // }
}
// @lc code=end

