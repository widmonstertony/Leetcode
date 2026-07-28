/*
 * @lc app=leetcode id=123 lang=java
 *
 * [123] Best Time to Buy and Sell Stock III
 */

// @lc code=start
class Solution {
    public int maxProfit(int[] prices) {
        return maxProfitKTransaction(prices, 2);
    }
    private int maxProfitKTransaction(int[] prices, int k) {
        if (prices.length < 1) {
            return 0;
        }
        // 在到达第i天时最多可进行j次交易并且最后一次交易在最后一天卖出的最大利润
        // 局部最优
        int [][] localMax = new int[prices.length][k + 1];
        // global[i][j]为全局最优 
        // 在到达第i天时最多可进行j次交易的最大利润
        int[][] globalMax = new int[prices.length][k + 1];
        for (int i = 1; i < prices.length; i++) {
            for (int j = 1; j <= k; j++) {
                int diff = prices[i] - prices[i - 1];
                // 找出昨天的最大利润，是昨天卖掉最赚钱，还是昨天不卖最赚钱
                // localMax[i - 1][j]代表昨天卖掉并且最后一次交易在昨天的最大利润
                // localMax[i - 1][j] + diff代表从昨天卖掉变成昨天不卖今天卖的最大利润，并且进行最后一次交易j是在今天进行的
                // 和另一种情况，也就是在昨天进行了j - 1次交易的最大利润globalMax[i - 1][j - 1]，加上昨天买今天卖的这笔
                // 两种情况比较，得出到达第i天时最多可进行j次交易，并且最后一次交易在最后一天j卖出的最大利润
                localMax[i][j] = Math.max(globalMax[i - 1][j - 1], localMax[i - 1][j]) + diff;

                // 总共的最大利润
                // 就是今天卖掉并且进行了j次交易的最大利润，和昨天就已经进行了j次交易的最大利润的最大值
                globalMax[i][j] = Math.max(localMax[i][j], globalMax[i - 1][j]);
            }
        }
        return globalMax[prices.length - 1][k];
    }
}
// @lc code=end

