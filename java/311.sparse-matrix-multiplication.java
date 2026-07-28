/*
 * @lc app=leetcode id=311 lang=java
 *
 * [311] Sparse Matrix Multiplication
 */

// @lc code=start
class Solution {
    public int[][] multiply(int[][] A, int[][] B) {
        int[][] res = new int[A.length][B[0].length];
        // i x j 的矩阵A乘以一个 j x k 的矩阵B会得到一个 i x k 大小的矩阵C
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[0].length; j++) {
                // 检测是否为0，可以节约很多时间
                if (A[i][j] != 0) {
                    for (int k = 0; k < B[0].length; k++) {
                        if (B[j][k] != 0) {
                            res[i][k] += A[i][j] * B[j][k];
                        }
                    }
                }
            }
        }
        return res;
    }
}
// @lc code=end

