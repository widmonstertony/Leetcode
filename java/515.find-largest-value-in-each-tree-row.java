import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.tree.TreeNode;

/*
 * @lc app=leetcode id=515 lang=java
 *
 * [515] Find Largest Value in Each Tree Row
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
    // level order traversal
    public List<Integer> largestValues(TreeNode root) {
        List<Integer> resList = new ArrayList<>();
        if (root == null) {
            return resList;
        }
        Queue<TreeNode> nodeQueue = new LinkedList<>();
        nodeQueue.add(root);
        while (!nodeQueue.isEmpty()) {
            int currLevelSize = nodeQueue.size();
            int currMax = Integer.MIN_VALUE;
            while (currLevelSize-- > 0) {
                TreeNode currNode = nodeQueue.poll();
                currMax = Math.max(currMax, currNode.val);
                if (currNode.left != null) {
                    nodeQueue.add(currNode.left);
                }
                if (currNode.right != null) {
                    nodeQueue.add(currNode.right);
                }
            }
            resList.add(currMax);
        }
        return resList;
    }
}
// @lc code=end

