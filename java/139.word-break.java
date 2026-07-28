import java.util.HashSet;
import java.util.List;

/*
 * @lc app=leetcode id=139 lang=java
 *
 * [139] Word Break
 */

// @lc code=start
class Solution {
    public boolean wordBreak(String s, List<String> wordDict) {
        // dp[i] 表示范围 [0, i) 内的子串是否可以拆分
        // [0, i) 分为 [0, j) 和 [j, i)
        boolean[] dp = new boolean[s.length() + 1];
        Set<String> wordSet = new HashSet<>();
        for (String word: wordDict) {
            wordSet.add(word);
        }
        dp[0] = true;
        for (int i = 0; i <= s.length(); i++) {
            // 把s分成0 - j 和 j - i
            for (int j = 0; j < i; j++) {
                if (dp[j] && wordDict.contains(s.substring(j, i))) {
                    dp[i] = true;
                }
            }
        }
        return dp[s.length()];
    }
}
// @lc code=end

