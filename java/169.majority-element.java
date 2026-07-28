/*
 * @lc app=leetcode id=169 lang=java
 *
 * [169] Majority Element
 */

// @lc code=start
class Solution {
    public int majorityElement(int[] nums) {
        int res = 0, cnt = 0;
        for (int num : nums) {
            if (cnt == 0) {
                res = num;
                cnt++;
            }
            else {
                if (num == res) {
                    cnt++;
                }
                else {
                    cnt--;
                }
            }
        }
        return res;
    }
    // 二分法，O(nlogn)
    // public int majorityElement(int[] nums) {
    //     return helper(nums, 0, nums.length - 1);
    // }
    // private int helper(int[] nums, int start, int end) {
    //     if (start >= end) {
    //         return nums[start];
    //     }
    //     // 把当前nums以start为头，以end为尾平均分成两半
    //     int mid = start + (end - start) / 2;
    //     // 各找到一半里出现次数大于1/2的数字
    //     // 注意这里start配的是mid，因为mid最小等于start，而mid是included
    //     // 如果写成start, mid - 1就会卡住
    //     int leftMax = helper(nums, start, mid);
    //     int rightMax = helper(nums, mid + 1, end);

    //     if (leftMax == rightMax) {
    //         return leftMax;
    //     }
    //     // 整个从start到end出现次数大于1/2的数只会出现在这两个之中
    //     // 各遍历一遍start到end，找出出现次数更多的那一个数
    //     // 注意这里不是分一半遍历，因为不知道当前数有没有出现在另一半
    //     // 所以都遍历一遍找出出现次数更多的那一个就好
    //     int leftCount = 0;
    //     for (int i = start; i <= end; i++) {
    //         if (nums[i] == leftMax) {
    //             leftCount++;
    //         }
    //     }
    //     int rightCount = 0;
    //     for (int i = start; i <= end; i++) {
    //         if (nums[i] == rightMax) {
    //             rightCount++;
    //         }
    //     }
    //     if (leftCount > rightCount) {
    //         return leftMax;
    //     }
    //     return rightMax;
    // }
}
// @lc code=end

