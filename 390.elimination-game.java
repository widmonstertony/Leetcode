/*
 * @lc app=leetcode id=390 lang=java
 *
 * [390] Elimination Game
 */

// @lc code=start
class Solution {
    public int lastRemaining(int n) {
        boolean leftToRight = true;
        int res = 1, nextStep = 1, remainCnt = n;
        while (remainCnt > 1) {
            // 如果是从左往右方向
            // 又或者是从右往左方向，但是当前剩余数量是奇数
            // 那么左边第一个数就要被移除
            // 需要把res的位置移到下一个还存在的数的位置上
            // 一直移动res直到只剩下最后一个数
            if (leftToRight || remainCnt % 2 == 1) {
                res += nextStep;
            }
            // 每删除一次，剩余的数字都会只剩下一半
            // 同时剩下的在场上的数字的间距也会加倍
            remainCnt /= 2;
            nextStep *= 2;
            // 记得改变方向
            leftToRight = !leftToRight;
        }
        return res;
    }
}
// @lc code=end

