/*
 * @lc app=leetcode id=243 lang=java
 *
 * [243] Shortest Word Distance
 */

// @lc code=start
class Solution {
    public int shortestDistance(String[] words, String word1, String word2) {
        int left = -1;
        int res = Integer.MAX_VALUE;
        for (int i = 0; i < words.length; i++) {
            String currWord = words[i];
            if (currWord.equals(word1) || currWord.equals(word2)) {
                if (left >= 0) {
                    if (!words[left].equals(currWord)) {
                        res = Math.min(res, i - left);
                    }
                }
                left = i;
            }
        }
        return res;
    }
}
// @lc code=end

