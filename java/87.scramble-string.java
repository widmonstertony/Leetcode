/*
 * @lc app=leetcode id=87 lang=java
 *
 * [87] Scramble String
 */

// @lc code=start
class Solution {
    public boolean isScramble(String s1, String s2) {
        if (s1.length() != s2.length()) {
            return false;
        }
        if (s1.equals(s2)) {
            return true;
        }
        char[] s1CharArr = s1.toCharArray();
        char[] s2CharArr = s2.toCharArray();
        Arrays.sort(s1CharArr);
        Arrays.sort(s2CharArr);
        if (!new String(s1CharArr).equals(new String(s2CharArr))) {
            return false;
        }
        // 必然存在一个在 s1 上的长度 l1，将 s1 分成 s11 和 s12 两段
        // 同样有 s21 和 s22, 那么要么 s11 和 s21 是 scramble 的
        // 并且 s12 和 s22 是 scramble 的
        // 从1开始试，找到这个长度
        for (int i = 1; i < s1.length(); i++) {
            String s11 = s1.substring(0, i);
            String s12 = s1.substring(i);
            String s21 = s2.substring(0, i);
            String s22 = s2.substring(i);
            if (isScramble(s11, s21) && isScramble(s12, s22)) {
                return true;
            }
            // 有可能当前的s2需要反过来分
            s21 = s2.substring(s1.length() - i);
            s22 = s2.substring(0, s1.length() - i);
            if (isScramble(s11, s21) && isScramble(s12, s22)) {
                return true;
            }
        }
        return false;
    }
}
// @lc code=end

