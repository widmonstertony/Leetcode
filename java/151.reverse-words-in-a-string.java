/*
 * @lc app=leetcode id=151 lang=java
 *
 * [151] Reverse Words in a String
 */

// @lc code=start
class Solution {
    public String reverseWords(String s) {
        StringBuilder newStrBuilder = new StringBuilder();

        for (int i = s.length() - 1; i >= 0; i--) {
            StringBuilder currWordBuilder = new StringBuilder();
            // 首先一直读不是空格的字母直到空格
            while (i >= 0 && s.charAt(i) != ' ') {
                currWordBuilder.insert(0, s.charAt(i));
                i--;
            }

            // 把刚刚读的单词加到答案里去
            newStrBuilder.append(currWordBuilder.toString());
            // 如果答案不为空，则在单词后面加个空格
            if (newStrBuilder.length() != 0) {
                newStrBuilder.append(" ");
            }

            // 一直移动i到下一个不会是空格的地方，确保下一次循环开始一定不是空格
            while (i > 0 && s.charAt(i - 1) == ' ') {
                i--;
            }

        }
        // 如果答案不为空，则删除最后单词后面跟着的那个空格
        if (newStrBuilder.length() > 0) {
            newStrBuilder.deleteCharAt(newStrBuilder.length() - 1);
        }
        return newStrBuilder.toString();
    }
}
// @lc code=end

