/*
 * @lc app=leetcode id=22 lang=java
 *
 * [22] Generate Parentheses
 */

// @lc code=start
class Solution {
    public List<String> generateParenthesis(int n) {
        List<String> resList = new ArrayList<>();
        generateParenthesisHelper(0, 0, resList, "", n);
        return resList;
    }
    private void generateParenthesisHelper(int leftCnt, int rightCnt, List<String> resList, String curStr, int n) {
        if (rightCnt > n || leftCnt > n) {
            return;
        }
        // 如果右括号多余左括号，就不valid了
        if (rightCnt > leftCnt) {
            return;
        }
        if (rightCnt == n && leftCnt == n) {
            resList.add(curStr);
            return;
        }
        generateParenthesisHelper(leftCnt + 1, rightCnt, resList, curStr + '(', n);
        generateParenthesisHelper(leftCnt, rightCnt + 1, resList, curStr + ')', n);
    }
}
// @lc code=end

