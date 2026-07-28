/*
 * @lc app=leetcode id=74 lang=java
 *
 * [74] Search a 2D Matrix
 */

// @lc code=start
class Solution {
    public boolean searchMatrix(int[][] matrix, int target) {
        if (matrix.length == 0 || matrix[0].length == 0) {
            return false;
        }
        int left = 0, right = matrix.length - 1;
        // 先上下找到下界，也就是比target小或者等于target的那个
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (matrix[mid][0] > target) {
                right = mid - 1;
            }
            else {
                left = mid + 1;
            }
        }
        // 下界可以能是-1，因为target可能比第一个还小
        int arrIndex = right;
        if (right < 0) {
            arrIndex = 0;
        }
        left = 0;
        right = matrix[arrIndex].length - 1;
        // 二分法找target在当前array的位置
        while (left <= right) {
            int mid = left + (right - left ) / 2;
            if (matrix[arrIndex][mid] > target) {
                right = mid - 1;
            }
            else if (matrix[arrIndex][mid] < target) {
                left = mid + 1;
            }
            else {
                return true;
            }
        }
        return false;
    }
}
// @lc code=end

