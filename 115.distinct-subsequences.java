/*
 * @lc app=leetcode id=115 lang=java
 *
 * [115] Distinct Subsequences
 */

// @lc code=start
class Solution {
    public int numDistinct(String s, String t) {
        // dp[i][j]表示从s[i]生成t[j]总共有多少种方法
        int[][] dp = new int[s.length() + 1][t.length() + 1];
        for (int i = 0; i <= s.length(); i++) {
            for (int j = 0; j <= t.length(); j++) {
                if (j == 0) {
                    dp[i][j] = 1;
                }
                else if (i == 0) {
                    
                }
                // 当前字符i生成当前字符j的方法数等于 
                // 当前字符i之前那个字符生成 要生成的字符j之前那个字符 有多少种方法
                // 加上 当前字符i之前那个字符 生成j有多少种方法
                else if (s.charAt(i - 1) == t.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j] + dp[i - 1][j - 1];
                }
                // 当前字符i 生成 当前字符j 的方法数
                // 等于 当前字符i之前那个字符 生成j有多少种方法 （相当于当前字符i不能生成j）
                else {
                    dp[i][j] = dp[i - 1][j];
                }
            }
        }
        return dp[s.length()][t.length()];
    }
}
// @lc code=end

