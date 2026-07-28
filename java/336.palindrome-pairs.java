import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * @lc app=leetcode id=336 lang=java
 *
 * [336] Palindrome Pairs
 */

// @lc code=start
class Solution {
    class TrieNode {
        // 这里用26大小的数组代替也可以
        private Map<Character, TrieNode> children;
        private int wordIdx;
        private List<Integer> list;
        public TrieNode() {
            children = new HashMap<>();
            wordIdx = -1;
            list = new ArrayList<>();
        }
    }
    public List<List<Integer>> palindromePairs(String[] words) {
        TrieNode root = new TrieNode();
        for (int i = 0; i < words.length; i++) {
            // 把word存进Trie里，并保存word的idx
            addWord(root, words[i], i);
        }
        List<List<Integer>> resList = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            search(words, i, root, resList);
        }
        return resList;
    }

    private void addWord(TrieNode root, String word, int wordIdx) {
        TrieNode currNode = root;
        // 从后往前，反向构造Trie树
        for (int i = word.length() - 1; i >= 0; i--) {
            char eachChar = word.charAt(i);
            TrieNode charNode = currNode.children.getOrDefault(eachChar, new TrieNode());
            currNode.children.put(eachChar, charNode);
            // 如果从0到i的部分是回文串，则把当前字符串坐标保存到node里
            // 因为待会配对时，是从后往前配对
            // 如果都配对上，然后再加上前缀是回文串，说明这个也是可行的
            if (isPalindrome(word, 0, i)) {
                currNode.list.add(wordIdx);
            }
            currNode = charNode;
        }
        currNode.wordIdx = wordIdx;
        currNode.list.add(wordIdx);
    }

    private void search(String[] words, int i, TrieNode root, List<List<Integer>> resList) {
        String currSearchWord = words[i];
        TrieNode currNode = root;
        // 当前字符串，从前往后，和字典里的字符一个一个配对
        // 因为字典里的是反过来存的，所以正好能形成回文串
        for (int j = 0; j < currSearchWord.length(); j++) {
            // 如果这时候字典里的字符串已经配对好，然后当前字符串剩下部分也可以组成回文串
            // 则说明这两个字符串可以组成回文串
            // 把当前字符串放前面，然后把字典的字符串（更短）放后面
            if (currNode.wordIdx >= 0 && currNode.wordIdx != i &&
                    isPalindrome(currSearchWord, j, currSearchWord.length() - 1)) {
                resList.add(Arrays.asList(i, currNode.wordIdx));
            }
            char currChar = currSearchWord.charAt(j);
            if (!currNode.children.containsKey(currChar)) {
                return;
            }
            currNode = currNode.children.get(currChar);
        }
        // 到这个loop的话，说明当前字符能和字典里反向保存的字符配对上
        // 这时候，如果反向保存的字符前面也是回文串，则这个答案也可行
        // 把当前字符串放前面，然后把字典的字符串（更长）放后面
        for (int wordIdx: currNode.list) {
            if (wordIdx != i) {
                resList.add(Arrays.asList(i, wordIdx));
            }
        }
    }

    // 验证从i到j部分的word是不是回文符
    private boolean isPalindrome(String word, int i, int j) {
        while (i < j) {
            if (word.charAt(i) != word.charAt(j)) {
                return false;
            }
            i++;
            j--;
        }
        return true;
    }

}
// @lc code=end

