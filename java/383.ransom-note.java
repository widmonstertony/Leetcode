/*
 * @lc app=leetcode id=383 lang=java
 *
 * [383] Ransom Note
 */

// @lc code=start
class Solution {
    public boolean canConstruct(String ransomNote, String magazine) {
        int[] charMap = new int[26];
        for (char eachChar : magazine.toCharArray()) {
            charMap[eachChar - 'a']++;
        }
        for (char eachChar: ransomNote.toCharArray()) {
            if (charMap[eachChar - 'a'] == 0) {
                return false;
            }
            charMap[eachChar - 'a']--;
        }
        return true;
    }
}
// @lc code=end

