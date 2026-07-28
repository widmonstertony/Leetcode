/*
 * @lc app=leetcode id=953 lang=java
 *
 * [953] Verifying an Alien Dictionary
 */

// @lc code=start
class Solution {
    public boolean isAlienSorted(String[] words, String order) {
        int[] charToIdx = new int[26];

        for (int i = 0; i < 26; i++) {
            charToIdx[order.charAt(i) - 'a'] = i;
        }

        for (int i = 0; i < words.length - 1; i++) {
            if (!isTwoWordsSorted(words[i], words[i + 1], charToIdx)) {
                return false;
            }
        }

        return true;
    }

    private boolean isTwoWordsSorted(String firstStr, String secondStr, int[] charToIdx) {

        if (firstStr.length() > secondStr.length()) {
            return !isTwoWordsSorted(secondStr, firstStr, charToIdx);
        }

        for (int i = 0; i < firstStr.length(); i++) {
            if (charToIdx[firstStr.charAt(i) - 'a'] == charToIdx[secondStr.charAt(i) - 'a']) {
                continue;
            }
            else if (charToIdx[firstStr.charAt(i) - 'a'] > charToIdx[secondStr.charAt(i) - 'a']) {
                return false;
            }
            else {
                break;
            }
        }

        return true;
    }
}
// @lc code=end

