/*
 * @lc app=leetcode id=897 lang=java
 *
 * [897] Increasing Order Search Tree
 */

// @lc code=start
/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode() {}
 *     TreeNode(int val) { this.val = val; }
 *     TreeNode(int val, TreeNode left, TreeNode right) {
 *         this.val = val;
 *         this.left = left;
 *         this.right = right;
 *     }
 * }
 */
class Solution {
    public TreeNode increasingBST(TreeNode root) {
        if (root == null) {
            return root;
        }
        // 先把左边的node变成一条线，并且拿到这个线的最左上角的node
        TreeNode leftTop = increasingBST(root.left);
        // 如果左边的线存在顶部，则需要把线的尾端和root连接上
        if (leftTop != null) {
            // 找到线的尾端
            TreeNode leftTail = leftTop;
            while (leftTail.right != null) {
                leftTail = leftTail.right;
            }
            // 把尾端和root连上，并且把root的左node清空
            leftTail.right = root;
            root.left = null;
        }
        // 再把右边的node变成一条线，并且把root和这条线的最左上角的node连接
        TreeNode rightTop = increasingBST(root.right);
        root.right = rightTop;
        return leftTop == null ? root : leftTop;
    }
}
// @lc code=end

