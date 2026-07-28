/*
 * @lc app=leetcode id=33 lang=java
 *
 * [33] Search in Rotated Sorted Array
 */

// @lc code=start
class Solution {
    public int search(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) {
                return mid;
            }
            // 如果中间值小于右边
            // 说明右边是sorted
            else if (nums[mid] < nums[right]) {
            // 既然右边是sorted
            // 如果target在右边的中间，也就是大等于mid，但是小等于right
            // 那么只要右边
                if (nums[mid] <= target && target <= nums[right]) {
                    left = mid + 1;
                }
                else {
                    right = mid - 1;
                }
            }
            // 左边sorted
            else {
                // 如果左边是sorted
                // 那么如果target在左边的中间，也就是大等于left，但小等于mid
                // 那么只要左边
                if (nums[left] <= target && target <= nums[mid]) {
                    right = mid - 1;
                }
                else {
                    left = mid + 1;
                }
            }
        }
        return -1;
    }
}
// @lc code=end

