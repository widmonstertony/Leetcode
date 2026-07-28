/*
 * @lc app=leetcode id=16 lang=java
 *
 * [16] 3Sum Closest
 */

// @lc code=start
class Solution {
    public int threeSumClosest(int[] nums, int target) {
        // 这里用long是因为会遇到res -(-1) 变成负数的情况
        long res = Integer.MAX_VALUE;
        Arrays.sort(nums);
        for (int i = 0; i < nums.length; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) {
                continue;
            }
            // 双指针
            int j = i + 1, k = nums.length - 1;
            while (j < k) {
                int currSum = nums[i] + nums[j] + nums[k];
                if (currSum == target) {
                    return target;
                }
                else {
                    if (Math.abs(res - target) > Math.abs(currSum - target)) {
                        res = currSum;
                    }
                    if (currSum > target) {
                        k--;
                    }
                    else {
                        j++;
                    }
                }
            }
        }
        return (int)res;
    }
}
// @lc code=end

