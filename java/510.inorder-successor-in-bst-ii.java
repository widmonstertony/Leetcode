/*
 * @lc app=leetcode id=510 lang=java
 *
 * [510] Inorder Successor in BST II
 */

// @lc code=start
/*
// Definition for a Node.
class Node {
    public int val;
    public Node left;
    public Node right;
    public Node parent;
};
*/

class Solution {
    public Node inorderSuccessor(Node node) {
        if (node == null) {
            return node;
        }
        // 如果当前node有右节点，则后续结点一定是右节点下面的最左节点
        if (node.right != null) {
            node = node.right;
            while (node != null && node.left != null) {
                node = node.left;
            }
            return node;
        }
        // 否则要往上找，找到第一个有左节点的parent
        // 并且一定是从左边上去的而不是右边，也就是当前node会等于parent的左节点
        // 这样确保了parent一定是第一个比一开始的node大的parent node
        while (node.parent != null) {
            if (node == node.parent.left) {
                return node.parent;
            }
            node = node.parent;
        }
        // 代表没找到
        return null;
    }
}
// @lc code=end

