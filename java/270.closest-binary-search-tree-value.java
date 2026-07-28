/*
 * @lc app=leetcode id=270 lang=java
 *
 * [270] Closest Binary Search Tree Value
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
    public int closestValue(TreeNode root, double target) {
        if (root == null) {
            return 0;
        }
        int res = root.val;
        TreeNode nextNode = null;
        // 找到下一个可能更接近target的node，递归查找试试
        if (root.val > target && root.left != null) {
            nextNode = root.left;
        }
        if (root.val < target && root.right != null) {
            nextNode = root.right;
        }
        if (nextNode != null) {
            int nextRes = closestValue(nextNode, target);
            if (Math.abs(nextRes - target) < Math.abs(res - target)) {
                res = nextRes;
            }
        }
        return res;
    }
}
// @lc code=end

