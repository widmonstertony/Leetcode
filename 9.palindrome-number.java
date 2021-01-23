/*
 * @lc app=leetcode id=9 lang=java
 *
 * [9] Palindrome Number
 */

// @lc code=start
class Solution {
    public boolean isPalindrome(int x) {
        if (x < 0) {
            return false;
        }
        if (x == 0) {
            return true;
        }
        int originalX = x;
        int tmpBase = 0;
        while (x > 0) {
            // 如果是回文串，反转后仍是原数字
            // 就不可能溢出，只要溢出一定不是 palindrome 返回 false 就行
            // 所以这里不需要检查是否溢出
            tmpBase = tmpBase * 10 + x % 10;
            x = x / 10;
        }
        return tmpBase == originalX;
    }
}
// @lc code=end

