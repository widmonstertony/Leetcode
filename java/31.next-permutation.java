/*
 * @lc app=leetcode id=31 lang=java
 *
 * [31] Next Permutation
 */

// @lc code=start
class Solution {
    public void nextPermutation(int[] nums) {
        for (int i = nums.length - 2; i >= 0; i--) {
            // 从后往前找，找到第一个不是降序的数字，也就是当前数字比后一位小
            if (nums[i] < nums[i + 1]) {
                // 接下来再从后往前，找第一个大于当前数字的数字
                for (int j = nums.length - 1; j > i; j--) {
                    if (nums[i] < nums[j]) {
                        // 交换这两个数的位置
                        swap(nums, i, j);
                        // 并把当前数字后面的所有数字顺序颠倒
                        reverse(nums, i + 1, nums.length - 1);
                        return;
                    }
                }
            }
        }
        // 否则颠倒整个数组
        reverse(nums, 0, nums.length - 1);
    }

    private void reverse(int[] nums, int i, int j) {
        while (i < j) {
            swap(nums, i, j);
            i++;
            j--;
        }
    }

    private void swap(int[] nums, int i, int j) {
        int tmp = nums[i];
        nums[i] = nums[j];
        nums[j] = tmp;
    }
}
// @lc code=end

