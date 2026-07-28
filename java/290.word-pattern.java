import java.util.HashMap;
import java.util.Map;

/*
 * @lc app=leetcode id=290 lang=java
 *
 * [290] Word Pattern
 */

// @lc code=start
class Solution {
    public boolean wordPattern(String pattern, String s) {
        String[] wordList = s.split(" ");
        Map<Character, String> charWordsMap = new HashMap<>();
        Map<String, Character> wordCharMap = new HashMap<>();
        if (wordList.length != pattern.length()) {
            return false;
        }
        for (int i = 0; i < pattern.length(); i++) {
            char currChar = pattern.charAt(i);
            String currWord = wordList[i];
            if (charWordsMap.containsKey(currChar)) {
                if (!charWordsMap.get(currChar).equals(currWord)) {
                    return false;
                }
            }
            else if (wordCharMap.containsKey(currWord)) {
                if (wordCharMap.get(currWord) != currChar) {
                    return false;
                }
            }
            else {
                charWordsMap.put(currChar, currWord);
                wordCharMap.put(currWord, currChar);
            }
        }
        return true;
    }
}
// @lc code=end

