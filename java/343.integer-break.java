import java.util.Arrays;

/*
 * @lc app=leetcode id=343 lang=java
 *
 * [343] Integer Break
 */

// @lc code=start
class Solution {
    public int integerBreak(int n) {
        // dp[i] 表示数字i拆分为至少两个正整数之和的最大乘积
        // 数组大小为 n+1，值均初始化为1，因为正整数的乘积不会小于1
        int[] dp = new int[n + 1];
        Arrays.fill(dp, 1);
        for (int i = 3; i <= n; i++) {
            // 对于每个i，需要遍历所有小于i的数，因为这些都是潜在的拆分情况
            for (int j = 1; j < i; j++) {
                // 首先计算拆分为两个数字的乘积，即j乘以 i-j
                // 然后是拆分为多个数字的情况，这里就要用到 dp[i-j] 了
                // 这个值表示数字 i-j 任意拆分可得到的最大乘积
                // 再乘以j就是数字i可拆分得到的乘积，取二者的较大值来更新 dp[i]
                // 最后返回 dp[n] 即可
                dp[i] = Math.max(dp[i], Math.max(j * (i - j), j * dp[i - j]));
            }
        }
        return dp[n];
    }
}
// @lc code=end

