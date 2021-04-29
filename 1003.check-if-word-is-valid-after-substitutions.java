/*
 * @lc app=leetcode id=1003 lang=java
 *
 * [1003] Check If Word Is Valid After Substitutions
 */

// @lc code=start
class Solution {
    public boolean isValid(String s) {
        Stack<Character> strStack = new Stack();
        for (char eachChar : s.toCharArray()) {
            if (eachChar == 'c') {
                // 有c，一定要有a和b，不然后面永远消不掉
                if (strStack.size() < 2) {
                    return false;
                }
                else {
                    char second = strStack.pop();
                    char first = strStack.pop();
                    // 有c，一定要有a和b，不然后面永远消不掉
                    if (first != 'a' || second != 'b') {
                        return false;
                    }
                }
            }
            else {
                strStack.push(eachChar);
            }
        }
        return strStack.isEmpty();
    }
    // brute force, not good
    // public boolean isValid(String s) {
    //     if (s.length() == 0) {
    //      return true;
    //     }
    //     for (int i = 0; i < s.length(); i++) {
    //         char currChar = s.charAt(i);
    //         if (currChar == 'a') {
    //             if (i < s.length() - 2 && s.charAt(i + 1) == 'b' && s.charAt(i + 2) ==  'c') {
    //                 return isValid(s.substring(0, i) + s.substring(i + 3));
    //             }
    //         }
    //     }
    //     return false;
    // }
}
// @lc code=end

