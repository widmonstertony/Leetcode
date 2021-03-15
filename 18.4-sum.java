import java.util.ArrayList;

/*
 * @lc app=leetcode id=18 lang=java
 *
 * [18] 4Sum
 */

// @lc code=start
class Solution {
    public List<List<Integer>> fourSum(int[] nums, int target) {
        Arrays.sort(nums);
        List<List<Integer>> resList = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            // 记得去重
            if (i > 0 && nums[i] == nums[i - 1]) {
                continue;
            }
            else {
                findThreeSum(nums, i, target-nums[i], resList);
            }
        }
        return resList;
    }
    private void findThreeSum(int[] nums, int start, int target, List<List<Integer>> resList) {
        List<List<Integer>> currList = new ArrayList<>();
        for (int i = start + 1; i < nums.length; i++) {
            // 记得去重
            if (i > start + 1 && nums[i] == nums[i - 1]) {
                continue;
            }
            else {
                findTwoSum(nums, i, target - nums[i], currList);
            }
        }
        for (List<Integer> eachList : currList) {
            eachList.add(nums[start]);
            resList.add(eachList);
        }
    }
    private void findTwoSum(int[] nums, int start, int target, List<List<Integer>> resList) {
        // 双指针，一个从start开始往后，一个从末尾开始往前
        int left = start + 1, right = nums.length - 1;
        while (left < right) {
            int currSum = nums[left] + nums[right];
            if (currSum == target) {
                List<Integer> currList = new ArrayList<>();
                currList.add(nums[start]);
                currList.add(nums[left]);
                currList.add(nums[right]);
                resList.add(currList);
                left++;
                right--;
                // 记得去重，同时不能超过对方的位置
                while (nums[left] == nums[left - 1] && left < right) {
                    left++;
                }
                while (nums[right] == nums[right + 1] && right > left) {
                    right--;
                }
            }
            else if (currSum < target) {
                left++;
            }
            else {
                right--;
            }
        }
    }
}
// @lc code=end

