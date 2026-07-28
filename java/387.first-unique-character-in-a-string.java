/*
 * @lc app=leetcode id=387 lang=java
 *
 * [387] First Unique Character in a String
 */

// @lc code=start
class Solution {
    public int firstUniqChar(String s) {
        char[] hashChar = new char[26];
        for (int i = 0; i < s.length(); i++) {
            hashChar[s.charAt(i) - 'a']++;
        }
        for (int i = 0; i < s.length(); i++) {
            if (hashChar[s.charAt(i) - 'a'] == 1) {
                return i;
            }
        }
        return -1;
    }
}
// @lc code=end

