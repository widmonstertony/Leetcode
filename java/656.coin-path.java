/*
 * @lc app=leetcode id=656 lang=java
 *
 * [656] Coin Path
 */

// @lc code=start
class Solution {
    public List<Integer> cheapestJump(int[] A, int B) {
        // 代表当前数字的下一个index是什么
        int[] to = new int[A.length];
        // dp[i]代表到i时的最小cost
        int[] dp = new int[A.length];
        // 倒过来更新dp是因为lexicographically smallest such path
        // 数字小的答案会后更新覆盖数字大的答案
        for (int i = A.length - 1; i >= 0; i--) {
            // 把所有cost初始为最大值
            dp[i] = Integer.MAX_VALUE;
            // 把所有下一个坐标定为-1
            to[i] = -1;
            // 如果cost是-1，则不更新dp，这样确保只有cost为-1的dp是整数最大值
            if (A[i] == -1) {
                continue;
            }
            if (i == A.length - 1) {
                dp[i] = A[i];
            }
            else {
                // 把从1到B的cost全部计算一遍，取cost最小的保存下来
                for (int j = 1; j <= B; j++) {
                    if (i + j >= A.length) {
                        break;
                    }
                    // 检查cost是否是整数最大值，避免使用-1
                    if (dp[i + j] == Integer.MAX_VALUE)  {
                        continue;
                    }
                    if (dp[i + j] + A[i] < dp[i]) {
                        dp[i] = dp[i + j] + A[i];
                        to[i] = i + j;
                    }
                }
            }
        }
        List<Integer> resList = new ArrayList<>();
        // 用dp值来检查而不是to，因为如果只有一个元素但是有解，to没有值但是dp有值因为有cost
        if (dp[0] == Integer.MAX_VALUE) {
            return resList;
        }
        int currIdx = 0;
        while (currIdx != -1) {
            resList.add(currIdx + 1);
            currIdx = to[currIdx];
        }
        return resList;
    }
}
// @lc code=end

