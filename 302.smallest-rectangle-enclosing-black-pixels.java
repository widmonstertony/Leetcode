/*
 * @lc app=leetcode id=302 lang=java
 *
 * [302] Smallest Rectangle Enclosing Black Pixels
 */

// @lc code=start
class Solution {
    // 二分法 O(mlogn + nlogm)
    public int minArea(char[][] image, int x, int y) {
        int m = image.length, n = image[0].length;
        // find top in the column array within [0, x)
        // 上边界是某个行数，范围肯定在 [0, x]
        int top = binarySearch(image, 0, x, 0, n, true, true);
        // find bottom in the column array within [x + 1, m)
        int bottom = binarySearch(image, x + 1, m, 0, n, true, false);
        // find left in the row array within [0, y)
        int left = binarySearch(image, 0, y, top, bottom, false, true);
        // find right in the row array within [y + 1, n)
        int right = binarySearch(image, y + 1, n, top, bottom, false, false);
        return (bottom - top) * (right - left);
    }

    private int binarySearch(char[][] image, int left, int right, int low, int high, boolean isHorizontal, boolean isLowerBound) {
        while (left < right) {
            int currIdx = low, mid = left + (right - left) / 2;
            // 能成为边界的条件是该行中至少有一个点是1
            // 列数从low开始遍历，直到找到为1的点，或者到达越界位置high
            while (currIdx < high && (isHorizontal ? image[mid][currIdx] : image[currIdx][mid]) == '0') {
                currIdx++;
            }
            // 没越界的话，说明当前行列有1
            // 找下边界也是同样的道理，但是跟上边界稍微又些不同的地方是
            // 如果当前行找到了1，应该再往下找，把上半部分抛弃
            // 而上边界找到了1，是继续往上找，把下半部分抛弃
            // 所以加一个isLowerBound，来翻转找的顺序
            if (currIdx < high == isLowerBound) {
                right = mid;
            }
            // 越界的话，说明当前行/列没有1，移动相应bound
            else {
                left = mid + 1;
            }
        }
        return left;
    }

    // linear solution，O(mn)
    // public int minArea(char[][] image, int x, int y) {
    //     // 找到长方形的顶部底部左边和右边的行数列数
    //     int top = x, bottom = x, left = y, right = y;
    //     for (int i = 0; i < image.length; i++) {
    //         for (int j = 0; j < image[i].length; j++) {
    //             // 如果当前是黑色，更新顶部底部左边和右边的行数列数
    //             if (image[i][j] == '1') {
    //                 top = Math.min(top, i);
    //                 bottom = Math.max(bottom, i);
    //                 left = Math.min(left, j);
    //                 right = Math.max(right, j);
    //             }
    //         }
    //     }
    //     // 长 * 宽就是长方形的面积
    //     return (bottom - top + 1) * (right - left + 1);
    // }
}
// @lc code=end

