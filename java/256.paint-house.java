/*
 * @lc app=leetcode id=256 lang=java
 *
 * [256] Paint House
 */

// @lc code=start
class Solution {
    public int minCost(int[][] costs) {
        if (costs.length == 0 || costs[0].length == 0) {
            return 0;
        }
        // 因为这个只用到了上一个位置的信息，所以可以优化成一维数组
        int[][] minCostDp = new int[costs.length][costs[0].length];
        for (int i = 0; i < costs.length; i++) {
            for (int j = 0; j < costs[i].length; j++) {
                if (i == 0) {
                    minCostDp[i][j] = costs[i][j];
                }
                // 当前i位置选j颜色的最小cost，等于上一个位置不选j的最小cost+当前位置j的cost
                else {
                    minCostDp[i][j] = Integer.MAX_VALUE;
                    for (int k = 1; k < costs[i].length; k++) {
                        minCostDp[i][j] = Math.min(minCostDp[i][j], minCostDp[i - 1][(j + k) % costs[i].length] + costs[i][j]);
                    }
                }
            }
        }
        // 找出最后一个房子的所有颜色里的最小花费
        int res = Integer.MAX_VALUE;
        for (int j = 0; j < costs[0].length; j++) {
            res = Math.min(res, minCostDp[costs.length - 1][j]);
        }
        return res;
    }
}
// @lc code=end

