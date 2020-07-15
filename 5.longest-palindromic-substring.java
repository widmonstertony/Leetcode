/*
 * @lc app=leetcode id=5 lang=java
 *
 * [5] Longest Palindromic Substring
 */

// @lc code=start
class Solution {
    public String longestPalindrome(String s) {
        // dp[i][j] 代表从i到j是否为回文串
        boolean[][] dp = new boolean[s.length()][s.length()];
        int currMaxLength = 0;
        String res = "";

        // i从后往前，j从i的地方从前往后
        // 这样确保i + 1一定在范围里，j - 1也一定在范围里
        for (int i = s.length() - 1; i >= 0; i--) {
            for (int j = i; j < s.length(); j++) {

                if (s.charAt(i) == s.charAt(j) &&
                  (j - i <= 2 || dp[i + 1][j - 1] == true)) {

                    dp[i][j] = true;

                    if (j + 1 - i > currMaxLength) {
                        currMaxLength = j + 1 - i;
                        res = s.substring(i, j + 1);
                    }
                }
            }
        }
        return res;
    }
}
// @lc code=end

