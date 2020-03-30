import java.util.ArrayList;
import java.util.List;

/*
 * @lc app=leetcode id=230 lang=java
 *
 * [230] Kth Smallest Element in a BST
 */

// @lc code=start
/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode(int x) { val = x; }
 * }
 */
class Solution {
    public int kthSmallest(TreeNode root, int k) {
        List<TreeNode> sortedList = new ArrayList<TreeNode>();
        // 利用in order travelsal遍历bst
        // 这样的结果是一个sorted的list
        inOrderTraversalBST(root, sortedList);
        return sortedList.get(k-1).val;
    }
    private void inOrderTraversalBST(TreeNode root, List<TreeNode> sortedList) {
        if (root != null) {
            inOrderTraversalBST(root.left, sortedList);
            sortedList.add(root);
            inOrderTraversalBST(root.right, sortedList);
        }
    }
}
// @lc code=end

