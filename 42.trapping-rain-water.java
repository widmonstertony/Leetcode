import java.util.Stack;

/*
 * @lc app=leetcode id=42 lang=java
 *
 * [42] Trapping Rain Water
 */

// @lc code=start
class Solution {
    public int trap(int[] height) {
        if (height == null || height.length == 0) {
            return 0;
        }
        // 单调递减的Stack，代表水坑的左边，一旦遇到大于stack最低的，说明就可能可以形成水坑
        Stack<Integer> decreBarHeightSt = new Stack<>();
        int res = 0;

        for (int i = 0; i < height.length; i++) {

            // 一直处理可能有坑的部分
            // 也就是当前高度大于stack里的最低点
            while (!decreBarHeightSt.isEmpty() && height[i] > height[decreBarHeightSt.peek()]) {
                // 一个水坑的中间坑的坐标，也就是stack的最低点
                int midBarIdx = decreBarHeightSt.pop();

                // 如果此时没有下一个高的bar，则不能形成坑
                if (decreBarHeightSt.isEmpty()) {
                    continue;
                }

                // 否则下一个高的bar就是水坑的左边界
                int leftBarIdx = decreBarHeightSt.peek();
                int rightBarIdx = i;

                // 左右边界中间的距离，需要多减1取中间部分
                int currAreaWidth = rightBarIdx - 1 - leftBarIdx;
                // 左右边界，取其中较小的值为装水的边界，然后此高度减去水槽最低点的高度，就是当前水坑的高度
                int currAreaHeight = Math.min(height[rightBarIdx], height[leftBarIdx]) - height[midBarIdx];
                res += currAreaWidth * currAreaHeight;
            }

            // 处理完后，再把当前的bar坐标加进去
            decreBarHeightSt.add(i);
        }

        return res;
    }
}
// @lc code=end

