/*
 * @lc app=leetcode id=122 lang=java
 *
 * [122] Best Time to Buy and Sell Stock II
 */

// @lc code=start
class Solution {
    public int maxProfit(int[] prices) {
        // 最大的利润
        int maxProfitRes = 0;
        for (int i = 1; i < prices.length; i++) {
            // 如果当前股价比昨天高，则把昨天的买了和今天的卖了的利润加进去
            if (prices[i] > prices[i - 1]) {
                maxProfitRes += prices[i] - prices[i - 1];
            }
        }
        return maxProfitRes;
    }
}
// @lc code=end

