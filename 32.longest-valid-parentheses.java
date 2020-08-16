/*
 * @lc app=leetcode id=32 lang=java
 *
 * [32] Longest Valid Parentheses
 */

// @lc code=start
class Solution {
    public int longestValidParentheses(String s) {
        Stack<Integer> parenthesesSt = new Stack<>();
        // start 记下当前都可行的字符的开始坐标
        int maxRes = 0, startIdx = 0;
        for (int i = 0; i < s.length(); i++) {
            char currChar = s.charAt(i);
            // 右括号，需要找左括号
            if (currChar == ')') {
                // 没有左括号找，说明当前字符不符合，start变成下一个字符坐标
                if (parenthesesSt.isEmpty()) {
                    startIdx = i + 1;
                }
                // 如果有左括号对应
                else {
                    parenthesesSt.pop();
                    // 如果这时是空栈,说明从start到当前都符合条件
                    if (parenthesesSt.isEmpty()) {
                        maxRes = Math.max(maxRes, i - startIdx + 1);
                    }
                    // 否则就是上一个还没被匹配的左括号的后一位到当前字符的长度
                    else {
                        maxRes = Math.max(maxRes, i - parenthesesSt.peek());
                    }
                }
            }
            // 当前是左括号，入栈
            else if (currChar == '(') {
                parenthesesSt.push(i);
            }
        }
        return maxRes;
    }
}
// @lc code=end

