/*
 * @lc app=leetcode id=772 lang=java
 *
 * [772] Basic Calculator III
 */

// @lc code=start
class Solution {
    public int calculate(String s) {
        Stack<Integer> numSt = new Stack<>();
        int res = 0, currNum = 0;
        char lastOp = '+';
        // 先遍历整个，把所有乘除的操作算完，加减的就直接放进stack
        for (int i = 0; i < s.length(); i++) {
            char currChar = s.charAt(i);
            // 和II不一样的是，因为有括号
            // 用一个变量 cnt，遇到左括号自增1，遇到右括号自减1
            // 当cnt为0的时候，说明括号正好完全匹配
            if (currChar == '(') {
                int start = i, cnt = 0;
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
                // 根据左右括号的位置提取出中间的子字符串调用递归函数，返回值赋给currNum
                currNum = calculate(s.substring(start + 1, i));
            }
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

