/*
 * @lc app=leetcode id=476 lang=java
 *
 * [476] Number Complement
 */

// @lc code=start
class Solution {
    public int findComplement(int num) {
        // 每次都右移一位，并根据最低位的值先进行翻转
        // 如果当前值小于等于1了，就不用再调用递归函数了
        return (1 - num % 2) + 2 * (num <= 1 ? 0 : findComplement(num / 2));
    }
    // 土方法，一个bit一个bit翻过来求解
    // public int findComplement(int num) {
    //     int res = 0, base = 1;
    //     while (num > 0) {
    //         int currBit = num % 2;
    //         num /= 2; 
    //         if (currBit == 0) {
    //             res += base;
    //         }
    //         base *= 2;
    //     }
    //     return res;
    // }
}
// @lc code=end

