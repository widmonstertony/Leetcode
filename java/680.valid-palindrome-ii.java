/*
 * @lc app=leetcode id=680 lang=java
 *
 * [680] Valid Palindrome II
 */

// @lc code=start
class Solution {
    public boolean validPalindrome(String s) {
        int left = 0, right = s.length() - 1;
        while (left < right) {
            // 遇到不匹配的时候
            // 删除左边的字符和右边的字符都要算一遍
            if (s.charAt(left) != s.charAt(right)) {
                return isVaildPalindrome(s, left + 1, right) || isVaildPalindrome(s, left, right - 1);
            }
            else {
                left++;
                right--;
            }
        }

        return true;
    }
    private boolean isVaildPalindrome(String s, int left, int right) {
        while (left < right) {
            if (s.charAt(left) != s.charAt(right)) {
                return false;
            }
            else {
                left++;
                right--;
            }
        }
        return true;
    }
}
// @lc code=end

