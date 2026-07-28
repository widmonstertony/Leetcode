import java.util.ArrayList;
import java.util.List;

/*
 * @lc app=leetcode id=257 lang=java
 *
 * [257] Binary Tree Paths
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
    public List<String> binaryTreePaths(TreeNode root) {
        List<String> resList = new ArrayList<>();
        dfsFindPathToLeaves(resList, "", root);
        return resList;
    }
    private void dfsFindPathToLeaves(List<String> resList, String currPath, TreeNode root) {
        // 如果root不是空
        if (root != null) {
            // 如果当前root就是leave，则把path结果和当前值加进最终结果
            if (root.left == null && root.right == null) {
                resList.add(currPath + root.val + "");
                return;
            }
            // 否则把当前值和->一起加进当前的path，说明后面还有值
            else {
                currPath = currPath + root.val + "->";
                // dfs找到左右子节点的path
                dfsFindPathToLeaves(resList, currPath, root.left);
                dfsFindPathToLeaves(resList, currPath, root.right);
            }
        }
    }
    
}
// @lc code=end

