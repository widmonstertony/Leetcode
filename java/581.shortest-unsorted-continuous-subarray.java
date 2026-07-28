import java.util.Arrays;

/*
 * @lc app=leetcode id=581 lang=java
 *
 * [581] Shortest Unsorted Continuous Subarray
 */

// @lc code=start
class Solution {
    public int findUnsortedSubarray(int[] nums) {
        // 初始和最后位置的坐标初始化，注意last是-2，确保在都没找到的情况下，函数会返回0
        int first = -1, last = -2;
        // 当前最小值从后往前更新
        // 当前最大值从前往后更新
        int currMin = nums[nums.length - 1], currMax = nums[0];
        for (int i = 1; i < nums.length; i++) {
            currMin = Math.min(currMin, nums[nums.length - 1 - i]);
            currMax = Math.max(currMax, nums[i]);
            // 因为当前最大值是从前往后更新的
            // 所以每一次当前最大值大于当前值，都会更新最后那个位置的坐标
            // 这也就能找到最后一次，当前最大值大于当前值的位置
            // 这也就说明，到当前位置都需要排序，因为当前值比最大值小，但是却在最大值后面
            if (currMax > nums[i]) {
                last = i;
            }
            // 同理，因为当前最小值是从后往前更新的
            // 所以每一次当前最小值小于当前值，都会更新初始的坐标
            // 这也就能找到第一次，当前最小值小于当前值的位置
            // 这也就说明，从当前位置开始需要排序，因为当前最小值比当前值小，但是却在当前值后面
            if (currMin < nums[nums.length - 1 - i]) {
                first = nums.length - 1 - i;
            }
        }
        // 最后把最后需要排序的位置和第一个需要排序的位置相减就好
        return last - first + 1;
    }
    // 暴力 先排序一次，再找到第一个和最后一个不一样的数的位置
    // public int findUnsortedSubarray(int[] nums) {
    //     int[] newNums = Arrays.copyOf(nums, nums.length);
    //     Arrays.sort(newNums);
    //     int first = -1, last = -1;
    //     for (int i = 0; i < nums.length; i++) {
    //         if (nums[i] != newNums[i]) {
    //             if (first == -1) {
    //                 first = i;
    //             }
    //             else {
    //                 last = i;
    //             }
    //         }
    //     }
    //     if (last >= 0) {
    //         return last - first + 1;
    //     }
    //     else if (first >= 0) {
    //         return nums.length - first;
    //     }
    //     else {
    //         return 0;
    //     }
    // }
}
// @lc code=end

