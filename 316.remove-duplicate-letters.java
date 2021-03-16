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
    // public String removeDuplicateLetters(String s) {
    //     if (s.length() == 0) {
    //         return s;
    //     }
    //     int firstPos = 0;
    //     int[] cntMap = new int[26];
    //     for (char eachChar : s.toCharArray()) {
    //         cntMap[eachChar - 'a']++;
    //     }
    //     for (int i = 0; i < s.length(); i++) {
    //         char currChar = s.charAt(i);
    //         // 找到第一个最左边的字符，必须要确保
    //         // 从它开始的后缀字符串都至少有整个字符串里的每一个字符出现过一次
    //         // 并且它是最小的那个字符
    //         if (currChar < s.charAt(firstPos)) {
    //             firstPos = i;
    //         }
    //         cntMap[currChar - 'a']--;
    //         // 只要有一个字符是出现最后一次就结束循环
    //         // 因为我们知道当前位置i之后就不符合条件了
    //         // 这时候能确保firstPos一定是从0到i的最小的字符
    //         // 并且从firstPos开始的后缀字符串都至少有整个字符串里的每一个字符出现过一次
    //         if (cntMap[currChar - 'a'] == 0) {
    //             break;
    //         }
    //     }
    //     if (firstPos + 1 >= s.length()) {
    //         return s.charAt(firstPos) + "";
    //     }
    //     // 把之后出现的firstPos的字符全部删除
    //     String newStr = s.substring(firstPos + 1, s.length()).replaceAll(s.charAt(firstPos) + "", "");
    //     // 迭代找下一个最小的字符
    //     return s.charAt(firstPos) + removeDuplicateLetters(newStr);
    // }
}
// @lc code=end

