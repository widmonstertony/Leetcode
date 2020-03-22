/*
 * @lc app=leetcode id=29 lang=java
 *
 * [29] Divide Two Integers
 */

// @lc code=start
class Solution {
    public int divide(int dividend, int divisor) {
        // 二分法
        int sign = -1;
        // 把所有的正负号相反
        // 因为负的比正的多1，防止overflow
        if (dividend > 0) {
            dividend = -dividend;
            sign *= -1;
        }
        if (divisor > 0) {
            divisor = -divisor;
            sign *= -1;
        }
        int res = 0;
        // 因为当前都是负的
        // 所以divident和divisor对比，是比小
        while (dividend <= divisor) {
            // 一直双倍减去当前的divisor，再把最大的那个从dividen中减去
            int currSum = -1;
            int currDivisor = divisor;
            // 这里，一定要用左边减，因为用加法的话，可能会overflow
            while(dividend - currDivisor < currDivisor) {
                currSum += currSum;
                currDivisor += currDivisor;
            }
            res += currSum;
            dividend -= currDivisor;
        }
        // 如果是最小的那个Integer，代表已经overflow了，是最大的那个值
        if (sign < 0 && res == Integer.MIN_VALUE) {
            return Integer.MAX_VALUE;
        }
        return sign * res;
    }
}
// @lc code=end

