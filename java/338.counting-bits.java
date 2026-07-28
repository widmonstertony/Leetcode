/*
 * @lc app=leetcode id=338 lang=java
 *
 * [338] Counting Bits
 */

// @lc code=start
class Solution {
    public int[] countBits(int num) {
        int[] dp = new int[num + 1];
        dp[0] = 0;
        for (int i = 0; i <= num; i++) {
            // 遇到偶数时，其1的个数和该偶数除以2得到的数字的1的个数相同
            if (i % 2 == 0) {
                dp[i] = dp[i / 2];
            }
            // 遇到奇数时，其1的个数等于该奇数除以2得到的数字的1的个数再加1
            else {
                dp[i] = dp[i / 2] + 1;
            }
        }
        return dp;
    }
}
// @lc code=end

