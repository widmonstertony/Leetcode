/*
 * @lc app=leetcode id=34 lang=java
 *
 * [34] Find First and Last Position of Element in Sorted Array
 */

// @lc code=start
class Solution {
    public int[] searchRange(int[] nums, int target) {
        int left = 0, right = nums.length -1;
        // 二分法找到target
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] < target) {
                left = mid + 1;
            }
            else if (nums[mid] > target) {
                right = mid - 1;
            }
            // 找到后左右移动坐标找到边界
            else {
                left = mid;
                while (left >= 0 && nums[left] == target) {
                    left--;
                }
                right = mid;
                while (right < nums.length && nums[right] == target) {
                    right++;
                }
                return new int[]{left + 1, right - 1};
            }
        }
        return new int[]{-1, -1};
    }
}
// @lc code=end

