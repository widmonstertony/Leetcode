/*
 * @lc app=leetcode id=392 lang=java
 *
 * [392] Is Subsequence
 */

// @lc code=start
class Solution {
    public boolean isSubsequence(String s, String t) {
        if (s.length() == 0) {
            return true;
        }
        int sIdx = 0, tIdx = 0;
        for (; tIdx < t.length(); tIdx++) {
            if (s.charAt(sIdx) == t.charAt(tIdx)) {
                sIdx++;
            }
            if (sIdx == s.length()) {
                return true;
            }
        }
        return false;
    }
}
// @lc code=end

