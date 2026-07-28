/*
 * @lc app=leetcode id=643 lang=java
 *
 * [643] Maximum Average Subarray I
 */

// @lc code=start
class Solution {
    public double findMaxAverage(int[] nums, int k) {
        int left = 0, right = k - 1;
        double maxRes = 0;
        int currSum = 0;
        for (int i = 0; i < k; i++) {
            currSum += nums[i];
        }
        maxRes = currSum * 1.0 / k;
        for (left++, right++; right < nums.length; left++, right++) {
            currSum -= nums[left - 1];
            currSum += nums[right];
            maxRes = Math.max(maxRes, currSum * 1.0 / k);
        }
        return maxRes;
    }
}
// @lc code=end

