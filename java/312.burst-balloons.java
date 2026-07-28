/*
 * @lc app=leetcode id=312 lang=java
 *
 * [312] Burst Balloons
 */

// @lc code=start
class Solution {
    public int maxCoins(int[] nums) {
        if (nums.length == 0) {
            return 0;
        }
        if (nums.length == 1) {
            return nums[0];
        }
        // dp[i][j] 表示打爆区间 [i,j] 中的所有气球能得到的最多金币
        int[][] dp = new int[nums.length][nums.length];
        for (int len = 1; len <= nums.length; len++) {
            for (int i = 0; i <= nums.length - len; i++) {
                int j = i + len - 1;
                // k在区间 [i, j] 中，假如第k个气球最后被打爆
                // 把k的所有情况计算一遍，找出在什么时候打爆k得分最多
                for (int k = i; k <= j; k++) {
                    // 此时区间 [i, j] 被分成了三部分，[i, k-1]，[k]，和 [k+1, j]
                    int leftPt, rightPt;
                    if (i == 0) {
                        leftPt = 1;
                    }
                    else {
                        leftPt = nums[i - 1];
                    }
                    if (j == nums.length - 1) {
                        rightPt = 1;
                    }
                    else {
                        rightPt = nums[j + 1];
                    }
                    // 因为k是区间[i, j]里最后打爆的，所以加上左右两边的分数dp[i][k - 1]和dp[k + 1][j]
                    // 因为[i][j]里的气球都被打爆了，k左边的数字应该是i - 1, 右边是j + 1，所以再加上打爆k所获得的分数
                    if (k == 0) {
                        dp[i][j] = Math.max(dp[i][j], leftPt * nums[k] * rightPt + dp[k + 1][j]);

                    }
                    else if (k == nums.length - 1) {
                        dp[i][j] = Math.max(dp[i][j], leftPt * nums[k] * rightPt + dp[i][k - 1]);
                    }
                    else {
                        dp[i][j] = Math.max(dp[i][j], leftPt * nums[k] * rightPt + dp[i][k - 1] + dp[k + 1][j]);
                    }
                }
            }
        }
        return dp[0][nums.length - 1];
    }
}
// @lc code=end

