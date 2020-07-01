import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * @lc app=leetcode id=737 lang=java
 *
 * [737] Sentence Similarity II
 */

// @lc code=start
class Solution {
    public boolean areSentencesSimilarTwo(String[] words1, String[] words2, List<List<String>> pairs) {
        if (words1.length != words2.length) {
            return false;
        }
        Map<String, String> rootMap = new HashMap<>();
        for (List<String> pair: pairs) {
            String word1 = pair.get(0), word2 = pair.get(1);
            union(word1, word2, rootMap);
        }
        for (int i = 0; i < words1.length; i++) {
            String word1 = words1[i], word2 = words2[i];
            String rootOfWord1 = find(word1, rootMap), rootOfWord2 = find(word2, rootMap);
            if (!rootOfWord1.equals(rootOfWord2)) {
                return false;
            }
        }
        return true;
    }
    private void union(String word1, String word2, Map<String, String> rootMap) {
        String rootOfWord1 = find(word1, rootMap), rootOfWord2 = find(word2, rootMap);
        if (rootOfWord1.equals(rootOfWord2)) {
            return;
        }
        rootMap.put(rootOfWord1, rootOfWord2);
    }
    private String find(String word, Map<String, String> rootMap) {
        if (!rootMap.containsKey(word)) {
            return word;
        }
        String root = rootMap.get(word);
        if (rootMap.containsKey(root)) {
            root = find(root, rootMap);
            rootMap.put(word, root);
        }
        return root;
    }
}
// @lc code=end

