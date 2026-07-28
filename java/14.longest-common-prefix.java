/*
 * @lc app=leetcode id=14 lang=java
 *
 * [14] Longest Common Prefix
 */

// @lc code=start
class Solution {
    public String longestCommonPrefix(String[] strs) {
        StringBuilder resBuilder = new StringBuilder();
        for (int i = 0; i < strs.length; i++) {
            String currStr = strs[i];
            if (resBuilder.length() == 0) {
                if (i != 0) {
                    return "";
                }
                resBuilder.append(currStr);
            }
            else {
                for (int j = 0; j < resBuilder.length(); j++) {
                    if (j >= currStr.length() || currStr.charAt(j) != resBuilder.charAt(j)) {
                        resBuilder.delete(j, resBuilder.length());
                        break;
                    }
                }
            }
        }
        return resBuilder.toString();
    }
}
// @lc code=end

