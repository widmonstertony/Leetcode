import java.util.ArrayList;
import java.util.List;

/*
 * @lc app=leetcode id=94 lang=java
 *
 * [94] Binary Tree Inorder Traversal
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
    // recursive递归方法 
    // public List<Integer> inorderTraversal(TreeNode root) {
    //     List<Integer> resList = new ArrayList<Integer>();
    //     inorderTraversalHelper(root, resList);
    //     return resList;
    // }
    // private void inorderTraversalHelper(TreeNode root, List<Integer> resList) {
    //     if (root == null) {
    //         return;
    //     }
    //     inorderTraversalHelper(root.left, resList);
    //     resList.add(root.val);
    //     inorderTraversalHelper(root.right, resList);
    // }
    
    // iterative迭代方法
    // 从根节点开始，先将根节点压入栈，然后再将其所有左子结点压入栈
    // 然后取出栈顶节点，保存节点值，再将当前指针移到其右子节点上
    // 若存在右子节点，则在下次循环时又可将其所有左子结点压入栈中。这样就保证了访问顺序为左-根-右
    // public List<Integer> inorderTraversal(TreeNode root) {
    //     List<Integer> resList = new ArrayList<Integer>();
    //     Stack<TreeNode> mStack = new Stack<TreeNode>();
    //     // 把所有的left包括root放入stack
    //     while (root != null) {
    //         mStack.add(root);
    //         root = root.left;
    //     }
    //     while(!mStack.isEmpty()) {
    //         TreeNode curr = mStack.pop();
    //         // 当前的值存下来
    //         resList.add(curr.val);
    //         // 在继续下一个loop往上移一层之前
    //         // 要先确认当前node有没有右孩子
    //         // 如果有，则找到右孩子的最左下面的孩子
    //         // 并且把所有的右孩子的left包括右孩子全部放进stack
    //         // 这样下次循环就会从右边下面的左边开始
    //         if (curr.right != null) {
    //             curr = curr.right;
    //             while (curr != null) {
    //                 mStack.add(curr);
    //                 curr = curr.left;
    //             }
    //         }
    //     }
    //     return resList;
    // }
    
    // Morris Traversal Morris遍历
    // pre指针的目的就是为了找到前驱节点
    // 也就是inorder遍历顺序下当前节点之前的那个节点
    // 然后把它和当前节点连起来
    // 遍历到当前节点处理完后，再还原
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> resList = new ArrayList<Integer>();
        TreeNode curr = root, pre;
        while (curr != null) {
            // 如果 curr 没有左子结点
            // 打印出 curr 的值
            // 将 curr 指针指向其右子节点
            if (curr.left == null) {
                resList.add(curr.val);
                curr = curr.right;
            }
            else {
                // 将 pre 指针指向 curr 的左子树中的最右子节点
                pre = curr.left;
                while (pre.right != null && pre.right != curr) {
                    pre = pre.right;
                }
                // 若 pre 不存在右子节点
                // 说明现在的pre没有右边
                // 也就是说明pre之后要遍历的那个node就是curr，而且pre还没被打印过
                // 同时也说明现在在输出curr之前
                // 需要去curr左边把里面的先遍历完
                // 那么将其pre右子节点指回curr
                // curr指向其左子节点，先把左边的访问完
                if (pre.right == null) {
                    pre.right = curr;
                    curr = curr.left;
                }
                // 如果pre的右子节点就是当前节点
                // 说明curr之前那个pre已经打印过了
                // 将pre的右子节点清空
                // 打印curr的值
                // 将curr指向其右子节点
                else {
                    pre.right = null;
                    resList.add(curr.val);
                    curr = curr.right;
                }
            }
        }
        return resList;
    }
}
// @lc code=end

