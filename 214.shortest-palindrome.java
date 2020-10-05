/*
 * @lc app=leetcode id=214 lang=java
 *
 * [214] Shortest Palindrome
 */

// @lc code=start
class Solution {
    public String shortestPalindrome(String s) {
        if(s.length() == 0 || s.length() == 1) return s;
        int i = 0, j = s.length() - 1;
        while(j >= 0) {
            if(s.charAt(i) == s.charAt(j)) {
                i++;
            }
            j--;
        }
        if(i == s.length()) return s;
        String tem = s.substring(i);
        StringBuilder sb = new StringBuilder(tem);
        sb.reverse();
        sb.append(shortestPalindrome(s.substring(0, i)));
        sb.append(tem);
        return sb.toString();
    }
}
// @lc code=end

