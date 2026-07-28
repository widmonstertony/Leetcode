/*
 * @lc app=leetcode id=114 lang=java
 *
 * [114] Flatten Binary Tree to Linked List
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
    // Iterative解法 Space O(1)
    public void flatten(TreeNode root) {
        if (root == null) {
            return;
        }
        while (root != null) {
            if (root.left != null) {
                TreeNode leftTail = root.left;
                while (leftTail.right != null) {
                    leftTail = leftTail.right;
                }
                leftTail.right = root.right;
                root.right = root.left;
                root.left = null;
            }
            root = root.right;
        }
    }
    // public void flatten(TreeNode root) {
    //     flattenTree(root);
    // }
    // private TreeNode flattenTree(TreeNode root) {
    //     if (root == null) {
    //         return root;
    //     }
    //     if (root.left == null && root.right == null) {
    //         return root;
    //     }
    //     TreeNode leftTail = flattenTree(root.left);
    //     TreeNode rightTail = flattenTree(root.right);

    //     if (leftTail != null) {
    //         leftTail.right = root.right;
    //         root.right = root.left;
    //         root.left = null;
    //     }
        
    //     if (rightTail != null) {
    //         return rightTail;
    //     }
    //     else {
    //         return leftTail;
    //     }
    // }
    // public void flatten(TreeNode root) {
    //     if (root == null) {
    //         return;
    //     }
    //     if (root.left == null && root.right == null) {
    //         return;
    //     }
    //     flatten(root.left);
    //     flatten(root.right);

    //     TreeNode preRightNode = root.right;

    //     root.right = root.left;
    //     root.left = null;

    //     // Find Left child Tail
    //     while (root.right != null) {
    //         root = root.right;
    //     }
    //     root.right = preRightNode;
    // }
}
// @lc code=end

