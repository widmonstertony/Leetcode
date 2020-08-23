import java.util.ArrayList;

/*
 * @lc app=leetcode id=140 lang=java
 *
 * [140] Word Break II
 */

// @lc code=start
class Solution {
    public List<String> wordBreak(String s, List<String> wordDict) {
        Map<String, List<String>> dp = new HashMap<>();
        Set<String> wordSet = new HashSet<>();
        for (String word: wordDict) {
            wordSet.add(word);
        }
        return wordBreakHelper(s, wordSet, dp);
    }
    private List<String> wordBreakHelper(String s, Set<String> wordSet, Map<String, List<String>> dp) {
        // 如果当前string已经遍历过，直接返回之前的结果
        if (dp.containsKey(s)) {
            return dp.get(s);
        }
        List<String> resList = new ArrayList<>();
        // 如果当前字符是空，表明结束了
        // 放一个空字符结束
        if (s.isEmpty()) {
            resList.add("");
            return resList;
        }
        // 把s分成0 - i 和 i - s结尾
        for (int i = 0; i <= s.length(); i++) {
            String frontStr = s.substring(0, i);
            String laterStr = s.substring(i);
            // 如果前半部分在词典里，则找出后半部分的所有组合
            if (wordSet.contains(frontStr)) {
                List<String> wordList = wordBreakHelper(laterStr, wordSet, dp);
                // 把后半部分所有组合的结果和当前词语加起来
                for (String word : wordList) {
                    // 一定要在s是空的时候放空字符
                    // 这样才能在结尾是空的时候把当前前半部分加进去
                    if (word.isEmpty()) {
                        resList.add(frontStr);
                    }
                    else {
                        resList.add(frontStr + ' ' + word);
                    }
                }
            }
            
        }
        dp.put(s, resList);
        return resList;
    }
}
// @lc code=end

  