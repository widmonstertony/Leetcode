/*
 * @lc app=leetcode id=611 lang=java
 *
 * [611] Valid Triangle Number
 */

// @lc code=start
class Solution {
    public int triangleNumber(int[] nums) {
        int res = 0, n = nums.length;
        Arrays.sort(nums);
        for (int i = n - 1; i >= 2; i--) {
            // 将left指向首数字，将right之前遍历到的数字的前面一个数字
            int left = 0, right = i - 1;
            // 如果left小于right就进行循环
            while (left < right) {
                // 如果left指向的数加上right指向的数大于当前的数字的话
                // 那么right到left之间的数字都可以组成三角形
                // 相当于此时确定了i和right的位置，可以将left向右移到right的位置
                // 中间经过的数都大于left指向的数，所以都能组成三角形
                if (nums[left] + nums[right] > nums[i]) {
                    res += right - left;
                    // 把right往前移，找下一个确定的right
                    right--;
                }
                else {
                    left++;
                }
            }
        }
        return res;
    }
    // 二分法
    // public int triangleNumber(int[] nums) {
    //     int res = 0, n = nums.length;
    //     Arrays.sort(nums);
    //     for (int i = 0; i < n; i++) {
    //         for (int j = i + 1; j < n; j++) {
    //             // 二分法确定第三个数字的范围
    //             // 找到比sum大或者等于的数的位置，之前的全都可以
    //             int sum = nums[i] + nums[j];
    //             int left = j + 1, right = n - 1;
    //             while (left <= right) {
    //                 int mid = left + (right - left) / 2;
    //                 if (nums[mid] < sum) {
    //                     left = mid + 1;
    //                 }
    //                 else {
    //                     right = mid - 1;
    //                 }
    //             }
    //             res += left - 1 - j;
    //         }
    //     }
    //     return res;
    // }
    // 暴力解，不太行
    // public int triangleNumber(int[] nums) {
    //     Arrays.sort(nums);
    //     List<Integer> triList = new ArrayList<>();
    //     int[] res = new int[1];
    //     helper(0, nums, res, triList);
    //     return res[0];
    // }
    // private void helper(int start, int[] nums, int[] res, List<Integer> triList) {
    //     if (triList.size() == 3) {
    //         if (triList.get(2) < triList.get(0) + triList.get(1)) {
    //             res[0]++;
    //         }
    //         return;
    //     }
    //     if (start >= nums.length) {
    //         return;
    //     }
    //     for (int i = start; i < nums.length; i++) {
    //         triList.add(nums[i]);
    //         helper(i + 1, nums, res, triList);
    //         triList.remove(triList.size() - 1);
    //     }
    // }
}
// @lc code=end

