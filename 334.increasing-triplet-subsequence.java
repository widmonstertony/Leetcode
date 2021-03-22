/*
 * @lc app=leetcode id=334 lang=java
 *
 * [334] Increasing Triplet Subsequence
 */

// @lc code=start
class Solution {
    public boolean increasingTriplet(int[] nums) {
        // first是到当前位置的最小元素
        // second是位于first之后，大于first并且距离first最近的元素
        int first = Integer.MAX_VALUE, second = Integer.MAX_VALUE;
        for (int num: nums) {
            if (num <= first) {
                first = num;
            }
            // 如果当前数大于first，但又小于second
            // 因为当前数肯定在first右边，所以更新second
            // 这样确保了second和first一定都满足题意
            else {
                if (num <= second) {
                    second = num;
                }
                // 如果这个数比first和second都大，而且first和second还都满足题意
                // 说明我们找到了第三个数
                else {
                    return true;
                }
            }
        }
        return false;
    }
    // space O(n)
    // public boolean increasingTriplet(int[] nums) {
    //     // smallest[i] 代表左边到i的最小值
    //     int[] smallest = new int[nums.length];
    //     for (int i = 0; i < nums.length; i++) {
    //         if (i == 0) {
    //             smallest[i] = nums[i];
    //         }
    //         else {
    //             smallest[i] = Math.min(smallest[i - 1], nums[i]);
    //         }
    //     }
    //     // biggest[i] 代表i到右边的最大值
    //     int[] biggest = new int[nums.length];
    //     for (int i = nums.length - 1; i >= 0; i--) {
    //         if (i == nums.length - 1) {
    //             biggest[i] = nums[i];
    //         }
    //         else {
    //             biggest[i] = Math.max(biggest[i + 1], nums[i]);
    //         }
    //     }
    //     // 如果有一个数，大于左边的最小值，小于右边的最大值
    //     // 说明满足题目的要求
    //     for (int i = 0; i < nums.length; i++) {
    //         if (smallest[i] < nums[i] && nums[i] < biggest[i]) {
    //             return true;
    //         }
    //     }
    //     return false;
    // }
    // public boolean increasingTriplet(int[] nums) {
    //     // brute force
    //     // dp[i] 代表nums[i]前面所有小于nums[i]的数量
    //      这个解法用来解不止3个，而是n个，最长递增子序列
    //     int[] dp = new int[nums.length];
    //     for (int i = 0; i < nums.length; i++) {
    //         for (int j = 0; j < i; j++) {
    //             if (nums[j] < nums[i]) {
    //                 dp[i] = Math.max(dp[j] + 1, dp[i]);
    //             }
    //             if (dp[i] >= 2) {
    //                 return true;
    //             }
    //         }
    //     }
    //     return false;
    // }
}
// @lc code=end

