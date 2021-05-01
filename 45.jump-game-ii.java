/*
 * @lc app=leetcode id=45 lang=java
 *
 * [45] Jump Game II
 */

// @lc code=start
class Solution {
    public int jump(int[] nums) {
        // 贪心 当前花这一步能跳到的最远距离
        int rightFarthest  = 0;
        // 当前能reach的最远距离
        int currFarthest = 0;
        int res = 0;
        // 注意这里不需要包括最后一个
        // 因为我们到达最后一个就不需要跳了
        for (int i = 0; i < nums.length - 1; i++) {
            currFarthest = Math.max(currFarthest, nums[i] + i);
            // 已经到达上一步能跳的最远距离，必须要花下一步了
            if (i == rightFarthest) {
                // 更新下一步所能到达的最远距离
                rightFarthest = currFarthest;
                res++;
            }
        }
        return res;
    }
}
// @lc code=end

