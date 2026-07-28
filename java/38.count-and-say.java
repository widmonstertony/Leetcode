/*
 * @lc app=leetcode id=38 lang=java
 *
 * [38] Count and Say
 */

// @lc code=start
class Solution {
    public String countAndSay(int n) {
        if (n == 1) {
            return "1";
        }
        String numStr = countAndSay(n - 1);
        StringBuilder resBuilder = new StringBuilder();
        for (int i = 0; i < numStr.length(); i++) {
            char currNum = numStr.charAt(i);
            int currCnt = 1;
            while (i + 1 < numStr.length() && numStr.charAt(i + 1) == currNum) {
                currCnt++;
                i++;
            }
            resBuilder.append(currCnt);
            resBuilder.append(currNum);
        }
        return resBuilder.toString();
    }
}
// @lc code=end

