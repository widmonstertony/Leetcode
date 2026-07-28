/*
 * @lc app=leetcode id=498 lang=java
 *
 * [498] Diagonal Traverse
 */

// @lc code=start
class Solution {
    public int[] findDiagonalOrder(int[][] matrix) {
        if (matrix.length == 0) {
            return new int[0];
        }
        int[] resArray = new int[matrix.length * matrix[0].length];
        int i = 0, j = 0;
        int curr = 0;
        boolean directionUp = true;
        while (curr < resArray.length) {
            resArray[curr] = matrix[i][j];
            curr++;
            // 如果是往上走
            if (directionUp) {
                if (i == 0 && j != matrix[0].length - 1) {
                    j++;
                    directionUp = false;
                }
                else if (j == matrix[0].length - 1) {
                    i++;
                    directionUp = false;
                }
                else {
                    i--; j++;
                }
            }
            // 如果是往下走
            else {
                if (j == 0 && i != matrix.length - 1) {
                    i++;
                    directionUp = true;
                }
                else if (i == matrix.length - 1) {
                    j++;
                    directionUp = true;
                }
                else {
                    i++;j--;
                }
            }
        }
        return resArray;
    }
}
// @lc code=end

