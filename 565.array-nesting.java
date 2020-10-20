/*
 * @lc app=leetcode id=565 lang=java
 *
 * [565] Array Nesting
 */

// @lc code=start
class Solution {
    public int arrayNesting(int[] nums) {
        // 用一个visited数组记录下访问过的数组
        boolean[] visited = new boolean[nums.length];
        int maxRes = 0;
        for (int i = 0; i < nums.length; i++) {
            if (!visited[i]) {
                int currIdx = i;
                int currRes = 0;
                while (!visited[currIdx]) {
                    currRes++;
                    visited[currIdx] = true;
                    currIdx = nums[currIdx];
                }
                maxRes = Math.max(maxRes, currRes);
            }
        }
        return maxRes;
    }
}
// @lc code=end

