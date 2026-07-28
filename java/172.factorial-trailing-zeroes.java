/*
 * @lc app=leetcode id=172 lang=java
 *
 * [172] Factorial Trailing Zeroes
 */

// @lc code=start
class Solution {
    public int trailingZeroes(int n) {
        int res = 0;
        // 找乘数中10的个数，那把所有5的个数找出来
        while (n > 0) {
            res += n / 5;
            n /= 5;
        }
        return res;
    }
}
// @lc code=end

