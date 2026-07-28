/*
 * @lc app=leetcode id=214 lang=java
 *
 * [214] Shortest Palindrome
 */

// @lc code=start
class Solution {
    public String shortestPalindrome(String s) {
        if(s.length() <= 1) {
            return s;
        }
        // 找出字符串s的最长回文前缀
        int leftIdx = 0, rightIdx = s.length() - 1;
        while (rightIdx >= 0) {
            if (s.charAt(leftIdx) == s.charAt(rightIdx)) {
                leftIdx++;
            }
            rightIdx--;
        }
        if (leftIdx == s.length()) {
            return s;
        }
        StringBuilder resBuilder = new StringBuilder(s.substring(leftIdx));
        resBuilder.reverse();
        resBuilder.append(shortestPalindrome(s.substring(0, leftIdx)));
        resBuilder.append(s.substring(leftIdx));
        return resBuilder.toString();
    }
}
// @lc code=end

