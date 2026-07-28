/*
 * @lc app=leetcode id=375 lang=java
 *
 * [375] Guess Number Higher or Lower II
 */

// @lc code=start
class Solution {
    public int getMoneyAmount(int n) {
        // dp[i][j] 表示从数字i到j之间猜中任意一个数字最少需要花费的钱数
        int[][] dp = new int[n + 1][n + 1];
        // 遍历每一段区间 [j, i]
        for (int i = 2; i <= n; i++) {
            for (int j = i - 1; j > 0; j--) {
                int allCostMin = Integer.MAX_VALUE;
                // 然后遍历该区间中的每一个数字，计算局部最大值
                // 这个正好是将该区间在每一个位置k都分为两段j, k-1和k+1, i
                // 然后取当前位置k的花费 加上左右两段更大的花费之和为局部最大值
                //   这是在考虑最坏的情况的cost
                // 然后再更新到j到i的全局最小值，也就是j到i应该取整体cost最小的
                //   这是在考虑所有猜测的方法中，最坏情况里cost最小的是哪一个
                for (int k = j + 1; k < i; k++) {
                    allCostMin = Math.min(allCostMin, k + Math.max(dp[j][k - 1], dp[k + 1][i]));
                }
                // j和i是否是相邻的，相邻的话赋为j,因为cost更小
                if (j + 1 == i) {
                    dp[j][i] = j;
                }
                // 否则j到i的最小值就是全局最小cost的值
                else {
                    dp[j][i] = allCostMin;
                }
            }
        }
        return dp[1][n];
    }
}
// @lc code=end

