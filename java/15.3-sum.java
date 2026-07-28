import java.util.ArrayList;

/*
 * @lc app=leetcode id=15 lang=java
 *
 * [15] 3Sum
 */

// @lc code=start
class Solution {
    public List<List<Integer>> threeSum(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> resList = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) {
                continue;
            }
            else {
                findTwoSum(nums, i, -nums[i], resList);
            }
        }
        return resList;
    }
    private void findTwoSum(int[] nums, int i, int sum, List<List<Integer>> resList) {
        // 双指针，一个从i开始往后，一个从末尾开始往前
        int first = i + 1, last = nums.length - 1;
        while (first < last) {
            int currSum = nums[first] + nums[last];
            // 如果双指针的数之和满足条件，保存当前答案
            if (currSum == sum) {
                List<Integer> currList = new ArrayList<>();
                currList.add(nums[i]);
                currList.add(nums[first]);
                currList.add(nums[last]);
                resList.add(currList);
                // 移动双指针到下一个位置
                first++;
                last--;
                // 记得去重，同时不能超过对方的位置
                while (first < last && nums[first] == nums[first - 1]) {
                    first++;
                }
                while (first < last && nums[last] == nums[last + 1]) {
                    last--;
                }
            }
            else if (currSum > sum) {
                last--;
            }
            else {
                first++;
            }
        }
    }
}
// @lc code=end

