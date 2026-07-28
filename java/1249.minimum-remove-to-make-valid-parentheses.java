/*
 * @lc app=leetcode id=1249 lang=java
 *
 * [1249] Minimum Remove to Make Valid Parentheses
 */

// @lc code=start
class Solution {
    public String minRemoveToMakeValid(String s) {
        if (s == null || s.length() == 0) {
            return s;
        }

        StringBuilder sb = new StringBuilder();
        int leftParCnt = 0;
        for (char currChar : s.toCharArray()) {
            // 如果是左括号，左括号记数加一
            if (currChar == '(') {
                leftParCnt++;
            }
            else if (currChar == ')') {
                // 如果当前是右括号但是没有左括号去抵消，则需要忽略这个右括号
                if (leftParCnt == 0) {
                    continue;
                }

                // 否则左括号记数减一，代表合并了
                leftParCnt--;
            }

            // 把当前的char放进sb里
            sb.append(currChar);
        }
        // 上面循环的结果，一定是左括号不少于右括号
        StringBuilder res = new StringBuilder();
        for (int i = sb.length() - 1; i >= 0; i--) {
            char currChar = sb.charAt(i);
            // 如果当前的左括号是多余的没法合并的，因为可以合并的都在上面那个循环里合并掉了
            // 则跳过当前的左括号
            if (currChar == '(' && leftParCnt-- > 0) {
                continue;
            }
            res.append(currChar);
        }

        return res.reverse().toString();
    }
}
// @lc code=end

