/*
 * @lc app=leetcode id=91 lang=java
 *
 * [91] Decode Ways
 */

// @lc code=start
class Solution {
    public int numDecodings(String s) {
        // dp简化的two variables解法
        int preWaysCnt = 1, currWaysCnt = 1; 

        for (int i = 0; i < s.length(); i++) {

            // 如果i之前那位是0，则完全没办法decode
            if (s.charAt(i) == '0') {
                currWaysCnt = 0;
            }

            // 如果i不是第一位，并且i之前的两位数字合起来小于26
            // 则需要再多加一种decode方式
            // 需要把previous更新为current
            if (i > 0 && (s.charAt(i - 1) == '1' || (s.charAt(i - 1) == '2' && s.charAt(i) <= '6'))) {
                currWaysCnt += preWaysCnt;
                preWaysCnt = currWaysCnt - preWaysCnt;
            }
            // 否则至少有一种decode方式，也就是之前的那个decode方式
            else if (i > 0) {
                preWaysCnt = currWaysCnt;
            }

        }

        return currWaysCnt;
    }

    // public int numDecodings(String s) {
    //     // dp[i] 代表i位置之前有多少种decode方式
    //     int[] dp = new int[s.length() + 1];
    //     dp[0] = 1;

    //     for (int i = 1; i <= s.length(); i++) {

    //         // 如果i之前那位是0，则完全没办法decode
    //         if (s.charAt(i - 1) == '0') {
    //             dp[i] = 0;
    //         }
    //         // 否则至少有一种decode方式，也就是之前的那个decode方式
    //         else {
    //             dp[i] = dp[i - 1];
    //         }

    //         // 如果i不是第一位，并且i之前的两位数字合起来小于26
    //         // 则需要再多加一种decode方式
    //         if (i > 1 && (s.charAt(i - 2) == '1' || (s.charAt(i - 2) == '2' && s.charAt(i - 1) <= '6'))) {
    //             dp[i] += dp[i - 2];
    //         }

    //     }

    //     return dp[s.length()];
    // }

    // public int numDecodings(String s) {
    //     // dp[i] 代表到i位置有多少种decode方式
    //     int[] dp = new int[s.length()];
    //     for (int i = 0; i < s.length(); i++) {

    //         // 如果i位是0，则完全没办法decode
    //         if (s.charAt(i) == '0') {
    //             dp[i] = 0;
    //         }
    //         // 否则至少有一种decode方式，也就是之前的那个decode方式
    //         else {
    //             if (i == 0) {
    //                 dp[i] = 1;
    //             }
    //             else {
    //                 dp[i] = dp[i - 1];
    //             }
    //         }

    //         // 如果i不是第一位，并且i之前的两位数字合起来小于26
    //         // 则需要再多加一种decode方式
    //         if (i > 0 && (s.charAt(i - 1) == '1' || (s.charAt(i - 1) == '2' && s.charAt(i) <= '6'))) {
    //             if (i == 1) {
    //                 dp[i] += 1;
    //             }
    //             else {
    //                 dp[i] += dp[i - 2];
    //             }
    //         }
            
    //     }

    //     return dp[s.length() - 1];
    // }

}
// @lc code=end

