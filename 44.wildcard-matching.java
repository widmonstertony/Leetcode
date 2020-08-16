/*
 * @lc app=leetcode id=44 lang=java
 *
 * [44] Wildcard Matching
 */

// @lc code=start
class Solution {
    public boolean isMatch(String s, String p) {
        return helper(s, p ,0, 0) > 1;
    }
    // 返回0表示匹配到了s串的末尾，但是未匹配成功
    // 返回1表示未匹配到s串的末尾就失败了
    // 返回2表示成功匹配。那么只有返回值大于1，才表示成功匹配
    private int helper(String s, String p, int i, int j) {
        // 若s串和p串都匹配完成了，返回状态2
        if (i == s.length() && j == p.length()) {
            return 2;
        }
        // 若s串未匹配完，p串匹配完了，返回状态1
        if (j == p.length()) {
            return 1;
        }
        // 若s串匹配完成了，但p串但当前字符不是星号，返回状态0
        if (i == s.length() && p.charAt(j) != '*') {
            return 0;
        }
        // 若s串未匹配完，且当前字符成功匹配的话，对下一个位置调用递归
        if (i < s.length() && (s.charAt(i) == p.charAt(j) || p.charAt(j) == '?')) {
            return helper(s, p, i + 1, j + 1);
        }
        // 若p串当前字符是星号
        if (p.charAt(j) == '*') {
            // 首先跳过连续的星号
            if (j + 1 < p.length() && p.charAt(j + 1) == '*') {
                return helper(s, p, i, j + 1);
            }
            // 然后分别让星号匹配空串，一个字符，两个字符，....，直到匹配完整个s串
            for (int k = 0; k <= s.length() - i; k++) {
                int res = helper(s, p, i + k, j + 1);
                // 剪枝，当前返回值为状态0或者2的时候，返回，否则继续遍历
                // 如果仅仅是状态2的时候才返回，会有大量的重复计算
                // 因为当返回值为状态0的时候，已经没有继续循环下去的必要了，非常重要的一刀剪枝
                if (res == 0 || res == 2) {
                    return res;
                }
            }
        }
        return 1;
    }
}
// @lc code=end

