/*
 * @lc app=leetcode id=238 lang=java
 *
 * [238] Product of Array Except Self
 */

// @lc code=start
class Solution {
    public int[] productExceptSelf(int[] nums) {
        int[] res = new int[nums.length];
        // 先从左边遍历一遍，把乘积保存到res里
        int currProduct = 1;
        for (int i = 0; i < nums.length; i++) {
            currProduct *= nums[i];
            res[i] = currProduct;
        }
        // 再从右边遍历一遍，把乘积用res里的继续乘起来
        currProduct = 1;
        int[] rightProduct = new int[nums.length];
        for (int i = nums.length - 1; i >= 0; i--) {
            if (i == 0) {
                res[i] = currProduct;
            }
            else if (i == nums.length - 1) {
                res[i] = res[i - 1];
            }
            else {
                res[i] = res[i - 1] * currProduct;
            }
            currProduct *= nums[i];
        }
        
        return res;
    }
    // public int[] productExceptSelf(int[] nums) {
    //     int[] res = new int[nums.length];
    //     // 先从左边遍历一遍，把乘积保存下来
    //     int[] leftProduct = new int[nums.length];
    //     int currProduct = 1;
    //     for (int i = 0; i < nums.length; i++) {
    //         currProduct *= nums[i];
    //         leftProduct[i] = currProduct;
    //     }
    //     // 再从右边遍历一遍，把乘积保存下来
    //     currProduct = 1;
    //     int[] rightProduct = new int[nums.length];
    //     for (int i = nums.length - 1; i >= 0; i--) {
    //         currProduct *= nums[i];
    //         rightProduct[i] = currProduct;
    //     }
    //     for (int i = 0; i < nums.length; i++) {
    //         if (i == 0) {
    //             res[i] = rightProduct[i + 1];
    //         }
    //         else if (i == nums.length - 1) {
    //             res[i] = leftProduct[i - 1];
    //         }
    //         else {
    //             res[i] = leftProduct[i - 1] * rightProduct[i + 1];
    //         }
    //     }
    //     return res;
    // }
}
// @lc code=end

