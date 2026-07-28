import java.util.ArrayList;

/*
 * @lc app=leetcode id=437 lang=java
 *
 * [437] Path Sum III
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
    public int pathSum(TreeNode root, int sum) {
        if (root == null) {
            return 0;
        }
        return sumUp(root, 0, sum) + pathSum(root.left, sum) + pathSum(root.right, sum);
    }
    private int sumUp(TreeNode root, int currSum, int sum) {
        if (root == null) {
            return 0;
        }
        currSum += root.val;
        if (currSum == sum) {
            return 1 + sumUp(root.left, currSum, sum) + sumUp(root.right, currSum, sum); 
        }
        else {
            return sumUp(root.left, currSum, sum) + sumUp(root.right, currSum, sum); 
        }
    }
    // int mSum = 0;
    // public int pathSum(TreeNode root, int sum) {
    //     List<TreeNode> outNodeList = new ArrayList<>();
    //     helper(root, sum, 0, outNodeList);
    //     return mSum;
    // }
    // // 先序遍历二叉树，对于每一个节点都有记录了一条从根节点到当前节点到路径
    // // 同时用一个变量 curSum 记录路径节点总和
    // private void helper(TreeNode root, int sum, int currSum, List<TreeNode> outNodeList) {
    //     if (root == null) {
    //         return;
    //     }
    //     currSum += root.val;
    //     outNodeList.add(root);
    //     if (currSum == sum) {
    //         mSum++;
    //     }
    //     // 继续查看子路径和有没有满足题意的，做法就是每次去掉一个节点
    //     // 看路径和是否等于给定值，注意最后必须留一个节点，不能全去掉了
    //     // 因为如果全去掉了，路径之和为0，而如果给定值刚好为0的话就会有问题
    //     int curr = currSum;
    //     for (int i = 0; i < outNodeList.size() - 1; i++) {
    //         TreeNode node = outNodeList.get(i);
    //         curr -= node.val;
    //         if (curr == sum) {
    //             mSum++;
    //         }
    //     }
    //     helper(root.left, sum, currSum, outNodeList);
    //     helper(root.right, sum, currSum, outNodeList);
    //     outNodeList.remove(outNodeList.size() - 1);
    // }

    // 自己第一次写的
    // int mSum = 0;
    // public int pathSum(TreeNode root, int sum) {
    //     if (root == null) {
    //         return mSum;
    //     }
    //     helpPathSum(root, sum);
    //     pathSum(root.left, sum);
    //     pathSum(root.right, sum);
    //     return mSum;
    // }
    // private void helpPathSum(TreeNode root, int sum) {
    //     if (root == null) {
    //         return;
    //     }
    //     if (root.val == sum) {
    //         mSum++;
    //     }
    //     helpPathSum(root.left, sum - root.val);
    //     helpPathSum(root.right, sum - root.val);
    // }
}
// @lc code=end

