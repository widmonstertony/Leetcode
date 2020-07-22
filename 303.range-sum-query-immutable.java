/*
 * @lc app=leetcode id=303 lang=java
 *
 * [303] Range Sum Query - Immutable
 */

// @lc code=start
class NumArray {
    int[] dp;
    int[] nums;
    public NumArray(int[] nums) {
        dp = new int[nums.length];
        for (int i = 0; i < nums.length; i++) {
            if (i == 0) {
                dp[i] = nums[i];
            }
            else {
                dp[i] = dp[i - 1] + nums[i];
            }
        }
        this.nums = nums;
    }
    
    public int sumRange(int i, int j) {
        if (i == 0) {
            return dp[j];
        }
        else {
            return dp[j] - dp[i] + nums[i];
        }
    }
}

/**
 * Your NumArray object will be instantiated and called as such:
 * NumArray obj = new NumArray(nums);
 * int param_1 = obj.sumRange(i,j);
 */
// @lc code=end

