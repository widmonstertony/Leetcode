/*
 * @lc app=leetcode id=113 lang=java
 *
 * [113] Path Sum II
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
    // DFS + Backtracking 
    public List<List<Integer>> pathSum(TreeNode root, int sum) {
        List<List<Integer>> resList = new ArrayList<>();
        dfsPathSum(root, sum, resList, new ArrayList());
        return resList;
    }
    private void dfsPathSum(TreeNode root, int sum, List<List<Integer>> resList, List<Integer> currList) {
        
        if (root == null) {
            return;
        }

        // 先把当前的node放进路径结果里
        currList.add(root.val);

        // 如果当前是leave 并且是正确路径，保存答案
        if (root.left == null && root.right == null) {
            if (root.val == sum) {
                resList.add(new ArrayList<>(currList));
            }
        }
        // 否则dfs找它的左右子节点的路径
        else {
            dfsPathSum(root.left, sum - root.val, resList, currList);
            dfsPathSum(root.right, sum - root.val, resList, currList);
        }

        //把当前node从路径里移除，还原之前的样子
        currList.remove(currList.size() - 1);
    }
}
// @lc code=end

