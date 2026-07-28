import java.util.HashMap;
import java.util.Map;

/*
 * @lc app=leetcode id=246 lang=java
 *
 * [246] Strobogrammatic Number
 */

// @lc code=start
class Solution {
    public boolean isStrobogrammatic(String num) {
        Map<Character, Character> convertMap = new HashMap<>();
        convertMap.put('0', '0');
        convertMap.put('1', '1');
        convertMap.put('6', '9');
        convertMap.put('9', '6');
        convertMap.put('8', '8');
        int left = 0, right = num.length() - 1;
        while(left <= right) {
            char leftNum = num.charAt(left) ;
            char rightNum = num.charAt(right);
            if (!convertMap.containsKey(leftNum) || !convertMap.containsKey(rightNum)) {
                return false;
            }
            if (convertMap.get(leftNum) != rightNum) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }
}
// @lc code=end

