/*
 * @lc app=leetcode id=50 lang=java
 *
 * [50] Pow(x, n)
 */

// @lc code=start
class Solution {
    public double myPow(double x, int n) {
        if (n == 0) {
            return 1;
        }
        // 分治法和二分法
        // 如果n是奇数，那么就把自己也乘上，然后x^2, 同时n变成一半
        if (n % 2 > 0) {
            return x * myPow(x * x,  n / 2);
        }
        // 偶数的话，x^2, 同时n变成一半
        else if (n % 2 == 0){
            return myPow(x * x, n / 2);
        }
        // 如果是负数，那么就用1除以正数的范围，注意检测overflow
        else {
            if (n == Integer.MIN_VALUE) {
                return 1 / (x * myPow(x, -(n + 1)));
            }
            return 1 / myPow(x, -n);
        }
    }
}
// @lc code=end

