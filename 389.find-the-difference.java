/*
 * @lc app=leetcode id=389 lang=java
 *
 * [389] Find the Difference
 */

// @lc code=start
class Solution {
    public char findTheDifference(String s, String t) {
        int[] charCnt = new int[26];
        HashSet<Character> charSet = new HashSet<>();
        for (char eachChar : s.toCharArray()) {
            charCnt[eachChar - 'a']++;
            charSet.add(eachChar);
        }
        for (char eachChar : t.toCharArray()) {
            if (charCnt[eachChar - 'a']-- == 0) {
                return eachChar;
            }
            if (charCnt[eachChar - 'a'] == 0) {
                charSet.remove(eachChar);
            }
        }
        for (char eachChar : charSet) {
            return eachChar;
        }
        return 'a';
    }
}
// @lc code=end

