/*
 * @lc app=leetcode id=333 lang=java
 *
 * [333] Largest BST Subtree
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
    public int largestBSTSubtree(TreeNode root) {
        if (root == null) {
            return 0;
        }
        if (isValidBST(root, Long.MAX_VALUE, Long.MIN_VALUE)) {
            return cntNodesNum(root);
        }
        return Math.max(largestBSTSubtree(root.left), largestBSTSubtree(root.right));
    }
    private boolean isValidBST(TreeNode root, long maxValue, long minValue) {
        if (root == null) {
            return true;
        }
        if (root.val >= maxValue || root.val <= minValue) {
            return false;
        }
        return isValidBST(root.left, root.val, minValue) && isValidBST(root.right, maxValue, root.val);
    }
    private int cntNodesNum(TreeNode root) {
        if (root == null) {
            return 0;
        }
        return cntNodesNum(root.left) + 1 + cntNodesNum(root.right);
    }
}
// @lc code=end

