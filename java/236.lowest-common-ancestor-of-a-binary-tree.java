import javax.swing.tree.TreeNode;

import apple.laf.JRSUIUtils.Tree;

/*
 * @lc app=leetcode id=236 lang=java
 *
 * [236] Lowest Common Ancestor of a Binary Tree
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
        if (root == null || p == root || q == root) {
            return root;
        }

        // 若p和q分别位于左右子树中，那么对左右子结点调用递归函数，会分别返回p和q结点的位置
        // 而当前结点正好就是p和q的最小共同父结点，直接返回当前结点即可，这就是题目中的例子1的情况。

        // 若p和q同时位于左子树，这里有两种情况，一种情况是 left 会返回p和q中较高的那个位置
        // 而 right 会返回空，所以最终返回非空的 left 即可，这就是题目中的例子2的情况
        // 还有一种情况是会返回p和q的最小父结点，就是说当前结点的左子树中的某个结点才是p和q的最小父结点，会被返回。

        // 若p和q同时位于右子树，同样这里有两种情况，一种情况是 right 会返回p和q中较高的那个位置
        // 而 left 会返回空，所以最终返回非空的 right 即可，还有一种情况是会返回p和q的最小父结点
        // 就是说当前结点的右子树中的某个结点才是p和q的最小父结点，会被返回
        TreeNode leftLCA = lowestCommonAncestor(root.left, p, q);

        // 当p和q同时为于左子树或右子树中而且返回的结点并不是p或q
        // 那么就是p和q的最小父结点了
        if (leftLCA != null && leftLCA != p && leftLCA != q) {
            return leftLCA;
        }

        TreeNode rightLCA = lowestCommonAncestor(root.right, p, q);
        if (leftLCA != null && rightLCA != null) {
            return root;
        }
        if (leftLCA != null) {
            return leftLCA;
        }
        else {
            return rightLCA;
        }
    }
}
// @lc code=end

