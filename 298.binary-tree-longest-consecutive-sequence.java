/*
 * @lc app=leetcode id=298 lang=java
 *
 * [298] Binary Tree Longest Consecutive Sequence
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
    public int longestConsecutive(TreeNode root) {
        int[] res = new int[1];
        res[0] = 0;
        longest(root, null, res, 0);
        return res[0];
    }
    private void longest(TreeNode currNode, TreeNode preNode, int[] res, int currCnt) {
        if (currNode == null) {
            res[0] = Math.max(currCnt, res[0]);
        }
        else if(preNode == null || preNode.val + 1 == currNode.val) {
            currCnt++;
            longest(currNode.left, currNode, res, currCnt);
            longest(currNode.right, currNode, res, currCnt);
        }
        else {
            res[0] = Math.max(currCnt, res[0]);
            res[0] = Math.max(res[0], longestConsecutive(currNode));
        }
    }
}
// @lc code=end

