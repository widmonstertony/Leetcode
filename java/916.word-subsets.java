/*
 * @lc app=leetcode id=916 lang=java
 *
 * [916] Word Subsets
 */

// @lc code=start
class Solution {
    public List<String> wordSubsets(String[] A, String[] B) {
        // 用26的char Array可以更快, 因为题目说了只有小写
        Map<Character, Integer> charBCntMap = new HashMap<>();
        List<String> resList = new ArrayList<>();
        // 统计出集合B中某个单词中出现的最大次数
        for (String b : B) {
            Map<Character, Integer> tmpCntMap = new HashMap<>();
            for (char eachCharinB : b.toCharArray()) {
                tmpCntMap.put(eachCharinB, tmpCntMap.getOrDefault(eachCharinB, 0) + 1);
            }
            for (Map.Entry<Character, Integer> eachEntry : tmpCntMap.entrySet()) {
                char currChar = eachEntry.getKey();
                charBCntMap.put(currChar, Math.max(charBCntMap.getOrDefault(currChar, 0),  eachEntry.getValue()));
            }
        }
        // 检验集合A中的所有单词
        for (String a : A) {
            // 对于每个遍历到的单词，先统计其每个字母的出现次数
            Map<Character, Integer> charACntMap = new HashMap<>();
            for (char eachCharinA : a.toCharArray()) {
                charACntMap.put(eachCharinA, charACntMap.getOrDefault(eachCharinA, 0) + 1);
            }
            // 然后跟 charBCntMap 中每个位置上的数字比较
            // 只要均大于等于 charBCntMap 中的数字，就可以加入到结果 resList 中
            boolean allHasCount = true;
            for (Map.Entry<Character, Integer> eachEntry : charBCntMap.entrySet()) {
                char currChar = eachEntry.getKey();
                if (charACntMap.getOrDefault(currChar, 0) < eachEntry.getValue()) {
                    allHasCount = false;
                    break;
                }
            }
            if (allHasCount) {
                resList.add(a);
            }
        }
        return resList;
    }
    // TLE 因为每次都遍历一遍B太浪费时间
    // public List<String> wordSubsets(String[] A, String[] B) {
    //     List<String> resList = new ArrayList<>();
    //     for (String a : A) {
    //         Map<Character, Integer> charACntMap = new HashMap<>();
    //         for (char eachCharinA : a.toCharArray()) {
    //             charACntMap.put(eachCharinA, charACntMap.getOrDefault(eachCharinA, 0) + 1);
    //         }
    //         helper(a, B, charACntMap, resList);
    //     }
    //     return resList;
    // }
    // private void helper(String a, String[] B, Map<Character, Integer> charACntMap, List<String> resList) {
    //     for (String b : B) {
    //         Map<Character, Integer> charBCntMap = new HashMap<>();
    //         for (char eachCharinB : b.toCharArray()) {
    //             charBCntMap.put(eachCharinB, charBCntMap.getOrDefault(eachCharinB, 0) + 1);
    //             if (charACntMap.getOrDefault(eachCharinB, 0) < charBCntMap.get(eachCharinB)) {
    //                 return;
    //             }
    //         }
    //     }
    //     resList.add(a);
    // }
}
// @lc code=end

