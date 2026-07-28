/*
 * @lc app=leetcode id=20 lang=java
 *
 * [20] Valid Parentheses
 */

// @lc code=start
class Solution {
    public boolean isValid(String s) {
        Stack<Character> charSt = new Stack();
        for (char eachChar : s.toCharArray()) {
            switch (eachChar) {
                case ')':
                    if (charSt.isEmpty() || charSt.pop() != '(') {
                        return false;
                    }
                    break;
                case '}':
                    if (charSt.isEmpty() || charSt.pop() != '{') {
                        return false;
                    }
                    break;
                case ']':
                    if (charSt.isEmpty() || charSt.pop() != '[') {
                        return false;
                    }
                    break;
                default:
                    charSt.push(eachChar);
            }
        }
        return charSt.isEmpty();
    }
}
// @lc code=end

