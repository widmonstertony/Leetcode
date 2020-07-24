/*
 * @lc app=leetcode id=304 lang=java
 *
 * [304] Range Sum Query 2D - Immutable
 */

// @lc code=start
class NumMatrix {

    // 累计区间(0, 0)到(i - 1, j - 1)这个矩形区间所有的数字之和
    // 加上一行是为了防止sumRegion时越线判断太麻烦
    int[][] dp;
    public NumMatrix(int[][] matrix) {
        if (matrix == null || matrix.length == 0) {
            return;
        }
        
        int m = matrix.length, n = matrix[0].length;
        dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                // 两个长方形总和减去中间重合部分，再加上当前数字
                dp[i][j] = dp[i - 1][j] + dp[i][j - 1] - dp[i - 1][j - 1] 
                            + matrix[i - 1][j - 1];
            }
        }
    }
    
    public int sumRegion(int row1, int col1, int row2, int col2) {
        // 因为代表的是i - 1，j - 1的值，所以都加一方便理解
        row1++;col1++;row2++;col2++;

        // 返回row2 col2到左上角的长方形
        // 减去region上方的那个左边到顶点，右边到col2，高度为row1 - 1的长方形
        // 再减去region左边的上边到顶点，下边到row2，宽度为col1 - 1的长方形
        // 因为减了两次中间的重复面积，所以要再加回来
        // 也就是Region左上角往左上方斜移一位后的点到左边顶点的长方形，高度为row1 - 1 宽度为col1 - 1
        return dp[row2][col2] - dp[row1 - 1][col2] - dp[row2][col1 - 1] + dp[row1 - 1][col1 - 1];
    }
}

/**
 * Your NumMatrix object will be instantiated and called as such:
 * NumMatrix obj = new NumMatrix(matrix);
 * int param_1 = obj.sumRegion(row1,col1,row2,col2);
 */
// @lc code=end

