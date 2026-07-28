import java.util.HashSet;

/*
 * @lc app=leetcode id=859 lang=java
 *
 * [859] Buddy Strings
 */

// @lc code=start
class Solution {
    public boolean buddyStrings(String A, String B) {
        if (A.length() != B.length()) {
            return false;
        }
        char firstChar = ' ', secondChar = ' ';
        int diffCnt = 0;
        // 用set去记录A的每个char，确认有没有重复的
        int[] charSet = new int[26];
        boolean hasReplicate = false;
        for (int i = 0; i < A.length(); i++) {
            char charA = A.charAt(i), charB = B.charAt(i);
            charSet[charA - 'a']++;
            if (charSet[charA - 'a'] > 1) {
                hasReplicate = true;
            }
            if (charA != charB) {
                diffCnt++;
                if (diffCnt > 2) {
                    return false;
                }
                if (diffCnt == 1) {
                    firstChar = charA;
                    secondChar = charB;
                }
                if (diffCnt == 2) {
                    if (firstChar != charB || secondChar != charA) {
                        return false;
                    }
                }
            }
        }
        // 如果两个string一样
        if (diffCnt == 0) {
            if (A.length() < 2) {
                return false;
            }
            // 如果中间没有重复的字母，那么一定没办法swap
            // 否则一定可以swap
            return hasReplicate;
        }
        // 确保只有两位不一样
        return diffCnt == 2;
    }
}
// @lc code=end

