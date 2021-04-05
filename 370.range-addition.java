/*
 * @lc app=leetcode id=370 lang=java
 *
 * [370] Range Addition
 */

// @lc code=start
class Solution {
    public int[] getModifiedArray(int length, int[][] updates) {
        // 这个数组代表着到i为止，当前累积和和上一位相差多少
        int[] cumulateArr = new int[length + 1];
        for (int [] update: updates) {
            // 因为从update[0]开始，数字变成了update[2]
            cumulateArr[update[0]] += update[2];
            // 到update[1]结束，update[1] + 1位置的数变成原来的，相比于上一位就减少了update[2]
            cumulateArr[update[1] + 1] -= update[2];
        }
        int[] res = new int[length];
        // 把每一位的累积和放进res数组里
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                res[i] = cumulateArr[i];
            }
            else {
                res[i] = cumulateArr[i] + res[i - 1];
            }
        }
        return res;
    }
}
// @lc code=end

