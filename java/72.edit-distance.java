/*
 * @lc app=leetcode id=72 lang=java
 *
 * [72] Edit Distance
 */

// @lc code=start
class Solution {
    public int minDistance(String word1, String word2) {
        // dp[i][j]代表从word1的第i位之前到word2的第j位之前最少需要多少步
        int[][] dp = new int[word1.length() + 1][word2.length() + 1];
        // 初始化，每增加一个字符，步数加一
        for (int i = 0; i <= word1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= word2.length(); j++) {
            dp[0][j] = j;
        }
        for (int i = 1; i <= word1.length(); i++) {
            for (int j = 1; j <= word2.length(); j++) {
                // 如果word1的当前字符(i-1) 和word2的当前字符(j-1)相同，表示步数不需要增加
                // dp当前步数就等于两个字符串没有当前字符状态时的步数，也就是左上角那格
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                }
                //否则 左上角，左边，上边，找到哪个用最少步数，加上1，等于当前步数
                // 左上角代表替换，左边代表删word1的当前字符，上边代表删word2的当前字符
                else {
                    dp[i][j] = Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]) + 1;
                }
            }
        }
        return dp[word1.length()][word2.length()];
    }
}
// @lc code=end

