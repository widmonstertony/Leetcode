/*
 * @lc app=leetcode id=665 lang=java
 *
 * [665] Non-decreasing Array
 */

// @lc code=start
class Solution {
    public boolean checkPossibility(int[] nums) {
        // 用prevNum代替stack
        int decreCnt = 0, prevNum = nums[0];
        for (int i = 1; i < nums.length; i++) {
            int currNum = nums[i];
            // 如果当前数字小于前面那位
            if (currNum < prevNum) {
                if (decreCnt == 1) {
                    return false;
                }
                // 如果当前数字小于前面那位，但是大于前面的前面那位
                // 也就是 1 3 2
                // 那么就要把前面那位变成当前数字
                if (i == 1 || nums[i - 2] <= nums[i]) {
                    prevNum = nums[i];
                }
                // 否则就要把当前数字变成前面那位
                // 也就是prevNum保持不变
                // 这样下一个循环，prevNum就是对的

                decreCnt++;
            }
            else {
                prevNum = nums[i];
            }
        }
        return true;
    }
    // Stack
    // public boolean checkPossibility(int[] nums) {
    //     int decreCnt = 0;
    //     List<Integer> increSt = new ArrayList<Integer>();
    //     increSt.add(nums[0]);
    //     for (int i = 1; i < nums.length; i++) {
    //         int currNum = nums[i];
    //         // 如果当前数字小于前面那位
    //         if (currNum < increSt.get(increSt.size() - 1)) {
    //             if (decreCnt == 1) {
    //                 return false;
    //             }
    //             // 如果当前数字小于前面那位，但是大于前面的前面那位
    //             // 也就是 1 3 2
    //             // 那么就要把前面那位变成当前数字
    //             if (increSt.size() == 1 || increSt.get(i - 2) <= currNum) {
    //                 increSt.remove(increSt.size() - 1);
    //                 increSt.add(currNum);
    //                 increSt.add(currNum);
    //             }
    //             // 否则就要把当前数字变成前面那位
    //             else {
    //                 increSt.add(nums[i - 1]);
    //             }
    //             decreCnt++;
    //         }
    //         else {
    //             increSt.add(currNum);
    //         }
    //     }
    //     return true;
    // }
}
// @lc code=end

