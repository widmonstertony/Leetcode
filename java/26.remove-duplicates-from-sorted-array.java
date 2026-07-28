/*
 * @lc app=leetcode id=26 lang=java
 *
 * [26] Remove Duplicates from Sorted Array
 */

// @lc code=start
class Solution {
    public int removeDuplicates(int[] nums) {
        // 允许的相同数
        final int DUPLICATE_ALLOWED = 1;
        if (nums.length <= DUPLICATE_ALLOWED) return nums.length;
        
        //0和1位一定合法，从2号位开始
        int res = DUPLICATE_ALLOWED; // [0, end) is the result string, excluding nums[end], so end is the final length
        // res之前坐标的一定合法
        for (int i = DUPLICATE_ALLOWED; i < nums.length; i++) {
            // 找到和res - DUPLICATE_ALLOWED不一样的数字放在res的位置
            // 这样可以确保中间跳过的部分是和res - DUPLICATE_ALLOWED一样的，也就是重复数多于2的
            // 如果当前值和合法值头两位不相等，那么res可以被当前值替代，从0到res依然合法
            if (nums[res - DUPLICATE_ALLOWED] != nums[i]) { // 只需要对比这两个数是否相等即可
                nums[res] = nums[i];
                res++;
            }
        }
        return res;
    }
}
// @lc code=end

