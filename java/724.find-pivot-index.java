/*
 * @lc app=leetcode id=724 lang=java
 *
 * [724] Find Pivot Index
 */

// @lc code=start
class Solution {
    public int pivotIndex(int[] nums) {
        int[] preSum = new int[nums.length];
        int currSum = 0;
        for (int i = 0; i < nums.length; i++) {
            currSum += nums[i];
            preSum[i] = currSum;
        }
        for (int i = 0; i < nums.length; i++) {
            // 单独处理0的情况，0前面没有数字
            if (i == 0) {
                if (preSum[nums.length - 1] - nums[i] == 0) {
                    return i;
                }
            }
            else if (preSum[nums.length - 1] - nums[i] - preSum[i - 1] == preSum[i - 1]) {
                return i;
            }
        }
        return -1;
    }
}
// @lc code=end

