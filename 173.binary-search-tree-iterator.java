import java.util.ArrayList;
import java.util.Stack;

import javax.swing.tree.TreeNode;

/*
 * @lc app=leetcode id=173 lang=java
 *
 * [173] Binary Search Tree Iterator
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
class BSTIterator {
    Stack<TreeNode> mNodeStack;

    public BSTIterator(TreeNode root) {
        mNodeStack = new Stack<>();
        while (root != null) {
            mNodeStack.add(root);
            root = root.left;
        }
    }

    /** @return the next smallest number */
    public int next() {
        TreeNode currNode = mNodeStack.pop();
        if (currNode.right != null) {
            TreeNode currNodeNext = currNode.right;
            while (currNodeNext != null) {
                mNodeStack.add(currNodeNext);
                currNodeNext = currNodeNext.left;
            }
        }
        return currNode.val;
    }
    
    /** @return whether we have a next smallest number */
    public boolean hasNext() {
        return !mNodeStack.isEmpty();
    }
    // // 先inorder把整个bst转变成一个arraylist
    // List<Integer> mBSTArray;
    // int currIdx;
    // public BSTIterator(TreeNode root) {
    //     mBSTArray = new ArrayList<>();
    //     Stack<TreeNode> nodeSt = new Stack<>();
    //     while (root != null) {
    //         nodeSt.add(root);
    //         root = root.left;
    //     }
    //     while (!nodeSt.isEmpty()) {
    //         TreeNode currNode = nodeSt.pop();
    //         mBSTArray.add(currNode.val);
    //         if (currNode.right != null) {
    //             TreeNode rightLeftNode = currNode.right;
    //             while (rightLeftNode != null) {
    //                 nodeSt.add(rightLeftNode);
    //                 rightLeftNode = rightLeftNode.left;
    //             }
    //         }
    //     }
    //     currIdx = -1;
    // }
    
    // /** @return the next smallest number */
    // public int next() {
    //     if (!hasNext()) {
    //         return -1;
    //     }
    //     return mBSTArray.get(++currIdx);
    // }
    
    // /** @return whether we have a next smallest number */
    // public boolean hasNext() {
    //     return currIdx + 1 < mBSTArray.size();
    // }
}

/**
 * Your BSTIterator object will be instantiated and called as such:
 * BSTIterator obj = new BSTIterator(root);
 * int param_1 = obj.next();
 * boolean param_2 = obj.hasNext();
 */
// @lc code=end

