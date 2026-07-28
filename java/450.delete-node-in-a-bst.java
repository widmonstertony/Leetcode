/*
 * @lc app=leetcode id=450 lang=java
 *
 * [450] Delete Node in a BST
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
    // recursive 方法
    public TreeNode deleteNode(TreeNode root, int key) {
        if (root == null) {
            return root;
        }
        // 如果当前root的值不是key，找到继续下去的方向
        if (root.val > key) {
            root.left = deleteNode(root.left, key);
        }
        else if (root.val < key) {
            root.right = deleteNode(root.right, key);
        }
        // 当前值是key的话
        else {
            // 如果当前节点有一个子结点不存在，那么直接就把当前节点指向另一个
            if (root.left == null) {
                root = root.right;
            }
            else if (root.right == null) {
                root = root.left;
            }
            // 左右子结点都存在的情况
            // 需要在右子树里找到最小值，也就是右子树中最左下方的节点
            // 然后将最小值赋给root
            // 再在右子树中删除之前这个最小值
            else {
                TreeNode currNoderight = root.right;
                while (currNoderight.left != null) {
                    currNoderight = currNoderight.left;
                }
                root.val = currNoderight.val;
                root.right = deleteNode(root.right, currNoderight.val);
            }
        }
        return root;
    }
    // public TreeNode deleteNode(TreeNode root, int key) {
    //     TreeNode currNode = root;
    //     TreeNode preNode = null;
    //     // 先找到当前需要删除的key所在位置
    //     while (currNode != null) {
    //         if (currNode.val == key) {
    //             if (preNode == null) {
    //                 return delete(root);
    //             }
    //             // 判断需要删除的节点是左还是右，然后删除当前节点
    //             if (preNode.left == currNode) {
    //                 preNode.left = delete(currNode);
    //             }
    //             else {
    //                 preNode.right = delete(currNode);
    //             }
    //             break;
    //         }
    //         preNode = currNode;
    //         if (currNode.val > key) {
    //             currNode = currNode.left;
    //         }
    //         else {
    //             currNode = currNode.right;
    //         }
    //     }
    //     return root;
    // }

    // // 删除当前节点，并返回下一个和父节点连接的节点
    // private TreeNode delete(TreeNode root) {
    //     if (root == null) {
    //         return root;
    //     }
    //     // 如果右节点为空，只需要返回左节点就好
    //     if (root.right == null) {
    //         return root.left;
    //     }
        
    //     // 将要删除结点的左子结点放到右子树的最左子结点的左子结点上
    //     TreeNode currNoderight = root.right;
    //     while (currNoderight.left != null) {
    //         currNoderight = currNoderight.left;
    //     }
    //     currNoderight.left = root.left;
    //     return root.right;
    // }
}
// @lc code=end

