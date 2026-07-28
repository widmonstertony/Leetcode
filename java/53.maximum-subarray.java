/*
 * @lc app=leetcode id=53 lang=java
 *
 * [53] Maximum Subarray
 */

// @lc code=start
class Solution {
    public int maxSubArray(int[] nums) {
        // O(n) 解法， dp 
        int maxRes = nums[0];
        int sum = nums[0];
        for (int i = 1; i < nums.length; i++) {
            // 如果之前的dp数（之前的总和）是正数，加上当前数
            // if (sum > 0) {
            //     sum += nums[i];
            // }
            // // 不然从当前重新开始
            // else {
            //     sum = nums[i];
            // }
            sum = Math.max(sum + nums[i], nums[i]);
            // 一直记录每个dp的值，对比记下最大值
            maxRes = Math.max(maxRes, sum);
        }
        return maxRes;
    }
    // 分治法的思想
    // public int maxSubArray(int[] nums) {
    //     return helper(nums, 0, nums.length - 1);
    // }
    // // 类似于二分搜索法
    // private int helper(int[] nums, int left, int right) {
    //     if (left >= right) {
    //         return nums[left];
    //     }
    //     // 需要把数组一分为二，分别找出左边和右边的最大子数组之和
    //     int mid = left + (right - left) / 2;
    //     int leftMax = helper(nums, left, mid - 1);
    //     int rightMax = helper(nums, mid + 1, right);
    //     // 从中间开始向左右分别扫描
    //     // 求出的最大值分别和左右两边得出的最大值相比较取最大的那一个
    //     int midMax = nums[mid], currMax = midMax;
    //     for (int i = mid - 1; i >= left; i--) {
    //         midMax += nums[i];
    //         currMax = Math.max(midMax, currMax);
    //     }
    //     midMax = currMax;
    //     for (int i = mid + 1; i <= right; i++) {
    //         midMax += nums[i];
    //         currMax = Math.max(midMax, currMax);
    //     }
    //     return Math.max(currMax, Math.max(leftMax, rightMax));
    // }
}
// @lc code=end

