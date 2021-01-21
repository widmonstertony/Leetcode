/*
 * @lc app=leetcode id=6 lang=java
 *
 * [6] ZigZag Conversion
 */

// @lc code=start
class Solution {
    public String convert(String s, int numRows) {
        if (numRows <= 1) {
            return s;
        }
        String[] resArr = new String[numRows];
        for (int i = 0; i < numRows; i++) {
            resArr[i] = "";
        }
        int currRow = 0;
        boolean isGoingUp = false;
        for (char currChar : s.toCharArray()) {
            resArr[currRow] += currChar;
            if (currRow == numRows - 1) {
                isGoingUp = true;
            }
            else if (currRow == 0) {
                isGoingUp = false;
            }
            if (isGoingUp == true) {
                currRow--;
            }
            else {
                currRow++;
            }
        }
        StringBuilder resBuilder = new StringBuilder();
        for (int i = 0; i < numRows; i++) {
            resBuilder.append(resArr[i]);
        }
        return resBuilder.toString();
    }
}
// @lc code=end

