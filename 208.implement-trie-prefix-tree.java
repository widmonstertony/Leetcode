/*
 * @lc app=leetcode id=208 lang=java
 *
 * [208] Implement Trie (Prefix Tree)
 */

// @lc code=start
class Trie {
    class TrieNode {
        // 标志符用来记录到当前位置为止是否为一个词
        private boolean isWord;
        // 字母的字典树每个节点要定义一个大小为 26 的子节点指针数组
        private TrieNode[] children;
        public TrieNode() {
            children = new TrieNode[26];
        }
        public TrieNode getChildNodeOrCreate(char currChar, boolean createIfNotExist) {
            if (children[currChar - 'a'] == null && createIfNotExist) {
                children[currChar - 'a'] = new TrieNode();
            }
            return children[currChar - 'a'];
        }
        public void setIsWord(boolean isWord) {
            this.isWord = isWord;
        }
        public boolean getIsWord() {
            return this.isWord;
        }
    }
    /** Initialize your data structure here. */
    
    private TrieNode root;
    public Trie() {
        root = new TrieNode();
    }
    
    /** Inserts a word into the trie. */
    public void insert(String word) {
        // 对于要插入的字符串的每一个字符算出其的位置
        // 然后找是否存在这个子节点，若不存在则新建一个，然后再查找下一个
        TrieNode currNode = root;
        for (char currChar : word.toCharArray()) {
            currNode = currNode.getChildNodeOrCreate(currChar, true);
        }
        // 最后把当前位置记录为一个词
        currNode.setIsWord(true);
    }
    
    /** Returns if the word is in the trie. */
    public boolean search(String word) {
        TrieNode currNode = root;
        for (char currChar : word.toCharArray()) {
            currNode = currNode.getChildNodeOrCreate(currChar, false);
            // 如果不存在node说明没有inset过到这个地方
            if (currNode == null) {
                return false;
            }
        }
        // 最后确认当前位置是否为一个词
        return currNode.getIsWord();
    }
    
    /** Returns if there is any word in the trie that starts with the given prefix. */
    public boolean startsWith(String prefix) {
        TrieNode currNode = root;
        for (char currChar : prefix.toCharArray()) {
            currNode = currNode.getChildNodeOrCreate(currChar, false);
            // 如果不存在node说明没有inset过到这个地方
            if (currNode == null) {
                return false;
            }
        }
        // 如果到了当前位置，说明有这个prefix
        return true;
    }
}

/**
 * Your Trie object will be instantiated and called as such:
 * Trie obj = new Trie();
 * obj.insert(word);
 * boolean param_2 = obj.search(word);
 * boolean param_3 = obj.startsWith(prefix);
 */
// @lc code=end

