import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * @lc app=leetcode id=734 lang=java
 *
 * [734] Sentence Similarity
 */

// @lc code=start
class Solution {
    public boolean areSentencesSimilar(String[] words1, String[] words2, List<List<String>> pairs) {
        if (words1.length != words2.length) {
            return false;
        }
        // HashTable用来保存词和它的近义词Set
        Map<String, Set<String>> hashTable = new HashMap<>();
        for (List<String> pair: pairs) {
            String firstStr = pair.get(0), secondStr = pair.get(1);
            if (!hashTable.containsKey(firstStr)) {
                hashTable.put(firstStr, new HashSet<String>());
            }
            Set<String> firstStrSet = hashTable.get(firstStr);
            firstStrSet.add(secondStr);
            if (!hashTable.containsKey(secondStr)) {
                hashTable.put(secondStr, new HashSet<String>());
            }
            Set<String> secondStrSet = hashTable.get(secondStr);
            secondStrSet.add(firstStr);
        }
        for (int i = 0; i < words1.length; i++) {
            String word1 = words1[i], word2 = words2[i];
            // 如果有在近义词的Set里找到另一个词，则是近义词
            if (hashTable.containsKey(word1)) {
                if (hashTable.get(word1).contains(word2)) {
                    continue;
                }
            }
            if (hashTable.containsKey(word2)) {
                if (hashTable.get(word2).contains(word1)) {
                    continue;
                }
            }
            // 如果相等，也是近义词
            if (word2.equals(word1)) {
                continue;
            }
            return false;
        }
        return true;
    }
}
// @lc code=end

