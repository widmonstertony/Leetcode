/*
 * @lc app=leetcode id=276 lang=java
 *
 * [276] Paint Fence
 */

// @lc code=start
class Solution {
    public int numWays(int n, int k) {
        int[] dp = new int[n];
        // 因为用的是前两个的数据，所以还可以优化成两个变量
        for (int i = 0; i < n; i++) {
            if (i == 0) {
                dp[i] = k;
            }
            else if (i == 1) {
                dp[i] = dp[i - 1] * k;
            }
            else {
                // 两种情况
                // 前面和当前一种颜色，则表示更前一个栅栏颜色和右边两个不同, 当前颜色有k-1个颜色可选（排除更前的那个颜色），更前颜色有dp[i - 2]种方式涂
                // 前面和当前不一样颜色，则当前颜色有k-1种选择，前一个颜色总共有dp[i - 1]种方式涂
                dp[i] = dp[i - 2] * (k - 1) + dp[i - 1] * (k - 1);
            }
        }
        
        return dp[n - 1];
    }
}
// @lc code=end

