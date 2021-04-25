/*
 * @lc app=leetcode id=1047 lang=java
 *
 * [1047] Remove All Adjacent Duplicates In String
 */

// @lc code=start
class Solution {
    public String removeDuplicates(String S) {
        // 把这个当作是stack
        StringBuilder resBuilder = new StringBuilder();

        for (int i = 0; i < S.length(); i++) {
            // 如果当前字符和stack的最顶端一样，则把stack顶端的移除
            char currChar = S.charAt(i);
            if (resBuilder.length() > 0 && currChar == resBuilder.charAt(resBuilder.length() - 1)) {
                resBuilder.delete(resBuilder.length() - 1, resBuilder.length());
            }
            // 否则把当前的字符加入stack
            else {
                resBuilder.append(currChar);
            }
        }
        // 返回stack的最终结果
        return resBuilder.toString();
    }
    // 无脑stack解法
    // public String removeDuplicates(String S) {
    //     if (S.length() == 0) {
    //         return S;
    //     }
    //     for (int i = 0; i < S.length() - 1; i++) {
    //         if (S.charAt(i) == S.charAt(i + 1)) {
    //             return removeDuplicates(S.substring(0, i) + S.substring(i + 2, S.length()));
    //         }
    //     }
    //     return S;
    // }
}
// @lc code=end

