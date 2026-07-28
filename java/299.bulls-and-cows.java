/*
 * @lc app=leetcode id=299 lang=java
 *
 * [299] Bulls and Cows
 */

// @lc code=start
class Solution {
    public String getHint(String secret, String guess) {
        int[] hashInt = new int[256];
        int cntA = 0, cntB = 0;
        for (int i = 0; i < secret.length(); i++) {
            char secretChar = secret.charAt(i);
            char guessChar = guess.charAt(i);
            if (guessChar == secretChar) {
                cntA++;
            }
            else {
                // 在处理不是bulls的位置时
                // 如果secret当前位置数字的映射值小于0
                // 则表示其在guess中出现过
                if (hashInt[secretChar] < 0) {
                    cntB++;
                }
                hashInt[secretChar]++;
                // 如果guess当前位置的数字的映射值大于0
                // 则表示其在secret中出现过
                if (hashInt[guessChar] > 0) {
                    cntB++;
                }
                hashInt[guessChar]--;
            }
        }
        return cntA + "A" + cntB + "B";
    }
    // public String getHint(String secret, String guess) {
    //     // 自己想的算法，set用来记录下数字出现过的所有位置坐标
    //     Map<Character, Set<Integer>> numIdxMap = new HashMap<>();
    //     for (int i = 0; i < secret.length(); i++) {
    //         char currNum = secret.charAt(i);
    //         Set<Integer> numIdxSet = numIdxMap.getOrDefault(currNum, new HashSet<>());
    //         numIdxSet.add(i);
    //         numIdxMap.put(currNum, numIdxSet);
    //     }
    //     int cntA = 0, cntB = 0;
    //     for (int i = 0; i < guess.length(); i++) {
    //         char currNum = guess.charAt(i);
    //         if (numIdxMap.containsKey(currNum)) {
    //             Set<Integer> numIdxSet = numIdxMap.get(currNum);
    //             if (numIdxSet.contains(i)) {
    //                 numIdxSet.remove(i);
    //                 cntA++;
    //             }
    //             else {
    //                 if (!numIdxSet.isEmpty()) {
    //                     for (Integer eachIdx: numIdxSet) {
    //                         if (guess.charAt(eachIdx) != currNum) {
    //                             numIdxSet.remove(eachIdx);
    //                             cntB++;
    //                             break;
    //                         }
    //                     }
    //                 }
    //             }
    //         }
    //     }
    //     return cntA + "A" + cntB + "B";
    // }
}
// @lc code=end

