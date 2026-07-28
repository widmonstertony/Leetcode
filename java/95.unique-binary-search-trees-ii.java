/*
 * @lc app=leetcode id=95 lang=java
 *
 * [95] Unique Binary Search Trees II
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
    public List<TreeNode> generateTrees(int n) {
        if (n < 1) {
            return new ArrayList<TreeNode>();
        }
        // 优化，记忆数组，保存计算过的中间结果，从而避免重复计算
        // dp[i][j]表示在区间 [i, j] 范围内可以生成的所有 BST 的根结点
        List<TreeNode>[][] dp = new ArrayList[n][n];
        return generateTreesHelper(1, n, dp);
    }
    private List<TreeNode> generateTreesHelper(int start, int end, List<TreeNode>[][] dp) {
        List<TreeNode> resList = new ArrayList<>();
        if (start > end) {
            resList.add(null);
            return resList;
        }
        // 如果当前情况已经被算过，直接返回之前的结果
        if(dp[start - 1][end - 1] != null)  {
            return dp[start - 1][end - 1];
        }
        // 将区间 [1, n] 当作一个整体
        // 然后需要将其中的每个数字都当作根结点
        for (int i = start; i <= end; i++) {
            // 其划分开了左右两个子区间，然后分别调用递归函数，会得到两个结点数组
            List<TreeNode> leftNodes = generateTreesHelper(start, i - 1, dp);
            List<TreeNode> rightNodes = generateTreesHelper(i + 1, end, dp);
            // 从这两个数组中每次各取一个结点，当作当前根结点的左右子结点
            // 然后将根结点加入结果 res中
            for (TreeNode leftNode : leftNodes) {
                for (TreeNode rightNode : rightNodes) {
                    TreeNode newRoot = new TreeNode(i);
                    newRoot.left = leftNode;
                    newRoot.right = rightNode;
                    resList.add(newRoot);
                }
            }
        }
        dp[start - 1][end - 1] = resList;
        return resList;
    }
}
// @lc code=end

