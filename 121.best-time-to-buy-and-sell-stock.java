/*
 * @lc app=leetcode id=121 lang=java
 *
 * [121] Best Time to Buy and Sell Stock
 */

// @lc code=start
class Solution {
    public int maxProfit(int[] prices) {
        // 最小的买入价格
        int minBuyPrice = Integer.MAX_VALUE;
        // 最大的利润
        int maxProfitRes = 0;
        for (int i = 0; i < prices.length; i++) {
            // 更新最小的买入价格
            minBuyPrice = Math.min(minBuyPrice, prices[i]);
            // 更新最大利润，当前价格减去最小的买入价格，和之前最大利润比较
            maxProfitRes = Math.max(prices[i] - minBuyPrice, maxProfitRes);
        }
        return maxProfitRes;
    }
}
// @lc code=end

