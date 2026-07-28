/*
 * @lc app=leetcode id=224 lang=java
 *
 * [224] Basic Calculator
 */

// @lc code=start
class Solution {
    public int calculate(String s) {
        int currNum = 0;
        char lastOp = '+';
        int res = 0;
        for (int i = 0; i < s.length(); i++) {
            char currChar = s.charAt(i);
            if (currChar == '(') {
                int cnt = 0, start = i;
                for (; i < s.length(); i++) {
                    char nextChar = s.charAt(i);
                    if (nextChar == '(') {
                        cnt++;
                    }
                    else if (nextChar == ')') {
                        cnt--;
                    }
                    if (cnt == 0) {
                        break;
                    }
                }
                currNum = calculate(s.substring(start + 1, i));
            }
            if (currChar >= '0') {
                currNum = currNum * 10 + currChar - '0';
            }
            if (currChar < '0' && currChar != ' ' || i == s.length() - 1) {
                switch (lastOp) {
                    case '+':
                        res += currNum;
                        break;
                    case '-':
                        res -= currNum;
                        break;
                    default:
                        break;
                }
                lastOp = currChar;
                currNum = 0;
            }
        }
        return res;
    }
}
// @lc code=end

