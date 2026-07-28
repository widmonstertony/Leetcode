/*
 * @lc app=leetcode id=771 lang=java
 *
 * [771] Jewels and Stones
 */

// @lc code=start
class Solution {
    public int numJewelsInStones(String J, String S) {
        Set<Character> charSet = new HashSet<>();
        for (char eachChar : J.toCharArray()) {
            charSet.add(eachChar);
        }
        int resCnt = 0;
        for (char eachChar : S.toCharArray()) {
            if (charSet.contains(eachChar)) {
                resCnt++;
            }
        }
        return resCnt;
    }
}
// @lc code=end

