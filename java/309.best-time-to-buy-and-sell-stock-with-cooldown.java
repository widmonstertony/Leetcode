/*
 * @lc app=leetcode id=309 lang=java
 *
 * [309] Best Time to Buy and Sell Stock with Cooldown
 */

// @lc code=start
class Solution {
    public int maxProfit(int[] prices) {
        if (prices.length == 0) {
            return 0;
        }
        // buy[i]表示在第i天之前最后一个操作是买，此时的最大收益。
        // sell[i]表示在第i天之前最后一个操作是卖，此时的最大收益。
        int[] buy = new int[prices.length];
        int[] sell = new int[prices.length];
        for (int i = 0; i < prices.length; i++) {
            if (i == 0) {
                buy[i] = -prices[i];
                sell[i] = 0;
            }
            else if (i == 1) {
                buy[i] = Math.max(-prices[i], buy[i - 1]);
                sell[i] = Math.max(buy[i - 1] + prices[i], sell[i - 1]);
            }
            else {
                buy[i] = Math.max(sell[i - 2] - prices[i], buy[i - 1]);
                sell[i] = Math.max(buy[i - 1] + prices[i], sell[i - 1]);
            }
        }
        return sell[prices.length - 1];
    }
}
// @lc code=end

