/*
 * @lc app=leetcode id=563 lang=java
 *
 * [563] Binary Tree Tilt
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
    int mTotalTilt = 0;
    public int findTilt(TreeNode root) {
        findSum(root);
        return mTotalTilt;
    }
    private int findSum(TreeNode root) {
        if (root == null) {
            return 0;
        }
        
        int leftSum = findSum(root.left);
        int rightSum = findSum(root.right);
        mTotalTilt += Math.abs(leftSum - rightSum);

        return leftSum + rightSum + root.val;
    }
    // public int findTilt(TreeNode root) {
        // if (root == null) {
        //     return 0;
        // }
        // int leftTilt = findTilt(root.left);
        // int rightTilt = findTilt(root.right);
        // return Math.abs(findSum(root.left) - findSum(root.right)) + leftTilt + rightTilt;
    // }
    // private int findSum(TreeNode root) {
    //     if (root == null) {
    //         return 0;
    //     }
    //     return root.val + findSum(root.left) + findSum(root.right);
    // }
}
// @lc code=end

