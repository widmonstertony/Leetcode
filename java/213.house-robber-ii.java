/*
 * @lc app=leetcode id=213 lang=java
 *
 * [213] House Robber II
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
        // 去头去尾分别跑一次，取最大值
        return Math.max(rob(nums, 0, nums.length - 1), rob(nums, 1, nums.length));
    }
    private int rob(int[] nums, int start, int end) {
        // dp[i]代表到i位置时，所能获得的最大额度
        int[] dp = new int[end];
        for (int i = start; i < end; i++) {
            if (i == start) {
                dp[i] = nums[i];
            }
            else if (i == start + 1) {
                dp[i] = Math.max(nums[start], nums[start + 1]);
            }
            else {
                dp[i] = Math.max(dp[i - 2] + nums[i], dp[i - 1]);
            }
        }
        return dp[end - 1];
    }
}
// @lc code=end

