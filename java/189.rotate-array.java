/*
 * @lc app=leetcode id=189 lang=java
 *
 * [189] Rotate Array
 */

// @lc code=start
class Solution {
    public void rotate(int[] nums, int k) {
        if (nums == null || nums.length == 0) {
            return;
        }
        int startIdx = 0, preNum = 0, currIdx = 0, currNum = nums[0];
        for (int i = 0; i < nums.length; i++) {
            preNum = currNum;
            // 每一次，当前位置都跳到下一个k的位置，直到跳回一开始的位置
            currIdx = (currIdx + k) % nums.length;
            // 把下一个k的位置上的数换成当前位置上的数
            // 并保存下下一个位置上的数
            currNum = nums[currIdx];
            nums[currIdx] = preNum;
            // 如果跳回到一开始的位置了，则一开始的位置往右移一位，继续跳
            if (currIdx == startIdx) {
                currIdx = ++startIdx;
                if (currIdx >= nums.length) {
                    break;
                }
                currNum = nums[currIdx];
            }
        }
    }
}
// @lc code=end

