import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
/*
 * @lc app=leetcode id=314 lang=java
 *
 * [314] Binary Tree Vertical Order Traversal

 */
import java.util.Map;
import java.util.Queue;

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
    // 层序遍历, BFS，这样才可以知道column数是左还是右
    public List<List<Integer>> verticalOrder(TreeNode root) {
        
        List<List<Integer>> resList = new ArrayList<>();
        
        if (root == null) {
            return resList;
        }
        
        // TreeNode确保输出时是按大小排列的
        // 或者用minColumn和maxColumn来记录最小值和最大值，省时间
        Map<Integer, List<Integer>> hashTableRes = new HashMap<>();
        // bfs遍历Tree，并按照列的数字放进TreeMap
        Queue<Pair> queue = new LinkedList<>();
        queue.offer(new Pair(root, 0));
        int minColumn = Integer.MAX_VALUE, maxColumn = Integer.MIN_VALUE;
        while (!queue.isEmpty()) {
            Pair currPair = queue.poll();
            TreeNode currNode = currPair.getNode();
            int currCol = currPair.getCol();
            
            if (!hashTableRes.containsKey(currCol)) {
                hashTableRes.put(currCol, new ArrayList<Integer>());
            }
            hashTableRes.get(currCol).add(currNode.val);

            // 更新最大最小列数
            minColumn = Math.min(minColumn, currCol);
            maxColumn = Math.max(maxColumn, currCol);

            // 把左右子分别按照列数放进queue
            if (currNode.left != null) {
                queue.offer(new Pair(currNode.left, currCol - 1));
            }
            if (currNode.right != null) {
                queue.offer(new Pair(currNode.right, currCol + 1));
            }
        }

        for (int i = minColumn; i <= maxColumn; i++) {
            resList.add(hashTableRes.get(i));
        }
        return resList;
    }
    class Pair{
        TreeNode mNode;
        int mColumn;
        public Pair(TreeNode node,int column){
            this.mNode = node;
            this.mColumn = column;
        }
        public int getCol() {
            return this.mColumn;
        }
        public TreeNode getNode() {
            return this.mNode;
        }
    }
}
// @lc code=end

