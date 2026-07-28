import java.util.ArrayList;
import java.util.List;

/*
 * @lc app=leetcode id=247 lang=java
 *
 * [247] Strobogrammatic Number II
 */

// @lc code=start
class Solution {
    public List<String> findStrobogrammatic(int n) {
        if (n == 1) {
            return findStrobogrammatic(n, true);
        }
        else {
            return findStrobogrammatic(n, false);
        }
    }
    public List<String> findStrobogrammatic(int n, boolean includeZero) {
        List<String> strobogrammaticArr = new ArrayList<>();
        if (n <= 0) {
            return strobogrammaticArr;
        }
        if (includeZero) {
            strobogrammaticArr.add("0");
        }
        strobogrammaticArr.add("1");
        strobogrammaticArr.add("8");
        if (n == 1) {
            return strobogrammaticArr;
        }
        List<String> resList = new ArrayList<>();
        strobogrammaticArr.add("6");
        strobogrammaticArr.add("9");
        if (n > 2) {
            List<String> reduceList = findStrobogrammatic(n - 2, true);
            for (String word : reduceList) {
                createString(word, resList, strobogrammaticArr);
            }
        }
        else {
            createString("", resList, strobogrammaticArr);
        }
        return resList;
    }
    private void createString(String word, List<String> resList, List<String> strobogrammaticArr) {
        for (String stroWord : strobogrammaticArr) {
            if (stroWord.equals("6")) {
                resList.add("6" + word + "9");
            }
            else if (stroWord.equals("9")) {
                resList.add("9" + word + "6");
            }
            else {
                resList.add(stroWord + word + stroWord);
            }
        }
    }
}
// @lc code=end

