/*
 * @lc app=leetcode id=372 lang=java
 *
 * [372] Super Pow
 */

// @lc code=start
class Solution {
    public int superPow(int a, int[] b) {
        long res = 1;
        for (int i = 0; i < b.length; i++) {
            res = myPow(res, 10) * myPow(a, b[i]) % 1337;
        }
        return (int) res;
    }
    // 二分法，记得要加上mod 1337，不然数字会过大
    private long myPow(long x, int n) {
        if (n == 0) {
            return 1;
        }
        // 偶数的话，x^2, 同时n变成一半
        if (n % 2 == 0) {
            return myPow(x * x % 1337, n / 2);
        }
        // 如果n是奇数，那么就把自己也乘上，然后x^2, 同时n变成一半
        else {
            return myPow(x * x % 1337, n / 2) * x;
        }
    }
}
// @lc code=end

