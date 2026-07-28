/*
 * @lc app=leetcode id=742 lang=java
 *
 * [742] Closest Leaf in a Binary Tree
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
    public int findClosestLeaf(TreeNode root, int k) {
        Map<TreeNode, List<TreeNode>> graph = new HashMap();
        // 先用dfs建立子node到父node的反向连接
        dfsTraversal(graph, root, null);
        
        // 然后再从k的node开始bfs遍历，找到第一个到达leaf的node
        Queue<TreeNode> nodeQueue = new LinkedList();
        // 需要一个set来记录已经遍历过的node用于跳过
        Set<TreeNode> visitedNodes = new HashSet();
        for (TreeNode currNode : graph.keySet()) {
            if (currNode != null && currNode.val == k) {
                nodeQueue.add(currNode);
                visitedNodes.add(currNode);
            }
        }
        
        while(!nodeQueue.isEmpty()) {
            TreeNode currNode = nodeQueue.poll();
            if (currNode != null) {
                List<TreeNode> currList = graph.get(currNode);
                if (currList.size() <= 1) {
                    return currNode.val;
                }
                for (TreeNode nextNode: currList) {
                    if (!visitedNodes.contains(nextNode)) {
                        visitedNodes.add(nextNode);
                        nodeQueue.add(nextNode);
                    }
                }
            }
        }
        throw null;
    }
    private void dfsTraversal(Map<TreeNode, List<TreeNode>> graph, TreeNode currNode, TreeNode parentNode) {
        if (currNode == null) {
            return;
        }
        if (!graph.containsKey(currNode)) {
            graph.put(currNode, new LinkedList<TreeNode>());
        }
        if (!graph.containsKey(parentNode)) {
            graph.put(parentNode, new LinkedList<TreeNode>());
        }
        // 重点是把当前节点和父结点连起来，这样可以从当前节点到父结点
        graph.get(currNode).add(parentNode);
        graph.get(parentNode).add(currNode);
        dfsTraversal(graph, currNode.left, currNode);
        dfsTraversal(graph, currNode.right, currNode);
    }
}
// @lc code=end

