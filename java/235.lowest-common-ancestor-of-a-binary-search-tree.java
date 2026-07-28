/*
 * @lc app=leetcode id=235 lang=java
 *
 * [235] Lowest Common Ancestor of a Binary Search Tree
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
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null) {
            return root;
        }
        // p一定不大于q
        if (p.val > q.val) {
            return lowestCommonAncestor(root, q, p);
        }
        if (p.val <= root.val && root.val <= q.val) {
            return root;
        }
        // 如果最小的p比root小，说明p在左边，但是q也不在右边，说明p和q都在左边
        // 也就说明他们的lowest common ancestor也在左边
        if (p.val < root.val) {
            return lowestCommonAncestor(root.left, p, q);
        }
        else {
            return lowestCommonAncestor(root.right, p, q);
        }
    }
}
// @lc code=end

