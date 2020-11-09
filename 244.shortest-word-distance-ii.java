/*
 * @lc app=leetcode id=244 lang=java
 *
 * [244] Shortest Word Distance II
 */

// @lc code=start
class WordDistance {
    Map<String, List<Integer>> wordsMap;
    public WordDistance(String[] words) {
        wordsMap = new HashMap<>();
        for (int i = 0; i < words.length; i++) {
            String currWord = words[i];
            List<Integer> wordIdxList = wordsMap.getOrDefault(currWord, new ArrayList<>());
            wordIdxList.add(i);
            wordsMap.put(currWord, wordIdxList);
        }
    }
    
    public int shortest(String word1, String word2) {
        List<Integer> word1List = wordsMap.getOrDefault(word1, new ArrayList<>());
        List<Integer> word2List = wordsMap.getOrDefault(word2, new ArrayList<>());
        if (word1List.isEmpty() || word2List.isEmpty()) {
            return -1;
        }
        int res = Integer.MAX_VALUE;
        int i = 0, j = 0;
        while (i < word1List.size() && j < word2List.size()) {
            int word1Idx = word1List.get(i), word2Idx = word2List.get(j);
            res = Math.min(res, Math.abs(word1Idx - word2Idx));
            // 比较位置数组中的数字，将较小的一个的指针向后移动一位
            if (word1Idx < word2Idx) {
                i++;
            }
            else {
                j++;
            }
        }
        return res;
    }
}

/**
 * Your WordDistance object will be instantiated and called as such:
 * WordDistance obj = new WordDistance(words);
 * int param_1 = obj.shortest(word1,word2);
 */
// @lc code=end

