/*
 * @lc app=leetcode id=394 lang=java
 *
 * [394] Decode String
 */

// @lc code=start
class Solution {
    public String decodeString(String s) {
        if (s.length() == 0) {
            return s;
        }
        StringBuilder resBuilder = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char currChar = s.charAt(i);
            // 如果当前字符是数字
            if (Character.isDigit(currChar)) {
                // 把左括号前面的数字找到
                int times = currChar - '0';
                i++;
                while (Character.isDigit(s.charAt(i))) {
                    times = times * 10 + s.charAt(i) - '0';
                    i++;
                }
                i++;
                int leftCnt = 1;
                // 左右括号中间的string
                StringBuilder inside = new StringBuilder();
                // 找到右边的括号
                while (leftCnt != 0) {
                    currChar = s.charAt(i);
                    if (currChar == '[') {
                        leftCnt++;
                    }
                    else if (currChar == ']') {
                        leftCnt--;
                        if (leftCnt == 0) {
                            break;
                        }
                    }
                    inside.append(currChar);
                    i++;
                }
                // 把括号中间的内容先decode
                String decoIns = decodeString(inside.toString());
                // 再根据次数添加到答案里
                for (; times > 0; times--) {
                    resBuilder.append(decoIns);
                }
            }
            else {
                resBuilder.append(currChar);
            }
        }
        return resBuilder.toString();
    }
}
// @lc code=end

