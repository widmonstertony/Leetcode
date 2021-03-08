/*
 * @lc app=leetcode id=291 lang=java
 *
 * [291] Word Pattern II
 */

// @lc code=start
class Solution {
    // backtracking
    public boolean wordPatternMatch(String pattern, String s) {
        Map<Character, String> patternMap = new HashMap<>();
        return helper(pattern, s, 0, 0, patternMap);
    }
    
    private boolean helper(String pattern, String s, int patIdx, int sIdx, Map<Character, String> patternMap) {
        if (patIdx == pattern.length() && sIdx == s.length()) {
            return true;
        }
        if (patIdx == pattern.length() || sIdx == s.length()) {
            return false;
        }
        // 先获得当前的char
        char currChar = pattern.charAt(patIdx);
        // 然后一个一个试String和char的map，看是否可以通过        
        for (int i = sIdx; i < s.length(); i++) {
            String currStr = s.substring(sIdx, i + 1);
            if (!patternMap.containsKey(currChar)) {
                if (!patternMap.containsValue(currStr)) {
                    patternMap.put(currChar, currStr);
                    if (helper(pattern, s, patIdx + 1, i + 1, patternMap)) {
                        return true;
                    }
                    else {
                        patternMap.remove(currChar);
                    }
                }
            }
            else if (patternMap.get(currChar).equals(currStr)) {
                if (helper(pattern, s, patIdx + 1, i + 1, patternMap)) {
                    return true;
                }
            }
        }
        return false;
    }
}
// @lc code=end

