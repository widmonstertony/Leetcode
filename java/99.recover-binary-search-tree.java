import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * @lc app=leetcode id=99 lang=java
 *
 * [99] Recover Binary Search Tree
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
    // Recursive方法
    // public void recoverTree(TreeNode root) {
    //     List<TreeNode> nodesList = new ArrayList<>();
    //     List<Integer> valueList = new ArrayList<>();
    //     inOrderTraversal(root, nodesList, valueList);
    //     Collections.sort(valueList);
    //     for (int i = 0; i < nodesList.size(); i++) {
    //         nodesList.get(i).val = valueList.get(i);
    //     }
    // }
    // private void inOrderTraversal(TreeNode root, List<TreeNode> nodesList, List<Integer> valueList) {
    //     if (root == null || nodesList == null || valueList == null) {
    //         return;
    //     }
    //     inOrderTraversal(root.left, nodesList, valueList);
    //     nodesList.add(root);
    //     valueList.add(root.val);
    //     inOrderTraversal(root.right, nodesList, valueList);
    // }
    
    // Iterative方法 空间O(1)
    // 利用inorder traversal一定是从小到大的顺序
    // 先找到第一个数，再一直更新第二个数找到最终那个错的
    public void recoverTree(TreeNode root) {
        // parent记下之前访问的那个node
        // first, second记下要交换的值
        TreeNode first = null, second = null, parent = null;
        TreeNode curr = root, pre;
        while (curr != null) {
            if (curr.left == null) {
                // 如果parent的值大于当前值
                // 说明curr是第二个需要交换的值
                // 如果第一个要换的值还没找到，那么parent就是第一个
                // 一直更新second找到最后那个最大的值，也就是需要交换位置的值
                if (parent != null && parent.val > curr.val) {
                    if (first == null) {
                        first = parent;
                    }
                    second = curr;
                }
                parent = curr;
                curr = curr.right;
            }
            else {
                pre = curr.left;
                while (pre.right != null && pre.right != curr) {
                    pre = pre.right;
                }
                if (pre.right == null) {
                    pre.right = curr;
                    curr = curr.left;
                }
                else {
                    pre.right = null;
                    if (parent != null && parent.val > curr.val) {
                        if (first == null) {
                            first = parent;
                        }
                        second = curr;
                    }
                    parent = curr;
                    curr = curr.right;
                }
            }
        }
        int tmp = first.val;
        first.val = second.val;
        second.val = tmp;
    }
}
// @lc code=end

