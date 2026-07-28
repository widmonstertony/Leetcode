/*
 * @lc app=leetcode id=769 lang=java
 *
 * [769] Max Chunks To Make Sorted
 */

// @lc code=start
class Solution {
    public int maxChunksToSorted(int[] arr) {
        int res = 0;
        int currMax = Integer.MIN_VALUE;
        // 断开为新块儿的地方都是
        // 当之前出现的最大值正好和当前位置坐标相等的地方
        for (int i = 0; i < arr.length; i++) {
            currMax = Math.max(currMax, arr[i]);
            // 如果最大值正好和当前位置坐标相等，则可以分块
            if (currMax == i) {
                res++;
            }
        }

        return res;
    }
}
// @lc code=end

