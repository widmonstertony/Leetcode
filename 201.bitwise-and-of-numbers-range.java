/*
 * @lc app=leetcode id=201 lang=java
 *
 * [201] Bitwise AND of Numbers Range
 */

// @lc code=start
class Solution {
    public int rangeBitwiseAnd(int m, int n) {
        // 最后的数是该数字范围内所有的数的左边共同的部分
        // 需要找到m和n公共的左边部分
        // 先创建一个32位都是1的mask
        int mask = Integer.MAX_VALUE;
        // 一直左移，直到找到m和n的公共部分
        while ((m & mask) != (n & mask)) {
            mask <<= 1;
        }
        // 返回公共部分
        return m & mask;
    }
}
// @lc code=end

