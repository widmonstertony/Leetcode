/*
 * @lc app=leetcode id=97 lang=java
 *
 * [97] Interleaving String
 */

// @lc code=start
class Solution {
    public boolean isInterleave(String s1, String s2, String s3) {
        if (s1.length() + s2.length() != s3.length()) {
            return false;
        }
        // dp[i][j] 代表s1到i的位置和s2到j的位置，能组成s3的到i+j位置的字符串
        boolean[][] dp = new boolean[s1.length() + 1][s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {
                // 都是0开始的话，初始化为true
                if (i == 0 && j == 0) {
                    dp[i][j] = true;
                }
                // 如果完全不用s1只用s2，那么就要确保当前s2的字符和s3的一样，并且之前的也可以匹配
                else if (i == 0) {
                    dp[i][j] = dp[i][j - 1] && s2.charAt(j - 1) == s3.charAt(j - 1);
                }
                // 反之既然
                else if (j == 0) {
                    dp[i][j] = dp[i - 1][j] && s1.charAt(i - 1) == s3.charAt(i - 1);
                }
                // 如果都不为0，则要看是否s1，s2里有一个字符串可以match s3并且之前到之前为止都可以match
                else {
                    dp[i][j] = (dp[i - 1][j] && s1.charAt(i - 1) == s3.charAt(i + j - 1)) 
                            || (dp[i][j - 1] && s2.charAt(j - 1) == s3.charAt(i + j - 1));
                }
            }
        }
        return dp[s1.length()][s2.length()];
    }
}
// @lc code=end

