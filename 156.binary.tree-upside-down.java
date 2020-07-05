/*
 * @lc app=leetcode id=156 lang=java
 *
 * [156] Binary Tree Upside Down
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

    public TreeNode upsideDownBinaryTree(TreeNode root) {
        // 需要把之前的顶部的节点和顶部右节点保存一下，因为它该去的地方现在已经被连上了
        // 只有在下一次遍历左节点时才能把它加上去
        TreeNode currTopNode = root, preNode = null, nextTopNode = null, preTopRightNode = null;
        while (currTopNode != null) {
            // 颠倒当前Top，同时保存当前右节点给下次用
            nextTopNode = currTopNode.left;
            currTopNode.left = preTopRightNode;
            preTopRightNode = currTopNode.right;
            currTopNode.right = preNode;
            
            // 移动到下一个左节点
            preNode = currTopNode;
            currTopNode = nextTopNode;
        }
        return preNode;
    }
    //     dfs解法
    //     public TreeNode upsideDownBinaryTree(TreeNode root) {
//         if (root == null || root.left == null) {
//             return root;
//         }
//         TreeNode leftNode = root.left, rightNode = root.right;
        
//         // 先把root的左节点颠倒过来
//         TreeNode resNode = upsideDownBinaryTree(leftNode);
        
//         // 把右节点接到左节点上
//         leftNode.left = rightNode;
//         // 把root接到右节点
//         leftNode.right = root;
//         // 清空root的子节点们
//         root.left = null;
//         root.right = null;
//         return resNode;
//     }
}
// @lc code=end

