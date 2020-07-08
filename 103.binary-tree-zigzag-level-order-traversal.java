import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.swing.tree.TreeNode;

/*
 * @lc app=leetcode id=103 lang=java
 *
 * [103] Binary Tree Zigzag Level Order Traversal
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
    // BFS解法 Level Traversal
    public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> resList = new ArrayList<>();
        if (root == null) {
            return resList;
        }
        LinkedList<TreeNode> nodeQueue = new LinkedList<>();
        nodeQueue.offer(root);
        boolean isFromLeftToRight = true;
        while (!nodeQueue.isEmpty()) {
            // 记录下当前level的node数量，把当前node的一级子节点全加进queue里
            int currLevelSize = nodeQueue.size();
            List<Integer> currNodeList = new ArrayList<>();
            while (currLevelSize-- > 0) {
                TreeNode currNode;
                if (isFromLeftToRight) {
                    currNode = nodeQueue.pollFirst();
                    if (currNode.left != null) {
                        nodeQueue.offer(currNode.left);
                    }
                    if (currNode.right != null) {
                        nodeQueue.offer(currNode.right);
                    }
                }
                else {
                    currNode = nodeQueue.pollLast();
                    if (currNode.right != null) {
                        nodeQueue.offerFirst(currNode.right);
                    }
                    if (currNode.left != null) {
                        nodeQueue.offerFirst(currNode.left);
                    }
                }
                currNodeList.add(currNode.val);
            }
            isFromLeftToRight = !isFromLeftToRight;
            resList.add(currNodeList);
        }
        return resList;
    }
    // DFS 解法 在level加入当前值时判断下是加在前面还是后面
    // public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
    //     List<List<Integer>> resList = new ArrayList<>();
    //     dfsTraversal(root, 0, resList);
    //     return resList;
    // }
    // private void dfsTraversal(TreeNode root, int level, List<List<Integer>> resList) {
    //     if (root == null) {
    //         return;
    //     }
    //     if (level >= resList.size() || resList.get(level) == null) {
    //         resList.add(level, new LinkedList<>());
    //     }
    //     if (level % 2 == 0) {
    //         resList.get(level).add(root.val);
    //     }
    //     else {
    //         resList.get(level).add(0, root.val);
    //     }
    //     dfsTraversal(root.left, level + 1, resList);
    //     dfsTraversal(root.right, level + 1, resList);
    // }
}
// @lc code=end

