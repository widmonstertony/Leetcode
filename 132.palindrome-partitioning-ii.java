/*
 * @lc app=leetcode id=132 lang=java
 *
 * [132] Palindrome Partitioning II
 */

// @lc code=start
class Solution {
    public int minCut(String s) {
        if (s.length() == 0) {
            return 0;
        }
        int n = s.length();
         // p[i][j] 代表从i到j是否为回文串
        boolean[][] p = new boolean[n][n];
        // dp[i] 表示区间 [i, n-1] 内的最小分割数
        int[] dp = new int[n];

        for (int i = n - 1; i >= 0; i--) {
            //  dp[i]初始化的最小分割数是把每个字母都分割
            dp[i] = n - 1 - i;

            for (int j = i; j < n; j++) {
                // 如果从i到j也是一个回文串
                if (s.charAt(i) == s.charAt(j) && (j - i <= 2 || p[i + 1][j - 1] == true)) {
                    p[i][j] = true;
                    // 如果j就是末尾，则说明当前不需要割一刀，i到末尾就是一个回文串
                    if (j == n - 1) {
                        dp[i] = 0;
                    }
                    // dp[i] 表示区间 [i, n-1] 内的最小分割数
                    // 因为从i到j是回文串，则当前的最小分割数
                    // 是从j + 1到最末尾的最小分割数，加上从i到j割的这一刀
                    else {
                        dp[i] = Math.min(dp[i], dp[j + 1] + 1);
                    }
                }
            }
        }
        return dp[0];
    }
}
// @lc code=end

