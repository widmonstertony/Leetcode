/*
 * @lc app=leetcode id=211 lang=java
 *
 * [211] Design Add and Search Words Data Structure
 */

// @lc code=start
class WordDictionary {
    class TrieNode {
        boolean isWord;
        TrieNode[] children;
        TrieNode() {
            isWord = false;
            children = new TrieNode[26];
        }
    }

    private TrieNode root;
    /** Initialize your data structure here. */
    public WordDictionary() {
        root = new TrieNode();
    }
    
    public void addWord(String word) {
        TrieNode currNode = root;
        for (char currChar : word.toCharArray()) {
            if (currNode.children[currChar - 'a'] == null) {
                currNode.children[currChar - 'a'] = new TrieNode();
            }
            currNode = currNode.children[currChar - 'a'];
        }
        currNode.isWord = true;
    }
    
    public boolean search(String word) {
        return search(word, root);
    }
    private boolean search(String word, TrieNode currNode) {
        for (int i = 0; i < word.length(); i++) {
            char currChar = word.charAt(i);
            if (currChar == '.') {
                for (TrieNode eachChild : currNode.children) {
                    if (eachChild != null) {
                        // 用dfs查找所有不为空的char, 如果有可以匹配的就返回true
                        if (search(word.substring(i + 1), eachChild)) {
                            return true;
                        }
                    }
                }
                return false;
            }
            else {
                if (currNode.children[currChar - 'a'] == null) {
                    currNode.children[currChar - 'a'] = new TrieNode();
                }
                currNode = currNode.children[currChar - 'a'];
            }
        }
        return currNode.isWord;
    }
}

/**
 * Your WordDictionary object will be instantiated and called as such:
 * WordDictionary obj = new WordDictionary();
 * obj.addWord(word);
 * boolean param_2 = obj.search(word);
 */
// @lc code=end

