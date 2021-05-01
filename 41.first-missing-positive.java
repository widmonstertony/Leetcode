/*
 * @lc app=leetcode id=41 lang=java
 *
 * [41] First Missing Positive
 */

// @lc code=start
class Solution {
    public int firstMissingPositive(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            int correctIdx = nums[i] - 1;
            // 确保当前数字在nums中
            // 如果当前坐标和数字不匹配，则一直交换到正确位置
            while (correctIdx >= 0 && correctIdx < nums.length &&
                    nums[i] != nums[correctIdx]) {
                swap(i, correctIdx, nums);
                correctIdx = nums[i] - 1;
            }
        }
        // 把位置和数字不匹配的第一个数字返回回去
        for (int i = 0; i < nums.length; i++) {
            if (i != nums[i] - 1) {
                return i + 1;
            }
        }
        return nums.length + 1;
    }
    private void swap(int a, int b, int[] nums) {
        int tmp = nums[a];
        nums[a] = nums[b];
        nums[b] = tmp;
    }
}
// @lc code=end

