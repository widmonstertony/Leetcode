/*
 * @lc app=leetcode id=250 lang=java
 *
 * [250] Count Univalue Subtrees
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
    // 一次遍历，在检查nodes是否相同的过程中更新结果
    public int countUnivalSubtrees(TreeNode root) {
        int[] res = new int[1];
        allNodesAreSame(root, -1001, res);
        return res[0];
    }
    // 这个函数确认root的值是否和下面的所有nodes的值一样，并且和parentVal一样
    // 但是只需要root的左右两边的nodes值一样就可以更新结果了，并不需要把root的值放入考虑
    private boolean allNodesAreSame(TreeNode root, int parentVal, int[] res) {
        if (root == null) {
            return true;
        }
        boolean left = allNodesAreSame(root.left, root.val, res);
        boolean right = allNodesAreSame(root.right, root.val, res);
        if (!left || !right) {
            return false;
        }
        // 只要左右都是一样的nodes，则当前这个root就符合univalue要求，结果加一
        res[0]++;
        // 确认这个root的值是否等于父node的值
        return root.val == parentVal;
    }
}
// 含有大量的重复 check，不是一次遍历，但是很清晰
// class Solution {
//     public int countUnivalSubtrees(TreeNode root) {
//         if (root == null) {
//             return 0;
//         }
//         int currSum = 0;
//         if (isUnival(root, root.val)) {
//             currSum++;
//         }
//         return currSum + countUnivalSubtrees(root.left) + countUnivalSubtrees(root.right);
//     }
//     private boolean isUnival(TreeNode root, int val) {
//         if (root == null) {
//             return true;
//         }
//         return root.val == val && isUnival(root.left, val) && isUnival(root.right, val);
//     }
// }
// @lc code=end

