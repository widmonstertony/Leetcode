import java.util.ArrayList;
import java.util.List;

/*
 * @lc app=leetcode id=249 lang=java
 *
 * [249] Group Shifted Strings
 */

// @lc code=start
class Solution {
    public List<List<String>> groupStrings(String[] strings) {
        List<List<String>> resList = new ArrayList<>();
        // 把遍历过的标记一下
        boolean[] marked = new boolean[strings.length];
        int cnt = strings.length;
        while (cnt > 0) {
            int firstIdx = -1;
            // 从第一个未标记的字符串开始
            for (int i = 0; i < strings.length; i++) {
                if (!marked[i]) {
                    firstIdx = i;
                    break;
                }
            }
            marked[firstIdx] = true;
            cnt--;
            String leadStr = strings[firstIdx];
            List<String> currGroup = new ArrayList<>();
            currGroup.add(leadStr);
            // 一直尝试shift剩下的每个字符串，如果有shift后相同的字符串，则加入当前list
            for (int i = firstIdx + 1; i < strings.length; i++) {
                String currStr = strings[i];
                if (!marked[i] && currStr.length() == leadStr.length()) {
                    if (shiftRightEqual(currStr, leadStr)) {
                        marked[i] = true;
                        cnt--;
                        currGroup.add(currStr);
                    }
                }
            }
            resList.add(currGroup);
        }
        return resList;
    }
    private boolean shiftRightEqual(String original, String leadStr) {
        int shiftDigit = leadStr.charAt(0) - original.charAt(0);
        if (shiftDigit < 0) {
            shiftDigit += 26;
        }
        // shift每一个字符并对比
        for (int i = 0; i < original.length(); i++) {
            char eachChar = original.charAt(i);
            if ('z' - eachChar < shiftDigit) {
                if ('a' + shiftDigit - ('z' - eachChar) - 1 - leadStr.charAt(i) != 0) {
                    return false;
                }
            }
            else {
                if (eachChar + shiftDigit - leadStr.charAt(i) != 0) {
                    return false;
                }
            }
        }
        return true;
    }
}
// @lc code=end

