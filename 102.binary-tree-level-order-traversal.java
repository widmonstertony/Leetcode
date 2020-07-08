import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.swing.tree.TreeNode;

/*
 * @lc app=leetcode id=102 lang=java
 *
 * [102] Binary Tree Level Order Traversal
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
    // BFS Level Traversal
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> resList = new ArrayList<>();
        Queue<TreeNode> queue = new LinkedList<>();
        if (root == null) {
            return resList;
        }
        queue.offer(root);
        while (!queue.isEmpty()) {
            List<Integer> currLevelNodes = new ArrayList<>();
            // 当前queue所有的node便是这一层所有的node的数量
            int currLevelNum = queue.size();
            while (currLevelNum > 0) {
                TreeNode currNode = queue.poll();
                currLevelNodes.add(currNode.val);
                if (currNode.left != null) {
                    queue.offer(currNode.left);
                }
                if (currNode.right != null) {
                    queue.offer(currNode.right);
                }
                currLevelNum--;
            }
            resList.add(currLevelNodes);
        }
        return resList;
    }
    // public List<List<Integer>> levelOrder(TreeNode root) {
    //     List<List<Integer>> resList = new ArrayList<>();
    //     Queue<TreeNode> queue = new LinkedList<>();
    //     if (root == null) {
    //         return resList;
    //     }
    //     queue.offer(root);
    //     int currLevelNum = 1;
    //     while (!queue.isEmpty()) {
    //         List<Integer> currLevelNodes = new ArrayList<>();
    //         int nextLevelNum = 0;
    //         while (currLevelNum > 0) {
    //             TreeNode currNode = queue.poll();
    //             currLevelNodes.add(currNode.val);
    //             if (currNode.left != null) {
    //                 queue.offer(currNode.left);
    //                 nextLevelNum++;
    //             }
    //             if (currNode.right != null) {
    //                 queue.offer(currNode.right);
    //                 nextLevelNum++;
    //             }
    //             currLevelNum--;
    //         }
    //         resList.add(currLevelNodes);
    //         currLevelNum = nextLevelNum;
    //     }
    //     return resList;
    // }
    // dfs 遍历
    // public List<List<Integer>> levelOrder(TreeNode root) {
    //     List<List<Integer>> resList = new ArrayList<>();
    //     dfsLevelTraversal(root, 0, resList);
    //     return resList;
    // }
    // private void dfsLevelTraversal(TreeNode root, int level, List<List<Integer>> resList) {
    //     if (root == null) {
    //         return;
    //     }
    //     if (resList.size() <= level || resList.get(level) == null) {
    //         resList.add(level, new ArrayList<>());
    //     }
    //     resList.get(level).add(root.val);
    //     dfsLevelTraversal(root.left, level + 1, resList);
    //     dfsLevelTraversal(root.right, level + 1, resList);
    // }
}
// @lc code=end

