/*
 * @lc app=leetcode id=783 lang=java
 *
 * [783] Minimum Distance Between BST Nodes
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
    int resMin;
    public int minDiffInBST(TreeNode root) {
        List<Integer> treeList = new ArrayList<>();
        resMin = Integer.MAX_VALUE;
        // 中序遍历BST，会是一个从小到大的数组
        inorderTraversal(root, treeList);
        return resMin;
    }
    private void inorderTraversal(TreeNode root, List<Integer> treeList) {
        if (root == null) {
            return;
        }
        inorderTraversal(root.left, treeList);
        if (treeList.size() > 0) {
            resMin = Math.min(resMin, root.val - treeList.get(treeList.size() - 1));
        }
        treeList.add(root.val);
        inorderTraversal(root.right, treeList);

    }
}
// @lc code=end

