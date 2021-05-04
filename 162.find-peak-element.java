/*
 * @lc app=leetcode id=162 lang=java
 *
 * [162] Find Peak Element
 */

// @lc code=start
class Solution {
    public int findPeakElement(int[] nums) {
        int left = 0, right = nums.length -1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (mid + 1 >= nums.length) {
                break;
            }
            // 因为 nums[-1] = nums[n] = -∞ 负无穷大
            // 所以我们只需要找到一个上升部分的最后那个数就是peak
            // 如果中间数小于中间数后面那个，说明我们要找的上升部分最后那个数在右边
            else if (nums[mid] < nums[mid + 1]) {
                left = mid + 1;
            }
            // 否则左边一定有一个上升的部分
            else {
                right = mid - 1;
            }
        }
        return left;
    }
}
// @lc code=end

