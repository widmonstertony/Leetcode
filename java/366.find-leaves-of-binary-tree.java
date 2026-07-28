import java.util.ArrayList;
import java.util.List;

/*
 * @lc app=leetcode id=366 lang=java
 *
 * [366] Find Leaves of Binary Tree
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
    public List<List<Integer>> findLeaves(TreeNode root) {
        List<List<Integer>> resList = new ArrayList<>();
        dfsFindLeaves(resList, root);
        return resList;
    }
    private int dfsFindLeaves(List<List<Integer>> resList, TreeNode root) {
        
        // 把空的子节点定义为-1的深度
        if (root == null) {
            return -1;
        }
        
        // 找到当前节点的深度，同时dfs把所有叶节点遍历完
        int depth = 1 + Math.max(dfsFindLeaves(resList, root.left), dfsFindLeaves(resList, root.right));
        
        // 如果当前深度没有创建过list，创建一个新的
        if (resList.size() <= depth || resList.size() > depth && resList.get(depth) == null) {
            resList.add(depth, new ArrayList<Integer>());
        }
        
        // 把当前节点的值加在正确的深度
        resList.get(depth).add(root.val);
        return depth;
    }
    // // 剥皮
    // public List<List<Integer>> findLeaves(TreeNode root) {
    //     List<List<Integer>> resList = new ArrayList<>();
        
    //     while (root != null) {
    //         List<Integer> leavesList = new ArrayList<>();
    //         root = dfsRemoveLeaves(root, leavesList);
    //         resList.add(leavesList);
    //     }
        
    //     return resList;
    // }
    
    // // return root, if root is not the leaf
    // // if root is the leaf, add root to leavesList, and return null
    // private TreeNode dfsRemoveLeaves(TreeNode root, List<Integer> leavesList) {
    //     if (root == null) {
    //         return root;
    //     }
        
    //     if (root.left == null && root.right == null) {
    //         leavesList.add(root.val);
    //         return null;
    //     }
        
    //     root.left = dfsRemoveLeaves(root.left, leavesList);
    //     root.right = dfsRemoveLeaves(root.right, leavesList);
    //     return root;
    // }
}
// @lc code=end

