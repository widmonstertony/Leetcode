/*
 * @lc app=leetcode id=1469 lang=java
 *
 * [1469] Find All The Lonely Nodes
 */

// @lc code=start
class Solution {
    public List<Integer> getLonelyNodes(TreeNode root) {
        List<Integer> resList = new ArrayList<>();
        helper(resList, root, null);
        return resList;
    }
    private void helper(List<Integer> resList, TreeNode root, TreeNode rootParent) {
        if (root == null) {
            return;
        }
        // XOR操作，两个条件不相同时则为真
        if (rootParent != null && (rootParent.left == null ^ rootParent.right == null)) {
            resList.add(root.val);
        }
        helper(resList, root.left, root);
        helper(resList, root.right, root);
    }

}
// @lc code=end

