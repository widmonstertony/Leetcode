/*
 * @lc app=leetcode id=481 lang=java
 *
 * [481] Magical String
 */

// @lc code=start
class Solution {
    public int magicalString(int n) {
        if (n == 0) {
            return 0;
        }
        if (n <= 3) {
            return 1;
        }
        int[] magicString = new int[n];
        magicString[0] = 1;
        magicString[1] = 2;
        magicString[2] = 2;
        int currIdx = 3;
        int currNum = 1;
        int onesCnt = 1;
        // 一直往数组里反复写入1和2，currIdx一直加到n - 1停止
        for (int i = 2; i < n; i++) {
            // 生成当前i位置的数的个数的currNum（1或2）加进入到magic
            // 然后切换当前数字currNum
            for (int j = 0; j < magicString[i] && currIdx < n; j++) {
                magicString[currIdx] = currNum;
                currIdx++;
                if (currNum == 1) {
                    onesCnt++;
                }
            }
            // 1 变成 2， 2 变成 1
            currNum = 3 - currNum;
        }
        return onesCnt;
    }
}
// @lc code=end

