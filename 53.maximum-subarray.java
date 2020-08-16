/*
 * @lc app=leetcode id=53 lang=java
 *
 * [53] Maximum Subarray
 */

// @lc code=start
class Solution {
    public int maxSubArray(int[] nums) {
        // O(n) 解法， dp 
        int maxRes = nums[0];
        int sum = nums[0];
        for (int i = 1; i < nums.length; i++) {
            // 如果之前的dp数（之前的总和）是正数，加上当前数
            if (sum > 0) {
                sum += nums[i];
            }
            // 不然从当前重新开始
            else {
                sum = nums[i];
            }
            // 一直记录每个dp的值，对比记下最大值
            maxRes = Math.max(maxRes, sum);
        }
        return maxRes;
    }
}
// @lc code=end

