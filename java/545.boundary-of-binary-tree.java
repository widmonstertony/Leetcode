/*
 * @lc app=leetcode id=545 lang=java
 *
 * [545] Boundary of Binary Tree
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
    public List<Integer> boundaryOfBinaryTree(TreeNode root) {
        List<Integer> resList = new ArrayList<>();
        // 先把root放进res
        if (!isLeaf(root)) {
            resList.add(root.val);
        }
        // 再把root左边的boundary都放进res
        TreeNode currNode = root.left;
        while (currNode != null) {
            if (!isLeaf(currNode)) {
                resList.add(currNode.val);
            }
            if (currNode.left != null) {
                currNode = currNode.left;
            }
            else {
                currNode = currNode.right;
            }
        }
        // 再把所有下面的叶子按照左右顺序放进res
        addLeaves(root, resList);

        // 再把右边的boundary放进res，因为需要反过来，所以用stack
        Stack<Integer> rightBoundarySt = new Stack<>();
        currNode = root.right;
        while (currNode != null) {
            if (!isLeaf(currNode)) {
                rightBoundarySt.push(currNode.val);
            }
            if (currNode.right != null) {
                currNode = currNode.right;
            }
            else {
                currNode = currNode.left;
            }
        }
        while (!rightBoundarySt.isEmpty()) {
            resList.add(rightBoundarySt.pop());
        }
        return resList;
    }
    private boolean isLeaf(TreeNode root) {
        return root != null && root.left == null && root.right == null;
    }
    private void addLeaves(TreeNode root, List<Integer> resList) {
        if (isLeaf(root)) {
            resList.add(root.val);
        }
        else {
            if (root.left != null) {
                addLeaves(root.left, resList);
            }
            if (root.right != null) {
                addLeaves(root.right, resList);
            }
        }
    }
}
// @lc code=end

