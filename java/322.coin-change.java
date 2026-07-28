import java.util.Arrays;

/*
 * @lc app=leetcode id=322 lang=java
 *
 * [322] Coin Change
 */

// @lc code=start
class Solution {
    public int coinChange(int[] coins, int amount) {
        Arrays.sort(coins);
        // dp[i] 表示钱数为i时的最小的找零硬币数
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);
        dp[0] = 0;
        for (int i = 1; i <= amount; i++) {
            // 遍历所有硬币，一直更新当前数额i的最佳组合方式
            for (int coin: coins) {
                // coins[j] 为第j个硬币 确保要比当前数目i大
                if (coin <= i) {
                    //  i - coins[j] 为钱数i减去其中一个硬币的值
                    dp[i] = Math.min(dp[i], dp[i - coin] + 1); 
                }
            }
        }
        if (dp[amount] > amount) {
            return -1;
        }
        return dp[amount];
    }
}
// @lc code=end

