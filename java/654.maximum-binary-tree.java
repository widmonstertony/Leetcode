import java.util.Arrays;

import javax.swing.tree.TreeNode;

/*
 * @lc app=leetcode id=654 lang=java
 *
 * [654] Maximum Binary Tree
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
    public TreeNode constructMaximumBinaryTree(int[] nums) {
        if (nums == null || nums.length == 0) {
            return null;
        }
        int maxNum = Integer.MIN_VALUE;
        int maxIdx = -1;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] > maxNum) {
                maxNum = nums[i];
                maxIdx = i;
            }
        }
        TreeNode root = new TreeNode(maxNum);
        root.left = constructMaximumBinaryTree(Arrays.copyOfRange(nums, 0, maxIdx));
        root.right = constructMaximumBinaryTree(Arrays.copyOfRange(nums, maxIdx + 1, nums.length));
        return root;
    }
}
// @lc code=end

