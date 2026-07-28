import javax.swing.tree.TreeNode;

/*
 * @lc app=leetcode id=285 lang=java
 *
 * [285] Inorder Successor in BST
 */

// @lc code=start
/**
 * Definition for a binary tree node. public class TreeNode { int val; TreeNode
 * left; TreeNode right; TreeNode(int x) { val = x; } }
 */
class Solution {
	public TreeNode inorderSuccessor(TreeNode root, TreeNode p) {
		TreeNode res = null;
		TreeNode currNode = root;
		while (currNode != null) {
			// 如果当前node大于p节点值
			// 那么有可能当前节点就是p的successor
			// 或者左子树中的某个节点才是
			if (currNode.val > p.val) {
				// 先把当前node赋值给res
				// 这样当当前node为空时，res指向的就是p的successor
				res = currNode;
				currNode = currNode.left;
			}
			// 否则当前节点移到右子节点
			else {
				currNode = currNode.right;
			}
		}
		return res;
	}
	// inorder实现方法，迭代的方式
	// public TreeNode inorderSuccessor(TreeNode root, TreeNode p) {
	// 	Stack<TreeNode> st = new Stack<>();
	// 	boolean hasFound = false;
	// 	TreeNode currNode = root;
	// 	while (currNode != null || !st.isEmpty()) {
	// 		// 先把所有的左边的node全放进stack
	// 		while (currNode != null) {
	// 			st.add(currNode);
	// 			currNode = currNode.left;
	// 		}
	// 		// 然后处理stack的第一个
	// 		currNode = st.pop();
	// 		if (hasFound) {
	// 			return currNode;
	// 		}
	// 		if (currNode == p) {
	// 			hasFound = true;
	// 		}
	// 		// 然后移动到右边的node
	// 		currNode = currNode.right;
	// 	}
	// 	return null;
	// }
	// 最基础的inorder实现方法，递归
	// public TreeNode inorderSuccessor(TreeNode root, TreeNode p) {
	// // res[0] 代表 当前node之前的那个node，res[1] 代表答案
	// TreeNode [] res = new TreeNode[2];
	// inorder(root, p, res);
	// return res[1];
	// }
	// private void inorder(TreeNode root, TreeNode p, TreeNode[] res) {
	// if (root == null) {
	// return;
	// }
	// inorder(root.left, p, res);
	// if (res[0] == p) {
	// res[1] = root;
	// }
	// res[0] = root;
	// inorder(root.right, p, res);
	// }
}
// @lc code=end
