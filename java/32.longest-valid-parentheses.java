/*
 * @lc app=leetcode id=32 lang=java
 *
 * [32] Longest Valid Parentheses
 */

// @lc code=start
class Solution {
    // dp 解法
    public int longestValidParentheses(String s) {
        // dp[i] 表示以s[i - 1]结尾的最长有效括号长度
        int[] dp = new int[s.length() + 1];
        int maxRes = 0;
        for (int i = 1; i <= s.length(); i++) {
            // 计算出第一个右括号的位置，通过 i-3-dp[i-1] 来获得
            // 由于这里定义的 dp[i] 对应的是字符 s[i-1]，所以需要再加1
            int j = i - 2 - dp[i - 1];
            // 若当前字符 s[i-1] 是左括号，或者j小于0（说明没有对应的左括号）
            // 或者 s[j] 是右括号
            // 将 dp[i] 重置为0
            if (s.charAt(i - 1) == '(' || j < 0 || s.charAt(j) == ')') {
                dp[i] = 0;
            } 
            // 否则就用 dp[i-1] + 2 + dp[j] 来更新 dp[i]
            else {
                dp[i] = dp[i - 1] + 2 + dp[j];
                maxRes = Math.max(maxRes, dp[i]);
            }
        }
        return maxRes;
    }
    // Two pointers
    // public int longestValidParentheses(String s) {
    //     // left 和 right，分别用来记录到当前位置时左括号和右括号的出现次数
    //     int leftCnt = 0, rightCnt = 0;
    //     int maxRes = 0;
    //     for (char currChar : s.toCharArray()) {
    //         if (currChar == '(') {
    //             leftCnt++;
    //         }
    //         else {
    //             rightCnt++;
    //         }
    //         // 最长有效的括号的子串，一定是左括号等于右括号的情况
    //         // 此时就可以更新结果 res
    //         if (leftCnt == rightCnt) {
    //             maxRes = Math.max(maxRes, leftCnt + rightCnt);
    //         }
    //         // 一旦右括号数量超过左括号数量了
    //         // 说明当前位置不能组成合法括号子串，left 和 right 重置为0
    //         else if (rightCnt > leftCnt) {
    //             leftCnt = 0;
    //             rightCnt = 0;
    //         }
    //     }
    //     leftCnt = 0;
    //     rightCnt = 0;
    //     // 反向遍历一遍
    //     for (int i = s.length() - 1; i >= 0; i--) {
    //         char currChar = s.charAt(i);
    //         if (currChar == '(') {
    //             leftCnt++;
    //         }
    //         else {
    //             rightCnt++;
    //         }
    //         if (leftCnt == rightCnt) {
    //             maxRes = Math.max(maxRes, leftCnt + rightCnt);
    //         }
    //         // 稍有不同的是此时若 left 大于 right 了，则重置0
    //         else if (rightCnt < leftCnt) {
    //             leftCnt = 0;
    //             rightCnt = 0;
    //         }
    //     }
    //     return maxRes;
    // }
    // Stack方法
    // public int longestValidParentheses(String s) {
    //     Stack<Integer> parenthesesSt = new Stack<>();
    //     // start 记下当前都可行的字符的开始坐标
    //     int maxRes = 0, startIdx = 0;
    //     for (int i = 0; i < s.length(); i++) {
    //         char currChar = s.charAt(i);
    //         // 右括号，需要找左括号
    //         if (currChar == ')') {
    //             // 没有左括号找，说明当前字符不符合，start变成下一个字符坐标
    //             if (parenthesesSt.isEmpty()) {
    //                 startIdx = i + 1;
    //             }
    //             // 如果有左括号对应
    //             else {
    //                 parenthesesSt.pop();
    //                 // 如果这时是空栈,说明从start到当前都符合条件
    //                 if (parenthesesSt.isEmpty()) {
    //                     maxRes = Math.max(maxRes, i - startIdx + 1);
    //                 }
    //                 // 否则就是上一个还没被匹配的左括号的后一位到当前字符的长度
    //                 else {
    //                     maxRes = Math.max(maxRes, i - parenthesesSt.peek());
    //                 }
    //             }
    //         }
    //         // 当前是左括号，入栈
    //         else if (currChar == '(') {
    //             parenthesesSt.push(i);
    //         }
    //     }
    //     return maxRes;
    // }
}
// @lc code=end

