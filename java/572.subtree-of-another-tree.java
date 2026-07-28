/*
 * @lc app=leetcode id=572 lang=java
 *
 * [572] Subtree of Another Tree
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
    public boolean isSubtree(TreeNode s, TreeNode t) {
        if (s == null) {
            return false;
        }
        if (areSameTree(s, t)) {
            return true;
        }
        return isSubtree(s.left, t) || isSubtree(s.right, t);
    }
    // 检查两个node组成的树是否完全一模一样
    private boolean areSameTree(TreeNode s, TreeNode t) {
        if (s == null && t == null) {
            return true;
        }
        if (s == null || t == null) {
            return false;
        }
        if (s.val != t.val) {
            return false;
        }
        return areSameTree(s.left, t.left) && areSameTree(s.right, t.right);
    }
    // public boolean isSubtree(TreeNode s, TreeNode t) {
    //     return isSubtree(s, t, false);
    // }
    // // mustBeSame是在一旦开始适配，则每一个node都必须相等，不然这个函数返回false
    // public boolean isSubtree(TreeNode s, TreeNode t, boolean mustBeSame) {
    //     if (t == null && s == null) {
    //         return true;
    //     }
    //     if (s == null || t == null) {
    //         return false;
    //     }
    //     if (t.val == s.val) {
    //         if (isSubtree(s.left, t.left, true) && isSubtree(s.right, t.right, true)) {
    //             return true;
    //         }
    //     }
    //     if (mustBeSame) {
    //         return false;
    //     }
    //     else {
    //         return isSubtree(s.left, t) || isSubtree(s.right, t);
    //     }
    // }
}
// @lc code=end

