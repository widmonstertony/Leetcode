/*
 * @lc app=leetcode id=516 lang=java
 *
 * [516] Longest Palindromic Subsequence
 */

// @lc code=start
class Solution {
    public int longestPalindromeSubseq(String s) {
        // dp[i][j] 代表从i到j的最长回文子序列
        int[][] dp = new int[s.length()][s.length()];
        for (int i = s.length() - 1; i >= 0; i--) {
            dp[i][i] = 1;
            for (int j = i + 1; j < s.length(); j++) {
                // 如果i的字符等于j的字符，则把两个字符的长度加入答案
                if (s.charAt(i) == s.charAt(j)) {
                    dp[i][j] = dp[i + 1][j - 1] + 2;
                }
                // 否则取去掉头尾的dp中最大的一个
                else {
                    dp[i][j] = Math.max(dp[i + 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[0][s.length() - 1];
    }
}
// @lc code=end

