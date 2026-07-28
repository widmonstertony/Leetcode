/*
 * @lc app=leetcode id=223 lang=java
 *
 * [223] Rectangle Area
 */

// @lc code=start
class Solution {
    public int computeArea(int A, int B, int C, int D, int E, int F, int G, int H) {
        // 先求出重叠区间的四个点 topLeft，topRight，bottomLeft，bottomRight的值
        // 由于交集都是在中间
        // 横边的左端点是两个矩形左顶点横坐标的较大值, 右端点是两个矩形右顶点的较小值
        // 竖边的下端点是两个矩形下顶点纵坐标的较大值，上端点是两个矩形上顶点纵坐标的较小值
        int topLeft = Math.max(A, E), topRight = Math.max(Math.min(C, G), topLeft);
        int bottomLeft = Math.max(B, F), bottomRight = Math.max(Math.min(D, H), bottomLeft);
        // 再求出重叠区间的面积, 两个长方形面积之和减去重叠面积就是剩余面积
        return (C - A) * (D - B) + (G - E) * (H - F) - (bottomRight - bottomLeft) * (topRight - topLeft);
    }
}
// @lc code=end

