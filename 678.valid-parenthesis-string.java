import java.util.Stack;

/*
 * @lc app=leetcode id=678 lang=java
 *
 * [678] Valid Parenthesis String
 */

// @lc code=start
class Solution {
    public boolean checkValidString(String s) {
        // 两个stack，分别存放左括号和星号的位置
        Stack<Integer> leftSt = new Stack<>();
        Stack<Integer> starSt = new Stack<>();
        for (int i = 0; i < s.length(); i++) {
            char currChar = s.charAt(i);
            if (currChar == '*') {
                starSt.push(i);
            }
            else if (currChar == '(') {
                leftSt.push(i);
            }
            // 如果是右括号，尝试先用左括号去配对，再用星号去配对
            else {
                if (leftSt.isEmpty() && starSt.isEmpty()) {
                    return false;
                }
                if (!leftSt.isEmpty()) {
                    leftSt.pop();
                }
                else {
                    starSt.pop();
                }
            }
        }
        // left中多余的左括号用星号来抵消
        while (!leftSt.isEmpty() && !starSt.isEmpty()) {
            // 如果left的栈顶左括号的位置在star的栈顶星号的右边
            // 那么就组成了 *( 模式，直接返回false
            if (leftSt.pop() > starSt.pop()) {
                return false;
            }
        }
        // 确保没有多余的左括号
        return leftSt.isEmpty();
    }
    // 暴力解
    // public boolean checkValidString(String s) {
    //     return helper(s, 0, 0);
    // }
    // private boolean helper(String s, int startIdx, int leftCnt) {
    //     if (leftCnt < 0) {
    //         return false;
    //     }
    //     for (int i = startIdx; i < s.length(); i++) {
    //         char currChar = s.charAt(i);
    //         if (currChar == '(') {
    //             leftCnt++;
    //         }
    //         else if (currChar == ')') {
    //             leftCnt--;
    //             if (leftCnt < 0) {
    //                 return false;
    //             }
    //         }
    //         // *的情况，三种情况都试一遍
    //         else {
    //             return helper(s, i + 1, leftCnt - 1) || helper(s, i + 1, leftCnt) || helper(s, i + 1, leftCnt + 1);
    //         }
    //     }
    //     return leftCnt == 0;
    // }
}
// @lc code=end

