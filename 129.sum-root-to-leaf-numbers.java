/*
 * @lc app=leetcode id=129 lang=java
 *
 * [129] Sum Root to Leaf Numbers
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
    int sum = 0;
    public int sumNumbers(TreeNode root) {
        dfsSumLeaves(root, 0);
        return sum;
    }
    // dfs遍历到叶子的话，就把当前的数字加进sum里
    private void dfsSumLeaves(TreeNode root, int currNum) {
        if (root == null) {
            return;
        }
        if (root.left == null && root.right == null) {
            sum += currNum * 10 + root.val;
        }

        dfsSumLeaves(root.left, currNum * 10 + root.val);
        dfsSumLeaves(root.right, currNum * 10 + root.val);

    }
}
// @lc code=end

