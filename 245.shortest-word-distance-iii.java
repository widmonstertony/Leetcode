/*
 * @lc app=leetcode id=245 lang=java
 *
 * [245] Shortest Word Distance III
 */

// @lc code=start
class Solution {
    public int shortestWordDistance(String[] words, String word1, String word2) {
        int left = -1;
        int res = Integer.MAX_VALUE;
        for (int i = 0; i < words.length; i++) {
            String currWord = words[i];
            if (currWord.equals(word1) || currWord.equals(word2)) {
                if (left >= 0) {
                    // 和243的区别，因为两个词可以一样，所以把两个词一样的情况也加进去
                    if (!words[left].equals(currWord) || word1.equals(word2)) {
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

