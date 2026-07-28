/*
 * @lc app=leetcode id=186 lang=java
 *
 * [186] Reverse Words in a String II
 */

// @lc code=start
class Solution {
    // in place，把helper里的写进原来的function就是O(1)了
    public void reverseWords(char[] s) {
        // 先把整个string翻过来
        helper(0, s.length - 1, s);
        // 再把每个词翻过来
        int left = 0;
        for (int right = 0; right < s.length; right++) {
            // 如果当前右边是空格，说明当前左指针到右指针是一个词
            // 颠倒它
            if (s[right] == ' ') {
                helper(left, right - 1, s);
                left = right + 1;
            }
            else if (right == s.length - 1) {
                helper(left, right, s);
            }
        }
    }
    // 把left到right的字母都翻转过来
    private void helper(int left, int right, char[] s) {
        while (left < right) {
            char tmpChar = s[left];
            s[left] = s[right];
            s[right] = tmpChar;
            left++;
            right--;
        }
    }
}
// @lc code=end

