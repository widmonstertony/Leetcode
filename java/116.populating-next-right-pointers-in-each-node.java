import java.util.Queue;
/*
 * @lc app=leetcode id=116 lang=java
 *
 * [116] Populating Next Right Pointers in Each Node
 */

// @lc code=start
/*
// Definition for a Node.
class Node {
    public int val;
    public Node left;
    public Node right;
    public Node next;

    public Node() {}
    
    public Node(int _val) {
        val = _val;
    }

    public Node(int _val, Node _left, Node _right, Node _next) {
        val = _val;
        left = _left;
        right = _right;
        next = _next;
    }
};
*/

class Solution {
    //  perfect binary tree, 用startnode来一直指到最左边的那个node
    public Node connect(Node root) {
        if (root == null) {
            return root;
        }
        Node startNode = root, currNode = null;
        while (startNode.left != null) {
            currNode = startNode;
            // 把currNode的左子节点的next连到右子节点
            // 再把右子节点连到当前currNode的next的左节点
            // 再移动到currNode的next节点
            while (currNode != null) {
                currNode.left.next = currNode.right;
                if (currNode.next != null) {
                    currNode.right.next = currNode.next.left;
                }
                currNode = currNode.next;
            }
            startNode = startNode.left;
        }
        return root;
    }

    // // DFS解法
    // public Node connect(Node root) {
    //     if (root == null) {
    //         return root;
    //     }
    //     // 左子节点的next是右节点
    //     if (root.left != null) {
    //         root.left.next = root.right;
    //     }
    //     // 右子节点的next是下一个next的左子节点
    //     if (root.right != null) {
    //         if (root.next != null) {
    //             root.right.next = root.next.left;
    //         }
    //     }
    //     connect(root.left);
    //     connect(root.right);
    //     return root;
    // }

    // // Level Traversal
    // public Node connect(Node root) {
    //     if (root == null) {
    //         return root;
    //     }
    //     Queue<Node> nodeQueue = new LinkedList<>();
    //     nodeQueue.offer(root);
    //     while (!nodeQueue.isEmpty()) {
    //         int currLevelSize = nodeQueue.size();
    //         while (currLevelSize-- > 0) {
    //             Node currNode = nodeQueue.poll();
    //             if (currLevelSize != 0) {
    //                 currNode.next = nodeQueue.peek();
    //             }
    //             if (currNode.left != null) {
    //                 nodeQueue.add(currNode.left);
    //             }
    //             if (currNode.right != null) {
    //                 nodeQueue.add(currNode.right);
    //             }
    //         }
    //     }
    //     return root;
    // }
}
// @lc code=end

