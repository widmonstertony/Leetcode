/*
 * @lc app=leetcode id=81 lang=java
 *
 * [81] Search in Rotated Sorted Array II
 */

// @lc code=start
class Solution {
    public boolean search(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            // 如果当前中间值和它右边的值一样的话，中间值往右移
            // 确保不会移过右界
            while (mid < right && nums[mid] == nums[mid + 1]) {
                mid++;
            }
            // 如果中间值就是我们要找的值
            if (nums[mid] == target) {
                return true;
            }
            //  右边sorted
            //  注意这里，如果中间值等于右边值，也说明右边sorted了
            //  因为右边只有一位了
            // 所以其实这里包括了中间值小于等于右边值
            if (nums[mid] < nums[right] || mid == right) {
                // 如果target在右边的范围的话，那么去右边
                if (nums[mid] <= target && target <= nums[right]) {
                    left = mid + 1;
                }
                else {
                    right = mid - 1;
                }
            }
            // 同理
            else {
                if (nums[left] <= target && target <= nums[mid]) {
                    right = mid - 1;
                }
                else {
                    left = mid + 1;
                }
            }
        }
        return false;
    }
}
// @lc code=end

