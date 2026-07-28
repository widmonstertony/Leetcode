/*
 * @lc app=leetcode id=80 lang=java
 *
 * [80] Remove Duplicates from Sorted Array II
 */

// @lc code=start
class Solution {
    public int removeDuplicates(int[] nums) {
        if (nums.length <= 2) return nums.length;
        
        //0和1位一定合法，从2号位开始
        int res = 2; // [0, end) is the result string, excluding nums[end], so end is the final length
        // res之前坐标的一定合法
        for (int i = 2; i < nums.length; i++) {
            // 找到和res - 2不一样的数字放在res的位置
            // 这样可以确保中间跳过的部分是和res - 2一样的，也就是重复数多于2的
            // 如果当前值和合法值头两位不相等，那么res可以被当前值替代，从0到res依然合法
            if (nums[res - 2] != nums[i]) { // 只需要对比这两个数是否相等即可
                nums[res] = nums[i];
                res++;
            }
        }
        return res;
    }
}
// @lc code=end

