/*
 * @lc app=leetcode id=233 lang=java
 *
 * [233] Number of Digit One
 */

// @lc code=start
class Solution {
    public int countDigitOne(int n) {
        int res = 0, currDig = 1, b = 1;
        while (n > 0) {
            // 看十位上的数字是否大于等于2，是的话就要加上多余的 10 个 '1'
            // 用 (x+8)/10 来判断一个数是否大于等于2
            res += (n + 8) / 10 * currDig + (n % 10 == 1 ? b : 0);
            b += n % 10 * currDig;
            currDig *= 10;
            n /= 10;
        }
        return res;
    }
}
// @lc code=end

