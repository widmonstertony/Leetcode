/*
 * @lc app=leetcode id=551 lang=java
 *
 * [551] Student Attendance Record I
 */

// @lc code=start
class Solution {
    public boolean checkRecord(String s) {
        int aCnt = 0, lCnt = 0;
        for (int i = 0; i < s.length(); i++) {
            char currChar = s.charAt(i);
            if (currChar == 'A') {
                aCnt++;
                if (aCnt > 1) {
                    return false;
                }
            }
            if (currChar == 'L') {
                if (i > 0 && s.charAt(i - 1) == 'L') {
                    lCnt++;
                }
                else {
                    lCnt = 1;
                }
                if (lCnt > 2) {
                    return false;
                }
            }
        }
        return true;
    }
}
// @lc code=end

