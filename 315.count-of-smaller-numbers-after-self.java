import java.util.ArrayList;
import java.util.List;

/*
 * @lc app=leetcode id=315 lang=java
 *
 * [315] Count of Smaller Numbers After Self
 */

// @lc code=start
class Solution {
    // Binary Index Tree, TLE，sad
    // class TreeNode {
    //     int val;
    //     int count;
    //     int duplicates;

    //     TreeNode left;
    //     TreeNode right;
    //     TreeNode(int val) {
    //         this.val = val;
    //         this.count = 0;
    //         this.duplicates = 1;
    //     }
    // }
    // public List<Integer> countSmaller(int[] nums) {
    //     int[] resArr = new int[nums.length];
    //     TreeNode root = null;

    //     for (int i = nums.length - 1; i >= 0; i--) {
    //         root = insert(nums[i], root, resArr, i, 0);
    //     }
    //     List<Integer> resList = new ArrayList<>();
    //     for (int i = 0; i < resArr.length; i++) {
    //         resList.add(resArr[i]);
    //     }
    //     return resList;
    // }
    // private TreeNode insert(int currNum, TreeNode currNode, int[] resArr, int idx, int preSum) {
    //     if (currNode == null) {
    //         currNode = new TreeNode(currNum);
    //         resArr[idx] = preSum;
    //     }
    //     else if (currNode.val == currNum) {
    //         currNode.duplicates++;
    //         resArr[idx] = preSum + currNode.count;
    //     }
    //     else if (currNode.val > currNum){
    //         currNode.count++;
    //         currNode.left = insert(currNum, currNode.left, resArr, idx, preSum);
    //     }
    //     else {
    //         currNode.right = insert(currNum, currNode.right, resArr, idx, preSum + currNode.duplicates + currNode.count);
    //     }
    //     return currNode;
    // }

    // 二分法找下界，找到那个第一个不小于当前数的数的位置，那么它前面的数就是都小的
    public List<Integer> countSmaller(int[] nums) {
        ArrayList<Integer> sortedNums = new ArrayList<>();
        LinkedList<Integer> res = new LinkedList<>();
        for (int i = nums.length - 1; i >= 0; i--) {
            int currNum = nums[i];
            int left = 0, right = sortedNums.size() - 1;
            while (left <= right) {
                int mid = left + (right - left) / 2;
                if (sortedNums.get(mid) < currNum) {
                    left = mid + 1;
                }
                else {
                    right = mid - 1;
                }
            }
            sortedNums.add(left, currNum);
            res.addFirst(left);
        }
        return res;
    }
}
// @lc code=end

