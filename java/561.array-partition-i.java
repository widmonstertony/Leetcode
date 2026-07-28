/*
 * @lc app=leetcode id=561 lang=java
 *
 * [561] Array Partition I
 */

// @lc code=start
class Solution {
    public int arrayPairSum(int[] nums) {
        //从小到大排序
        Arrays.sort(nums);
        int currSum = 0;
        // 取小的那个，因为是从小到大，所以取i就好
        for (int i = 0; i < nums.length; i+= 2) {
            currSum += nums[i];
        }
        return currSum;
    }
}
// @lc code=end

