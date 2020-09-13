/*
 * @lc app=leetcode id=223 lang=java
 *
 * [223] Rectangle Area
 */

// @lc code=start
class Solution {
    public int computeArea(int A, int B, int C, int D, int E, int F, int G, int H) {
        //先求出重叠区间的四个点 topLeft，topRight，bottomLeft，bottomRight的值
        int topLeft = Math.max(A, E), topRight = Math.max(Math.min(C, G), topLeft);
        int bottomLeft = Math.max(B, F), bottomRight = Math.max(Math.min(D, H), bottomLeft);
        // 再求出重叠区间的面积, 两个长方形面积之和减去重叠面积就是剩余面积
        return (C - A) * (D - B) + (G - E) * (H - F) - (bottomRight - bottomLeft) * (topRight - topLeft);
    }
}
// @lc code=end

