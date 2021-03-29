/*
 * @lc app=leetcode id=360 lang=java
 *
 * [360] Sort Transformed Array
 */

// @lc code=start
class Solution {
    public int[] sortTransformedArray(int[] nums, int a, int b, int c) {
        int[] res = new int[nums.length];
        int left = 0, right = nums.length - 1;
        // 根据a来判断抛物线是开口向上还说向下
        int currIdx = a > 0 ? nums.length - 1 : 0;
        while (left <= right) {
            int leftNum = f(a, b, c, nums[left]), rightNum = f(a, b, c, nums[right]);
            // 如果a是正数，开口向上，两边的数大于中间的数
            if (a > 0) {
                if (leftNum > rightNum) {
                    res[currIdx] = f(a, b, c, nums[left]);
                    left++;
                }
                else {
                    res[currIdx] = f(a, b, c, nums[right]);
                    right--;
                }
                currIdx--;
            }
            else {
                if (leftNum < rightNum) {
                    res[currIdx] = f(a, b, c, nums[left]);
                    left++;
                }
                else {
                    res[currIdx] = f(a, b, c, nums[right]);
                    right--;
                }
                currIdx++;
            }
        }
        return res;
    }
    private int f(int a, int b, int c, int x) {
        return a * x * x + b * x + c;
    }
}
// @lc code=end

