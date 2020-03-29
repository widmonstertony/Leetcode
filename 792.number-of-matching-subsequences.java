import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * @lc app=leetcode id=792 lang=java
 *
 * [792] Number of Matching Subsequences
 */

// @lc code=start
class Solution {
    public int numMatchingSubseq(String S, String[] words) {
        // 把所有的words按照开头字母各分成一列
        Map<Character, List<String>> wordsMap = new HashMap<Character, List<String>>();
        int ans = 0;
        for (String word: words) {
            if (word.length() > 0) {
                char startChar = word.charAt(0);
                List<String> currCharList;
                if (wordsMap.containsKey(startChar)) {
                    currCharList = wordsMap.get(startChar);
                }
                else {
                    currCharList = new ArrayList<>();
                    wordsMap.put(startChar, currCharList);
                }
                currCharList.add(word);
            }
        }
        // 然后一遍走完S，在走的过程中，把每个遇到的字母开头的那个list里的字母都减1
        // 如果减到0，说明这个word是subsequence
        // ans加一
        for (int i = 0; i < S.length(); i++) {
            char currChar = S.charAt(i);
            if (wordsMap.containsKey(currChar)) {
                List<String> charList= wordsMap.get(currChar);
                List<String> newCharList = new ArrayList<>();
                for (String word : charList) {
                    if (word.length() <= 1) {
                        ans++;
                    }
                    else {
                        // 取出下一个开头的char，如果和当前一样，则加回当前的List
                        char newChar = word.charAt(1);
                        String newString = word.substring(1);
                        if (newChar == currChar) {
                            newCharList.add(newString);
                        }
                        // 如果不同，把新的去头的String加到属于它的List里
                        else {
                            List<String> currCharList;
                            if (wordsMap.containsKey(newChar)) {
                                currCharList = wordsMap.get(newChar);
                            }
                            else {
                                currCharList = new ArrayList<>();
                                wordsMap.put(newChar, currCharList);
                            }
                            currCharList.add(newString);
                        }
                    }
                }
                wordsMap.remove(currChar);
                if (newCharList.size() > 0) {
                    wordsMap.put(currChar, newCharList);
                }
            }
        }
        return ans;
    }
}
// @lc code=end

