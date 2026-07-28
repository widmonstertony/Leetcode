/*
 * @lc app=leetcode id=145 lang=java
 *
 * [145] Binary Tree Postorder Traversal
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
    // public List<Integer> postorderTraversal(TreeNode root) {
    //     List<Integer> resList = new ArrayList<>();
    //     postorderTraversal(root, resList);
    //     return resList;
    // }
    // private void postorderTraversal(TreeNode root, List<Integer> resList) {
    //     if (root == null) {
    //         return;
    //     }
    //     postorderTraversal(root.left, resList);
    //     postorderTraversal(root.right, resList);
    //     resList.add(root.val);
    // }
    // iterative方法
    public List<Integer> postorderTraversal(TreeNode root) {
        List<Integer> resList = new ArrayList<Integer>();
        Stack<TreeNode> nodeStack = new Stack<>();
        if (root == null) {
            return resList;
        }
        nodeStack.add(root);
        TreeNode preNode = root;
        while (!nodeStack.isEmpty()) {
            TreeNode currNode = nodeStack.peek();
            // 如果当前node的左右子节点都没有，说明是leaf，加入resList
            // 又或者是，因为postOrder是左右根
            // 所以如果当前node是根并且之前的node是左右子节点
            // 都代表当前node的左右子节点都已经访问完了
            // 这时候再访问当前节点就好了
            if (currNode.left == null && currNode.right == null || currNode.right == preNode || currNode.left == preNode) {
                resList.add(currNode.val);
                nodeStack.pop();
                preNode = currNode;
            }
            // 因为postOrder是左-右-根，Stack是后进先出
            // 所以先把右节点加进stack再把左边加进去，这样下次就会先遍历左边再右边
            else {
                if (currNode.right != null) {
                    nodeStack.add(currNode.right);
                }
                if (currNode.left != null) {
                    nodeStack.add(currNode.left);
                }
            }
        }
        return resList;
    }
}
// @lc code=end

