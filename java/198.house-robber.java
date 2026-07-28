/*
 * @lc app=leetcode id=198 lang=java
 *
 * [198] House Robber
 */

// @lc code=start
class Solution {
    public int rob(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        if (nums.length == 1) {
            return nums[0];
        }
        // dp[i]代表到i位置时，所能获得的最大额度
        int[] dp = new int[nums.length];
        for (int i = 0; i < nums.length; i++) {
            if (i == 0) {
                dp[i] = nums[i];
            }
            else if (i == 1) {
                dp[i] = Math.max(nums[0], nums[1]);
            }
            else {
                dp[i] = Math.max(dp[i - 2] + nums[i], dp[i - 1]);
            }
        }
        return dp[nums.length - 1];
    }
}
// @lc code=end

