/*
 * @lc app=leetcode id=43 lang=java
 *
 * [43] Multiply Strings
 */

// @lc code=start
class Solution {
    public String multiply(String num1, String num2) {
        int size_num1 = num1.length(), size_num2 = num2.length();
        int[] digits = new int[size_num1 + size_num2];
        // num2 * num1
        for (int i = size_num1 - 1; i >= 0; i--) {
            for (int j = size_num2 - 1; j >= 0; j--) {
                int pos_first = i + j;
                int pos_second = pos_first + 1;
                // 计算当前位数的结果
                int multiply_res = (num1.charAt(i) - '0') * (num2.charAt(j) - '0');
                // 当前结果加上末位数的结果
                int curr_sum = multiply_res + digits[pos_second];
                // 把新的结果记录下来
                digits[pos_first] += curr_sum / 10;
                digits[pos_second] = curr_sum % 10;
            }
        }
        StringBuilder res = new StringBuilder();

        for (int digit: digits) {
            // 如果当前不为空或者当前位数不为0
            // （这样可以跳过leading 0的情况，从第一个不为0的数字开始
            if (res.length() != 0 || digit != 0) {
                res.append(digit);
            }
        }
        // 判断结果是不是为空，是的话就是0，否则就是结果
        return res.length() == 0 ? "0": res.toString();
    }
    }
}
// @lc code=end

