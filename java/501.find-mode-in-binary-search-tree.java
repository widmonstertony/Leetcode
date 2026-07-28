import java.util.ArrayList;

/*
 * @lc app=leetcode id=501 lang=java
 *
 * [501] Find Mode in Binary Search Tree
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
    class WrapInteger {
        int mNum;
        public WrapInteger(int i) {
            this.setNumber(i);
        }
        public void setNumber(int i) {
            mNum = i;
        }
        public int getNumber() {
            return mNum;
        }
    }
    class WrapNode {
        TreeNode mNode;
        public WrapNode(TreeNode i) {
            this.setNode(i);
        }
        public void setNode(TreeNode i) {
            mNode = i;
        }
        public TreeNode getNode() {
            return mNode;
        }
    }
    // 利用inorder traversal
    // 因为BST的特性，inorder是从小到大的顺序
    public int[] findMode(TreeNode root) {
        ArrayList<Integer> resList = new ArrayList<>();
        WrapInteger currMax = new WrapInteger(0), currFreq = new WrapInteger(1);
        WrapNode preNode = new WrapNode(null);
        inorderTraversal(root, preNode, currMax, currFreq, resList);
        int[] resArr = new int[resList.size()];
        for (int i = 0; i < resList.size(); i++) {
            resArr[i] = resList.get(i);
        }
        return resArr;
    }
    private void inorderTraversal(TreeNode root, WrapNode preNode, WrapInteger currMax, WrapInteger currFreq, ArrayList<Integer> resList) {
        if (root == null) {
            return;
        }
        inorderTraversal(root.left, preNode, currMax, currFreq, resList);

        // 如果之前的node和当前node一样，说明当前数字的frequence需要加一
        // 否则从1开始重新记数
        if (preNode.getNode() != null) {
            if (root.val == preNode.getNode().val) {
                currFreq.setNumber(currFreq.getNumber() + 1);
            }
            else {
                currFreq.setNumber(1);
            }
        }

        // 如果当前数字的频率大于等于最大值
        if (currFreq.getNumber() >= currMax.getNumber()) {
            // 如果当前数字的频率大于最大值
            // 则说明之前的数字都不对，清空答案
            if (currFreq.getNumber() > currMax.getNumber()) {
                resList.clear();
            }
            resList.add(root.val);
            currMax.setNumber(currFreq.getNumber());
        }

        // 把当前root记为preNode
        preNode.setNode(root);
        inorderTraversal(root.right, preNode, currMax, currFreq, resList);
    }
}
// @lc code=end

