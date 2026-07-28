import java.util.Stack;

/*
 * @lc app=leetcode id=402 lang=java
 *
 * [402] Remove K Digits
 */

// @lc code=start
class Solution {
    public String removeKdigits(String num, int k) {
        // 如果需要删除的长度和String一样长，则全删掉
        if (k == num.length()) {
            return "0";
        }

        // 一个单调递增的Stack
        StringBuilder resBuilder = new StringBuilder();

        // 遍历一遍当前String的每个char
        for (int i = 0; i < num.length(); i++) {
            char currChar = num.charAt(i);

            // 如果当前的这个char比单调Stack里的char要小，说明要把之前大的数字删掉
            while (resBuilder.length() != 0 && currChar < resBuilder.charAt(resBuilder.length() - 1) && k > 0) {
                resBuilder.delete(resBuilder.length() - 1, resBuilder.length());
                k--;
            }

            // 直到之前比当前数字小的数字都被删除后，再把当前数字加进去
            resBuilder.append(currChar);
        }

        // 如果此时还有需要删除的，则只取前面的部分，因为是单调递增的
        if (k > 0) {
            resBuilder.delete(resBuilder.length() - k, resBuilder.length());
        }

        // 把起始的0删除
        while (resBuilder.length() > 1 && resBuilder.charAt(0) == '0') {
            resBuilder.delete(0, 1);
        }

        return resBuilder.toString();
    }
    // public String removeKdigits(String num, int k) {
    //     if (k == num.length()) {
    //         return "0";
    //     }
    //     Stack<Integer> increSt = new Stack<>();
    //     int i = 0;
    //     while (i < num.length()) {
    //         int currNum = Integer.valueOf(num.substring(i, i + 1));
    //         while (!increSt.isEmpty() && currNum < increSt.peek() && k > 0) {
    //             k--;
    //             increSt.pop();
    //         }
    //         increSt.add(currNum);
    //         i++;
            
    //     }
    //     StringBuilder resBuilder = new StringBuilder();
    //     while (!increSt.isEmpty()) {
    //         resBuilder.insert(0, increSt.pop().toString());
    //     }
    //     if (i < num.length() - 1) {
    //         resBuilder.append(num.substring(i));
    //     }
    //     if (k > 0) {
    //         resBuilder.delete(resBuilder.length() - k, resBuilder.length());
    //     }
    //     while (resBuilder.length() > 1 && resBuilder.charAt(0) == '0') {
    //         resBuilder.delete(0, 1);
    //     }
    //     return resBuilder.toString();
    // }
}
// @lc code=end

