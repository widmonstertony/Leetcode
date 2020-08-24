import java.util.Arrays;

/*
 * @lc app=leetcode id=300 lang=java
 *
 * [300] Longest Increasing Subsequence
 */

// @lc code=start
class Solution {
    // O(n^2)
    public int lengthOfLIS(int[] nums) {
        // dp[i] 表示以 nums[i] 为结尾的最长递增子串的长度
        int[] dp = new int[nums.length];
        Arrays.fill(dp, 1);
        int maxRes = 0;
        for (int i = 0; i < nums.length; i++) {
            for (int j = 0; j < i; j++) {
                // 对于每一个 nums[i]，从第一个数再搜索到i
                // 如果发现某个数小于 nums[i]
                if (nums[j] < nums[i]) {
                    // 用那个小于 num[i]的数nums[j]的dp值加1后的值
                    // 也就是以那个小于nums[i]的数nums[j]作为结尾，再加上当前数字nums[i]的这个子串的长度
                    // 用这个长度来更新 以 nums[i] 为结尾的最长递增子串的长度
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
            maxRes = Math.max(maxRes, dp[i]);
        }
        return maxRes;
    }
    // public int lengthOfLIS(int[] nums) {
    //     // O(nlogn) 解法 二分查找
    //     List<Integer> dp = new ArrayList<>();
    //     //遍历原数组
    //     //对于每一个遍历到的数字, 用二分查找法在dp数组找第一个不小于它的数字
    //     for (int num: nums) {
    //         // O(nlogn) 二分搜索
    //         int left = 0, right = dp.size();
    //         while (left < right) {
    //             int mid = left + (right - left) / 2;
    //             if (dp.get(mid) < num) {
    //                 left = mid + 1;
    //             }
    //             else {
    //                 right = mid;
    //             }
    //         }
    //         // 如果找到了，则用当前数字代替第一个大于它的数字
    //         if (left < dp.size()) {
    //             dp.set(left, num);
    //         }
    //         // 如果这个数字不存在，也就是当前数字是dp数组里最大的数字
    //         // 那么直接在dp数组后面加上当前数字
    //         else {
    //             dp.add(num);
    //         }
    //     }
    //     return dp.size();
    // }
}
// @lc code=end

