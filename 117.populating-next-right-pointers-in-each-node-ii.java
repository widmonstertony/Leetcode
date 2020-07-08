/*
 * @lc app=leetcode id=117 lang=java
 *
 * [117] Populating Next Right Pointers in Each Node II
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
    // 这里由于子树有可能残缺，故需要平行扫描父节点同层的节点，找到他们的左右子节点
    // recursive 方法
    public Node connect(Node root) {
        if (root == null) {
            return root;
        }
        if (root.left != null) {
            if (root.right != null) {
                root.left.next = root.right;
            }
            else {
                Node nextHasChildNode = root.next;
                if (nextHasChildNode != null) {
                    while (nextHasChildNode.next != null && nextHasChildNode.left == null && nextHasChildNode.right == null) {
                        nextHasChildNode = nextHasChildNode.next;
                    }
                    if (nextHasChildNode.left != null) {
                        root.left.next = nextHasChildNode.left;
                    }
                    else if (nextHasChildNode.right != null) {
                        root.left.next = nextHasChildNode.right;
                    }
                } 
            }
        }
        if (root.right != null) {
            Node nextHasChildNode = root.next;
            if (nextHasChildNode != null) {
                while (nextHasChildNode.next != null && nextHasChildNode.left == null && nextHasChildNode.right == null) {
                    nextHasChildNode = nextHasChildNode.next;
                }
                if (nextHasChildNode.left != null) {
                    root.right.next = nextHasChildNode.left;
                }
                else if (nextHasChildNode.right != null) {
                    root.right.next = nextHasChildNode.right;
                }
            }
        }
        connect(root.right);
        connect(root.left);
        return root;
    }

    // // 这里由于子树有可能残缺，故需要平行扫描父节点同层的节点，找到他们的左右子节点
    // // constant space O(1)，runtime 接近O(N)
    // public Node connect(Node root) {
    //     if (root == null) {
    //         return root;
    //     }
    //     Node currNode = null, startNode = root;
    //     while (startNode != null) {
    //         currNode = startNode;
    //         while (currNode != null) {
    //             if (currNode.left != null) {
    //                 if (currNode.right != null) {
    //                     currNode.left.next = currNode.right;
    //                 }
    //                 else {
    //                     // 首先找到下一个有子节点的Node
    //                     // 再把它的子节点和当前node的左子节点连起来
    //                     Node nextHasChildNode = currNode.next;
    //                     if (nextHasChildNode != null) {
    //                         while (nextHasChildNode.next != null && nextHasChildNode.left == null && nextHasChildNode.right == null) {
    //                             nextHasChildNode = nextHasChildNode.next;
    //                         }
    //                         if (nextHasChildNode.left != null) {
    //                             currNode.left.next = nextHasChildNode.left;
    //                         }
    //                         else if (nextHasChildNode.right != null) {
    //                             currNode.left.next = nextHasChildNode.right;
    //                         }
    //                     }
    //                 }
    //             }
    //             if (currNode.right != null) {
    //                 // 首先找到下一个有子节点的Node
    //                 // 再把它的子节点和当前node的右子节点连起来
    //                 Node nextHasChildNode = currNode.next;
    //                 if (nextHasChildNode != null) {
    //                     while (nextHasChildNode.next != null && nextHasChildNode.left == null && nextHasChildNode.right == null) {
    //                         nextHasChildNode = nextHasChildNode.next;
    //                     }
    //                     if (nextHasChildNode.left != null) {
    //                         currNode.right.next = nextHasChildNode.left;
    //                     }
    //                     else if (nextHasChildNode.right != null) {
    //                         currNode.right.next = nextHasChildNode.right;
    //                     }
    //                 }
    //             }
    //             currNode = currNode.next;
    //         }
    //         // 找到下一个startNode，必须是有子节点并且和当前startNode在同一层的node
    //         while (startNode.next != null && startNode.left == null && startNode.right == null) {
    //             startNode = startNode.next;
    //         }
    //         if (startNode.left != null) {
    //             startNode = startNode.left;
    //         }
    //         else {
    //             startNode = startNode.right;
    //         }
    //     }
    //     return root;
    // }
}
// @lc code=end

