/*
 * @lc app=leetcode id=265 lang=java
 *
 * [265] Paint House II
 */

// @lc code=start
class Solution {
    // O(n * k)
    public int minCostII(int[][] costs) {
        if (costs.length == 0 || costs[0].length == 0) {
            return 0;
        }
        // 因为这个只用到了上一个位置的信息，所以可以优化成一维数组
        int[][] minCostDp = new int[costs.length][costs[0].length];
        for (int i = 0; i < costs.length; i++) {
            int minColor = -1, secondMinColor = -1;
            // 先找到上一个位置的最小和第二小花费的颜色
            for (int j = 0; j < costs[i].length; j++) {
                if (i == 0) {
                    continue;
                }
                int currCost = minCostDp[i - 1][j];
                if (minColor == -1) {
                    minColor = j;
                }
                else if (minColor >= 0 && currCost < minCostDp[i - 1][minColor]) {
                    secondMinColor = minColor;
                    minColor = j;
                }
                else if (secondMinColor == -1 || currCost < minCostDp[i - 1][secondMinColor]) {
                    secondMinColor = j;
                }
            }
            // 再更新当前位置的所有颜色的最小花费
            for (int j = 0; j < costs[i].length; j++) {
                if (i == 0) {
                    minCostDp[i][j] = costs[i][j];
                }
                // 如果当前颜色和上一个位置的最小花费的颜色一样，则当前颜色只能去上一个第二花费小的颜色
                else if (j == minColor) {
                    minCostDp[i][j] = minCostDp[i - 1][secondMinColor] + costs[i][j];
                }
                // 否则当前颜色的最小花费就是上一个位置的最小花费的颜色+当前颜色的花费
                else {
                    minCostDp[i][j] = minCostDp[i - 1][minColor] + costs[i][j];
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
    // O(n * k * k)
    // public int minCostII(int[][] costs) {
    //     if (costs.length == 0 || costs[0].length == 0) {
    //         return 0;
    //     }
    //     // 因为这个只用到了上一个位置的信息，所以可以优化成一维数组
    //     int[][] minCostDp = new int[costs.length][costs[0].length];
    //     for (int i = 0; i < costs.length; i++) {
    //         for (int j = 0; j < costs[i].length; j++) {
    //             if (i == 0) {
    //                 minCostDp[i][j] = costs[i][j];
    //             }
    //             // 当前i位置选j颜色的最小cost，等于上一个位置不选j的最小cost+当前位置j的cost
    //             else {
    //                 minCostDp[i][j] = Integer.MAX_VALUE;
    //                 for (int k = 1; k < costs[i].length; k++) {
    //                     minCostDp[i][j] = Math.min(minCostDp[i][j], minCostDp[i - 1][(j + k) % costs[i].length] + costs[i][j]);
    //                 }
    //             }
    //         }
    //     }
    //     // 找出最后一个房子的所有颜色里的最小花费
    //     int res = Integer.MAX_VALUE;
    //     for (int j = 0; j < costs[0].length; j++) {
    //         res = Math.min(res, minCostDp[costs.length - 1][j]);
    //     }
    //     return res;
    // }
}
// @lc code=end

