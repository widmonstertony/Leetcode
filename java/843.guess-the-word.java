/*
 * @lc app=leetcode id=843 lang=java
 *
 * [843] Guess the Word
 */

// @lc code=start
/**
 * // This is the Master's API interface.
 * // You should not implement it, or speculate about its implementation
 * interface Master {
 *     public int guess(String word) {}
 * }
 */
class Solution {
    public void findSecretWord(String[] wordlist, Master master) {
        LinkedList<String> wordLinkedList = new LinkedList();
        for (String word: wordlist) {
            wordLinkedList.add(word);
        }
        Collections.shuffle(wordLinkedList);

        while (!wordLinkedList.isEmpty()) {
            String currStr = wordLinkedList.pop();
            int numMatches = master.guess(currStr);
            if (numMatches >= 0) {
                Iterator<String> newStrIt = wordLinkedList.iterator();
                while (newStrIt.hasNext()) {
                    String nextStr = newStrIt.next();
                    // 因为正确答案和当前字符串的相同字符数等于numMatches
                    // 把和当前字符串相同字符数不等于numMatches的都删掉，因为他们肯定不是正确答案
                    if (commonChars(currStr, nextStr) != numMatches || currStr.equals(nextStr)) {
                        newStrIt.remove();
                    }
                }
            }
        }
        return;
    }
    private int commonChars(String currStr, String nextStr) {
        if (currStr.length() != nextStr.length()) {
            return -1;
        }
        int res = 0;
        for (int i = 0; i < currStr.length(); i++) {
            if (currStr.charAt(i) == nextStr.charAt(i)) {
                res++;
            }
        }
        return res;
    }
}
// @lc code=end

