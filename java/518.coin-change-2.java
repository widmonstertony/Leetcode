/*
 * @lc app=leetcode id=518 lang=java
 *
 * [518] Coin Change 2
 */

// @lc code=start
class Solution {
    public int change(int amount, int[] coins) {
        Arrays.sort(coins);
        int[] dp = new int[amount + 1];
        dp[0] = 1;
        // 遍历所有的硬币
        // 注意要把硬币放在outer loop，不然的话会重复计算
        // 一个硬币一个硬币的增加，每增加一个硬币，都从1遍历到 amount
        for (int coin : coins) {
            // 每一次循环一个coin，dp[i]都代表着用到目前位置的coin，总共有多少种可以组成i的方式
            for(int currAmount = coin; currAmount <= amount; currAmount++) {
                dp[currAmount] += dp[currAmount - coin];
            }
        }
        return dp[amount];
    }
}
// @lc code=end

