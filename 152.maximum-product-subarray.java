/*
 * @lc app=leetcode id=152 lang=java
 *
 * [152] Maximum Product Subarray
 */

// @lc code=start
class Solution {
    public int maxProduct(int[] nums) {
        int res = nums[0];
        // dpMax[i] 表示子数组 [0, i] 范围内并且一定包含 nums[i] 数字的最大子数组乘积
        int[] dpMax = new int[nums.length];
        // dpMin[i] 表示子数组 [0, i] 范围内并且一定包含 nums[i] 数字的最小子数组乘积
        int[] dpMin = new int[nums.length];
        dpMax[0] = res;
        dpMin[0] = res;
        for (int i = 1; i < nums.length; i++) {
            // 最大值和最小值只会在这三个数字之间产生
            // 即 dpMax[i-1]*nums[i]，dpMin[i-1]*nums[i]，和 nums[i]
            // 所以用三者中的最大值来更新 dpMax[i]，用最小值来更新dpMin[i]
            dpMax[i] = Math.max(Math.max(dpMax[i - 1] * nums[i], dpMin[i - 1] * nums[i]), nums[i]);
            dpMin[i] = Math.min(Math.min(dpMax[i - 1] * nums[i], dpMin[i - 1] * nums[i]), nums[i]);
            // 然后用 dpMax[i] 来更新结果 res 
            res = Math.max(dpMax[i], res);
        }
        // 由于最终的结果不一定会包括 nums[n-1] 这个数字
        // 所以 dpMax[n-1] 不一定是最终解，不断更新的结果 res 才是
        return res;
    }
}
// @lc code=end

