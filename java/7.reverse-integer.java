/*
 * @lc app=leetcode id=7 lang=java
 *
 * [7] Reverse Integer
 */

// @lc code=start
class Solution {
    public int reverse(int x) {
        int res = 0;
        while (x != 0) {
            //在乘以10前，先check如果乘以10是否会超过整数的最大值
            if (Math.abs(res) > Integer.MAX_VALUE / 10) {
                return 0;
            }
            res = res * 10 + x % 10;
            x /= 10;
        }
        return res;
    }
}
// @lc code=end

