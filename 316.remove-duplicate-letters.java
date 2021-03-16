/*
 * @lc app=leetcode id=316 lang=java
 *
 * [316] Remove Duplicate Letters
 */

// @lc code=start
class Solution {
    public String removeDuplicateLetters(String s) {
        StringBuilder resBuilder = new StringBuilder();
        // 记录每个字符出现的次数
        int[] charCountMap = new int[26];
        boolean[] visited = new boolean[26];
        for (char currChar : s.toCharArray()) {
            charCountMap[currChar - 'a']++;
        }
        for (int i = 0; i < s.length(); i++) {
            char currChar = s.charAt(i);
            // 每遍历过一次，次数减一
            charCountMap[currChar - 'a']--;
            // 如果visit过，说明当前字符的位置已经安排好了，跳过
            if (visited[currChar - 'a']) {
                continue;
            }
            if (resBuilder.length() > 0) {
                char endResChar = resBuilder.charAt(resBuilder.length() - 1);
                // 只要当前答案里最后加入的那个字符比当前字符大，并且在之后还会再出现
                while (currChar < endResChar && charCountMap[endResChar - 'a'] > 0) {
                    // 就把之前的字符移除，并且标记为未访问
                    resBuilder.deleteCharAt(resBuilder.length() - 1);
                    visited[endResChar - 'a'] = false;
                    if (resBuilder.length() <= 0) {
                        break;
                    }
                    endResChar = resBuilder.charAt(resBuilder.length() - 1);
                }
            }
            // 确保当前字符前面的字符都不能再优化了，把当前字符加到末尾，并设为visited
            resBuilder.append(currChar);
            visited[currChar - 'a'] = true;
            
        }
        return resBuilder.toString();
    }
}
// @lc code=end

