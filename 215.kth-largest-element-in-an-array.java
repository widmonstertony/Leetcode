/*
 * @lc app=leetcode id=215 lang=java
 *
 * [215] Kth Largest Element in an Array
 */

// @lc code=start
class Solution {
    // QuickSelect
    public int findKthLargest(int[] nums, int k) {
        Random rand = new Random();
        for (int i = 0; i < nums.length; i++) {
            int randomIndexToSwap = rand.nextInt(nums.length);
            swap(i, randomIndexToSwap, nums);
        }
        int left = 0, right = nums.length - 1;
        k--;
        while (left <= right) {
            // piovtu坐标代表了第几大，因为我们把大于piovt的放在前面
            int piovtIdx = partition(left, right, nums);
            // 如果当前坐标小于k，说明要找的数更小，这个数肯定在piovt的右边
            // 这时候就不需要去找左边了
            if (piovtIdx < k) {
                left = piovtIdx + 1;
            }
            else if (piovtIdx > k) {
                right = piovtIdx - 1;
            }
            else {
                return nums[piovtIdx];
            }
        }
        return -1;
    }

    private int partition(int left, int right, int[] nums) {
        int piovt = nums[right];
        int piovtIdx = left;
        for (int i = piovtIdx; i < right; i++) {
            // 因为要从大到小排列，所以把大于piovt的都放在前面
            if (nums[i] > piovt) {
                swap(i, piovtIdx, nums);
                piovtIdx++;
            }
        }
        swap(piovtIdx, right, nums);
        return piovtIdx;
    }

    private void swap(int i, int j, int[] nums) {
        int tmp = nums[i];
        nums[i] = nums[j];
        nums[j] = tmp;
    }
}
// @lc code=end

