/*
 * @lc app=leetcode id=617 lang=java
 *
 * [617] Merge Two Binary Trees
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
    public TreeNode mergeTrees(TreeNode t1, TreeNode t2) {
        if (t1 == null && t2 == null) {
            return null;
        }
        if (t1 == null) {
            return t2;
        }
        else if (t2 == null) {
            return t1;
        }
        t1.val += t2.val;
        TreeNode leftNode = mergeTrees(t1.left, t2.left);
        TreeNode rightNode = mergeTrees(t1.right, t2.right);
        t1.left = leftNode;
        t1.right = rightNode;
        t2.left = null;
        t2.right = null;
        return t1;
    }
}
// @lc code=end

