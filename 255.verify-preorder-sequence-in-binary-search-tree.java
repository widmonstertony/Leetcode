/*
 * @lc app=leetcode id=255 lang=java
 *
 * [255] Verify Preorder Sequence in Binary Search Tree
 */

// @lc code=start
class Solution {
    public boolean verifyPreorder(int[] preorder) {
        // 根据二叉搜索树的性质，当前节点的值一定大于其左子树中任何一个节点值
        // 而且其右子树中的任何一个节点值都不能小于当前节点值
        // 先设一个最小值 low
        int lowerBound = Integer.MIN_VALUE;
        Stack<Integer> mStack = new Stack<>();
        // 然后遍历数组
        for (int currNum : preorder) {
            // 如果当前值小于这个最小值 low，返回 false
            // 因为low代表着左边的最小值，而如果后面的值比low更小
            // 就不应该在右边而应该在左边，但就不应该比low位置还靠后，就代表当前的不valid
            if (currNum < lowerBound) {
                return false;
            }
            // 如果遇到的数字比栈顶元素小，说明是其左子树的点，直接压入栈中
            // 直到遇到的数字比栈顶元素大，那么就是右边的值了
            // 需要找到是哪个节点的右子树，所以更新 low 值并删掉栈顶元素
            // 然后继续和下一个栈顶元素比较，如果还是大于，则继续更新 low 值和删掉栈顶
            // 直到栈为空或者当前栈顶元素大于当前值停止，再把当前值压入栈中
            while (!mStack.isEmpty() && currNum > mStack.peek()) {
                lowerBound = mStack.peek();
                mStack.pop();
            }
            mStack.push(currNum);
        }
        return true;
    }
}
// @lc code=end

