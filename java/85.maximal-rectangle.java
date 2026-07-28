/*
 * @lc app=leetcode id=85 lang=java
 *
 * [85] Maximal Rectangle
 */

// @lc code=start
class Solution {
    public int maximalRectangle(char[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return 0;
        }
        int[] heights = new int[matrix[0].length];
        int maxRes = 0;
        // 遍历每一行，更新高度，并计算更新最大面积
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < heights.length; j++) {
                if (matrix[i][j] == '1') {
                    heights[j] += 1;
                }
                else {
                    heights[j] = 0;
                }
            }
            maxRes = Math.max(maxRes, largestRectangle(heights));
        }
        return maxRes;
    }
    private int largestRectangle(int[] heights) {
        Stack<Integer> increSt = new Stack<>();
        // 单调递增的栈，存的是长方形高度的坐标
        int[] newHeights = new int[heights.length + 1];

        for (int i = 0; i < heights.length; i++) {
            newHeights[i] = heights[i];
        }
        
        // 添加0在末尾，确保最后一个高度也会被记下来
        newHeights[heights.length] = 0;
        int maxArea = 0;
        for (int i = 0; i < newHeights.length; i++) {
            // 遇到栈顶元素高于当前元素时，就要取出栈顶元素处理，一直到没有比当前元素更高的
            // 因为取出的元素到当前数字i的位置，中间所有的长方形高度一定是比取出的元素高的
            // 也就是从下一个要取出的元素坐标之后到i坐标之前，当前取出的元素高度一定是最低的
            // （因为是递增栈，所以下一个要取出来的一定比现在取出来的低）
            // 这样的话，面积则就一定是取出的元素高度乘以（下一个要取出的元素坐标之后到i的距离）
            while (!increSt.isEmpty() && newHeights[increSt.peek()] > newHeights[i]) {
                int prevHeightStartIdx = increSt.pop();
                int currArea;
                // 如果这时候st空了，说明在i之前没有比现在取出来的元素更低的元素
                // 这种时候，面积就是刚刚拿出来的元素高度 * 到i坐标的距离
                if (increSt.isEmpty()) {
                    currArea = newHeights[prevHeightStartIdx] * i;
                }
                // 否则面积就是取出的元素高度乘以（下一个要取出的元素坐标之后到i的距离）
                else {
                    currArea = newHeights[prevHeightStartIdx] * (i - 1 - increSt.peek());
                }
                maxArea = Math.max(currArea, maxArea);
            }
            // 确认没有比当前元素更高的在stack里后，把当前元素加入stack，确保高度递增
            increSt.push(i);
        }
        return maxArea;
    }
}
// @lc code=end

