import java.util.Stack;

/*
 * @lc app=leetcode id=84 lang=java
 *
 * [84] Largest Rectangle in Histogram
 */

// @lc code=start
class Solution {
    public int largestRectangleArea(int[] heights) {
        int res = 0;
        //单调递增的栈，存的是长方形的坐标
        Stack<Integer> increSt = new Stack<>();

        int[] newHeights = new int[heights.length + 1];

        for (int i = 0; i < heights.length; i++) {
            newHeights[i] = heights[i];
        }
        
        // 添加0在末尾，确保最后一个高度也会被记下来
        newHeights[heights.length] = 0;

        for (int i = 0; i < heights.length + 1; i++) {
            
            // 遇到栈顶元素高于当前元素时，就要取出栈顶元素处理，一直到没有比当前元素更高的
            // 因为取出的元素到当前数字i的位置，中间所有的长方形高度一定是比取出的元素高的
            // 也就是从下一个要取出的元素坐标之后到i坐标之前，当前取出的元素高度一定是最低的
            // （因为是递增栈，所以下一个要取出来的一定比现在取出来的低）
            // 这样的话，面积则就一定是取出的元素高度乘以（下一个要取出的元素坐标之后到i的距离）
            while (!increSt.isEmpty() && newHeights[i] < newHeights[increSt.peek()]) {
                int curr = increSt.pop();
                int currArea;
                // 如果这时候st空了，说明在i之前没有比现在取出来的元素更低的元素
                // 这种时候，面积就是现在的元素高度 * 到i坐标的距离
                if (increSt.isEmpty()) {
                    currArea = newHeights[curr] * i;
                }
                // 否则面积就是取出的元素高度乘以（下一个要取出的元素坐标之后到i的距离）
                else {
                    currArea = newHeights[curr] * (i - 1 - increSt.peek());
                }
                res = Math.max(res, currArea);
            }

            // 确认没有比当前元素更高的在stack里后，把当前元素加入stack，确保高度递增
            increSt.add(i);
        }
        return res;
    }
}
// @lc code=end

