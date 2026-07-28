/*
 * @lc app=leetcode id=11 lang=java
 *
 * [11] Container With Most Water
 */

// @lc code=start
class Solution {
    public int maxArea(int[] height) {
        // 两个指针，分别从头和尾开始
        int i = 0, j = height.length - 1;
        int res = Integer.MIN_VALUE;
        while (i < j) {
            // 计算当前i j指针覆盖的面积
            int currArea = Math.min(height[i], height[j]) * (j - i);
            // 更新答案
            res = Math.max(res, currArea);
            // 跳过更矮的那一根
            if (height[i] < height[j]) {
                i++;
            }
            else {
                j--;
            }
        }
        return res;
    }
}
// @lc code=end

