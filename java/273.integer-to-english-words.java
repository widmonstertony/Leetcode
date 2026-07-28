/*
 * @lc app=leetcode id=273 lang=java
 *
 * [273] Integer to English Words
 */

// @lc code=start
class Solution {
    final String[] STR_DIGITS = new String[]{"Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
    final String[] STR_TENS = new String[]{"Zero", "Ten", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};
 
    public String numberToWords(int num) {
        if (num == 0) {
            return STR_DIGITS[num];
        }
        String ret = intToString(num);
        return ret.substring(1, ret.length());
    }

    private String intToString(int num) {
        if (num >= 1000000000) {
            return intToString(num / 1000000000) + " Billion" + intToString(num % 1000000000);
        } 
        else if (num >= 1000000) {
            return intToString(num / 1000000) + " Million" + intToString(num % 1000000);
        } 
        else if (num >= 1000) {
            return intToString(num / 1000) + " Thousand" + intToString(num % 1000);
        } 
        else if (num >= 100) {
            return intToString(num / 100) + " Hundred" + intToString(num % 100);
        }
        else if (num >= 20) {
            return " " + STR_TENS[num / 10] + intToString(num % 10);
        }
        else if (num >= 1) {
            return " " + STR_DIGITS[num];
        }
        else {
            return "";
        }
    }
}
// @lc code=end

