import java.util.ArrayList;
import java.util.Stack;

import javax.swing.tree.TreeNode;

/*
 * @lc app=leetcode id=144 lang=java
 *
 * [144] Binary Tree Preorder Traversal
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
    // dfs traversal方式
    // public List<Integer> preorderTraversal(TreeNode root) {
    //     List<Integer> resList = new ArrayList<>();
    //     preorderTraversal(root, resList);
    //     return resList;
    // }
    // private void preorderTraversal(TreeNode root, List<Integer> resList) {
    //     if (root == null) {
    //         return;
    //     }
    //     resList.add(root.val);
    //     preorderTraversal(root.left, resList);
    //     preorderTraversal(root.right, resList);
    // }
    // iterative方法
    public List<Integer> preorderTraversal(TreeNode root) {
        List<Integer> resList = new ArrayList<Integer>();
        Stack<TreeNode> nodeStack = new Stack<>();
        if (root == null) {
            return resList;
        }
        nodeStack.add(root);
        while (!nodeStack.isEmpty()) {
            TreeNode currNode = nodeStack.pop();
            resList.add(currNode.val);
            // 因为preOrder是根-左-右，Stack是后进先出
            // 所以先把右节点加进stack再把左边加进去，这样下次就会先遍历左边再右边
            if (currNode.right != null) {
                nodeStack.add(currNode.right);
            }
            if (currNode.left != null) {
                nodeStack.add(currNode.left);
            }
        }
        return resList;
    }
}
// @lc code=end

