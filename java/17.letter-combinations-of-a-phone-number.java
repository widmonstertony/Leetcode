import java.awt.List;

/*
 * @lc app=leetcode id=17 lang=java
 *
 * [17] Letter Combinations of a Phone Number
 */

// @lc code=start
class Solution {
    public List<String> letterCombinations(String digits) {
        String[] numToStrDict = new String[]{"", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"};
        List<String> resList = new ArrayList<>();
        if (digits.length() == 0) {
            return resList;
        }
        String numStr = numToStrDict[digits.charAt(0) - '0'];
        for (char numChar : numStr.toCharArray()) {
            List<String> resComList = letterCombinations(digits.substring(1));
            if (resComList.size() == 0) {
                resList.add(Character.toString(numChar));
            }
            for (String comStr : resComList) {
                resList.add(numChar + comStr);
            }
        }
        return resList;
    }
}
// @lc code=end

