/*
 * @lc app=leetcode id=280 lang=java
 *
 * [280] Wiggle Sort
 */

// @lc code=start
class Solution {
    // 一次增一次减，如果下一个数不符合要求就和当前数替换
    public void wiggleSort(int[] nums) {
        boolean isDecreasing = false;
        for (int i = 0; i < nums.length - 1; i++) {
            if (isDecreasing) {
                // 因为现在是减，而当前的数小于之后的数
                // 之前的数是增加，当前的数肯定大于前面的数
                // 说明之后的数换到当前位置，肯定也会大于前面的数
                // 交换现在和之后的数的位置
                if (nums[i] < nums[i + 1]) {
                    swap(nums, i, i + 1);
                }
            }
            // 同理，因为现在是加，而当前的数大于之后的数
            // 之前的数是减少，当前的数肯定小于前面的数
            // 说明之后的数换到当前位置，肯定也会小于前面的数
            // 交换现在和之后的数的位置
            else {
                if (nums[i] > nums[i + 1]) {
                    swap(nums, i, i + 1);
                }
            }
            isDecreasing = !isDecreasing;
        }
    }
    private void swap(int[] nums, int i, int j) {
        int tmp = nums[i];
        nums[i] = nums[j];
        nums[j] = tmp;
    }
}
// @lc code=end

