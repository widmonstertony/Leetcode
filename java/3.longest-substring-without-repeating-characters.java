/*
 * @lc app=leetcode id=3 lang=java
 *
 * [3] Longest Substring Without Repeating Characters
 */

// @lc code=start
class Solution {
    public int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> charMap = new HashMap<Character, Integer>();
        int maxLength = 0;
        int left = 0;
        for (int i = 0; i < s.length(); i++) {
            char currChar = s.charAt(i);
            // 如果遇到有重复的就跳到左边的下一个继续
            if (charMap.containsKey(currChar) && charMap.get(currChar) >= left) {
                left = charMap.get(currChar) + 1;
            }
            charMap.put(currChar, i);
            maxLength = Math.max(maxLength, i - left + 1);
        }
        return maxLength;

    }
    // 自己写的，用hashmap记录位置，如果有重复就删除之前的
    // public int lengthOfLongestSubstring(String s) {
    //     Map<Character, Integer> charMap = new HashMap<Character, Integer>();
    //     int maxLength = 0;
    //     int left = 0;
    //     for (int i = 0; i < s.length(); i++) {
    //         char currChar = s.charAt(i);
    //         if (charMap.containsKey(currChar)) {
    //             int repeatEndIdx = charMap.get(currChar);
    //             for (int j = left; j <= repeatEndIdx; j++) {
    //                 charMap.remove(s.charAt(j));
    //             }
    //             left = repeatEndIdx + 1;
    //         }
    //         else {
    //             maxLength = Math.max(maxLength, i - left + 1);
    //         }
    //         charMap.put(currChar, i);
    //     }
    //     return maxLength;
    // }

}
// @lc code=end

