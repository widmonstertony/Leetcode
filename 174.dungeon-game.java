/*
 * @lc app=leetcode id=174 lang=java
 *
 * [174] Dungeon Game
 */

// @lc code=start
class Solution {
    public int calculateMinimumHP(int[][] dungeon) {
        int m = dungeon.length, n = dungeon[0].length;
        // dp[i][j]代表从i,j位置走到公主至少需要多少血
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) {
            Arrays.fill(dp[i], Integer.MAX_VALUE);
        }
        // 公主下方和右方初始化为1，代表走到公主后至少还有1血
        dp[m][n - 1] = 1; dp[m - 1][n] = 1;
        for (int i = m - 1; i >= 0; i--) {
            for (int j = n - 1; j >= 0; j--) {
                // 从右边和下面选一格
                // 用较小的可生存血量减去当前房间的数字
                // 和1比较，如果小于1，说明当前dp是正数，加血
                // 则当前血量只需要为1就好了，因为当前的数值能加血加到足够走完剩下的
                // 因为生命为零游戏就结束了
                dp[i][j] = Math.max(1, Math.min(dp[i + 1][j], dp[i][j + 1]) - dungeon[i][j]);
            }
        }
        return dp[0][0];
    }
}
// @lc code=end

