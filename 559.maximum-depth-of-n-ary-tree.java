/*
 * @lc app=leetcode id=559 lang=java
 *
 * [559] Maximum Depth of N-ary Tree
 */

// @lc code=start
/*
// Definition for a Node.
class Node {
    public int val;
    public List<Node> children;

    public Node() {}

    public Node(int _val) {
        val = _val;
    }

    public Node(int _val, List<Node> _children) {
        val = _val;
        children = _children;
    }
};
*/

class Solution {
    public int maxDepth(Node root) {
        if (root == null) {
            return 0;
        }
        int maxRes = 1;
        
        // 从子节点里找到最深的
        for (Node child : root.children) {
            maxRes = Math.max(maxDepth(child) + 1, maxRes);
        }
        return maxRes;
    }
}
// @lc code=end

