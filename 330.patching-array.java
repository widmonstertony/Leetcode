/*
 * @lc app=leetcode id=330 lang=java
 *
 * [330] Patching Array
 */

// @lc code=start
class Solution {
    public int minPatches(int[] nums, int n) {
        int minMiss = 1, resCnt = 0, i = 0;
        while (minMiss > 0 && minMiss <= n) {
            // 如果此时的num <= miss
            // 那么我们可以把我们能表示数的范围扩大到[0, miss+num)
            if (i < nums.length && nums[i] <= minMiss) {
                minMiss += nums[i];
                i++;
            }
            // 如果num>miss，那么此时我们需要添加一个数
            // 为了能最大限度的增加表示数范围，我们加上miss它本身
            else {
                minMiss += minMiss;
                resCnt++;
            }
        }
        return resCnt;
    }
}
// @lc code=end

