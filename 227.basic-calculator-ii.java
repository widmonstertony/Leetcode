/*
 * @lc app=leetcode id=227 lang=java
 *
 * [227] Basic Calculator II
 */

// @lc code=start
class Solution {
    public int calculate(String s) {
        Stack<Integer> numSt = new Stack<>();
        char lastOp = '+';
        int res = 0, currNum = 0;
        // 先遍历整个，把所有乘除的操作算完，加减的就直接放进stack
        for (int i = 0; i < s.length(); i++) {
            char currChar = s.charAt(i);
            if (currChar >= '0') {
                currNum = currNum * 10 + currChar - '0';
            }
            // 如果当前操作符是加减乘除，或者是最后一位的时候
            if (currChar < '0' && currChar != ' ' || i == s.length() - 1) {
                switch (lastOp) {
                    // 如果该数字之前的符号是加或减，那么把当前数字压入栈中
                    // 注意如果是减号，则加入当前数字的相反数
                    case '+' :
                        numSt.push(currNum);
                        break;
                    case '-' :
                        numSt.push(-currNum);
                        break;
                    // 如果之前的符号是乘或除
                    // 那么从栈顶取出一个数字和当前数字进行乘或除的运算，再把结果压入栈中
                    case '*' :
                        numSt.push(numSt.pop() * currNum);
                        break;
                    case '/' :
                        numSt.push(numSt.pop() / currNum); 
                        break;
                    default:   
                        break;
                }
                lastOp = currChar;
                currNum = 0;
            }
        }
        while (!numSt.isEmpty()) {
            res += numSt.pop();
        }
        return res;
    }
}
// @lc code=end

