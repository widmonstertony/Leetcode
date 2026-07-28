/*
 * @lc app=leetcode id=35 lang=java
 *
 * [35] Search Insert Position
 */

// @lc code=start
class Solution {
    public int searchInsert(int[] nums, int target) {
        int left = 0, right = nums.length -1;
        //求上界 UPPER BOUND
        while (left <= right) {
            int mid = left + (right - left) / 2;
            // 如果中间值小于target，那么left就会被移动到mid + 1
            // 就确定了，循环结束后，left的值一定是大于等于target的
            if (nums[mid] < target) {
                left = mid + 1;
            }
            else {
                right = mid - 1;
            }
        }
        return left;
    }
}
// @lc code=end

