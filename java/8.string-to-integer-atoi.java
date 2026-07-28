/*
 * @lc app=leetcode id=8 lang=java
 *
 * [8] String to Integer (atoi)
 */

// @lc code=start
class Solution {
    public int myAtoi(String s) {
        int sign = 1, base = 0, currIdx = 0;
        //跳过空格
        while (currIdx < s.length() && s.charAt(currIdx) == ' ') {
            currIdx++;
        }
        // 如果这时已经超出，说明当前字符全是空格，结束
        if (currIdx == s.length()) {
            return base;
        }
        // 判断符号，移动当前坐标
        if (s.charAt(currIdx) == '+') {
            currIdx++;
        }
        else if (s.charAt(currIdx) == '-') {
            currIdx++;
            sign *= -1;
        }
        while (currIdx < s.length()) {
            char currChar = s.charAt(currIdx);
            if (currChar < '0' || currChar > '9') {
                break;
            }
            // 如果当前值乘以10后超过了最大值
            // edge case：32位int的最大值 2147483647的最后一位
            if (base > Integer.MAX_VALUE / 10 || 
                (base == Integer.MAX_VALUE / 10 && currChar - '0' > 7)) {
                return sign > 0 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
            }
            base = 10 * base + (currChar - '0');
            currIdx++;
        }
        return base * sign;
    }
}
// @lc code=end

