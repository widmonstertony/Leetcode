/*
 * @lc app=leetcode id=124 lang=java
 *
 * [124] Binary Tree Maximum Path Sum
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
    int res = Integer.MIN_VALUE;
    public int maxPathSum(TreeNode root) {
        findOneMaxPathSum(root);
        return res;
    }

    // 找到root的左右子节点里数字更大的那一条
    // 对于每个结点来说，要知道经过其左子结点的 path之和大还是经过右子节点的 path之和大
    private int findOneMaxPathSum(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int leftMax = Math.max(findOneMaxPathSum(root.left), 0);
        int rightMax = Math.max(findOneMaxPathSum(root.right), 0);
        res = Math.max(res, leftMax + rightMax + root.val);
        return Math.max(leftMax, rightMax) + root.val;
    }
}
// @lc code=end

