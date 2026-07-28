/*
 * @lc app=leetcode id=623 lang=java
 *
 * [623] Add One Row to Tree
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
    public TreeNode addOneRow(TreeNode root, int v, int d) {
        Queue<TreeNode> nodeQueue = new LinkedList<>();
        if (root == null) {
            return root;
        }
        if (d == 1) {
            TreeNode newRoot = new TreeNode(v);
            newRoot.left = root;
            return newRoot;
        }
        nodeQueue.add(root);
        while (!nodeQueue.isEmpty() && d > 1) {
            int currLevelNum = nodeQueue.size();
            d--;
            while (currLevelNum-- > 0) {
                TreeNode currNode = nodeQueue.poll();
                if (d == 1) {
                    TreeNode newLeftNode = new TreeNode(v);
                    newLeftNode.left = currNode.left;
                    currNode.left = newLeftNode;
                    TreeNode newRightNode = new TreeNode(v);
                    newRightNode.right = currNode.right;
                    currNode.right = newRightNode;
                }
                if (currNode.left != null) {
                    nodeQueue.add(currNode.left);
                }
                if (currNode.right != null) {
                    nodeQueue.add(currNode.right);
                }
            }
        }
        return root;
    }
}
// @lc code=end

