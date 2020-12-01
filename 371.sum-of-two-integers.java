/*
 * @lc app=leetcode id=371 lang=java
 *
 * [371] Sum of Two Integers
 */

// @lc code=start
class Solution {
    public int getSum(int a, int b) {
        if (b == 0) {
            return a;
        }
        // 不考虑进位的加法
        int sum = a ^ b;
        // 只考虑进位得到的carry
        int carry = (a & b) << 1;
        // 最后把这两个数加起来，直到carry变成0为止
        return getSum(sum, carry);
    }
}
// @lc code=end

