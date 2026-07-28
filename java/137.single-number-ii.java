/*
 * @lc app=leetcode id=137 lang=java
 *
 * [137] Single Number II
 */

// @lc code=start
class Solution {
    public int singleNumber(int[] nums) {
        int res = 0;
        // 建立一个 32 位的数字，来统计所有数字的每一位上1出现的个数
        for (int i = 0; i < 32; i++) {
            int sum = 0;
            // 获得所有数字的当前第i位上的1出现的个数
            for (int j = 0; j < nums.length; j++) {
                // 把当前数右移i位再&1，也就是获得当前数字num[j]的第i位的数字是不是1
                sum += (nums[j] >> i) & 1;
            }
            // 把每个数的对应位都加起来对3取余
            // 如果该整数出现了三次，它的每一位对3取余为0
            res |= (sum % 3) << i;
        }
        //每一位对3取余剩下来的1拼凑出来的就是那个多余的数字
        return res;
    }
}
// @lc code=end

