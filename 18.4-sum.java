import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * @lc app=leetcode id=18 lang=java
 *
 * [18] 4Sum
 */

// @lc code=start
class Solution {
    // kSum解法
    public List<List<Integer>> fourSum(int[] nums, int target) {
        Arrays.sort(nums);
        return kSum(nums, target, 0, 4);
    }
    private List<List<Integer>> kSum(int[] nums, int target, int start, int k) {
        List<List<Integer>> resList = new ArrayList<>();
        if (start == nums.length) {
            return resList;
        }
        if (k == 2) {
            return twoSum(nums, target, start);
        }
        for (int i = start; i < nums.length; i++) {
            // 如果当前数字和前一位一样，跳过，去重
            if (i != start && nums[i - 1] == nums[i]) {
                continue;
            }
            int currNum = nums[i];
            // 把从下一个数字开始，所有可以组合成target减去当前数字的k-1个答案全部拿到
            List<List<Integer>> onelessKlist = kSum(nums, target - currNum, i + 1, k - 1);
            // 把这些答案里都加上当前数字，并且给到最后答案
            for (List<Integer> eachList : onelessKlist) {
                List<Integer> currResList = new ArrayList<>();
                currResList.add(currNum);
                currResList.addAll(eachList);
                resList.add(currResList);
            }
        }
        return resList;
    }
    private List<List<Integer>> twoSum(int[] nums, int target, int start) {
        List<List<Integer>> resList = new ArrayList<>();
        int left = start, right = nums.length - 1;
        while (left < right) {
            int currSum = nums[left] + nums[right];
            // 如果当前总和小于target，移动左指针
            // 或者如果左边和它之前的一样，跳过，去重
            if (currSum < target || (left > start && nums[left] == nums[left - 1])) {
                left++;
            }
            // 如果当前总和大于target，移动右指针
            // 同时如果右边和它之后的一样，跳过，去重
            else if (currSum > target || (right < nums.length - 1 && nums[right] == nums[right + 1])) {
                right--;
            }
            else {
                resList.add(Arrays.asList(nums[left], nums[right]));
                left++;
                right--;
            }
        }
        return resList;
    }
    // public List<List<Integer>> fourSum(int[] nums, int target) {
    //     Arrays.sort(nums);
    //     List<List<Integer>> resList = new ArrayList<>();
    //     for (int i = 0; i < nums.length; i++) {
    //         // 记得去重
    //         if (i > 0 && nums[i] == nums[i - 1]) {
    //             continue;
    //         }
    //         else {
    //             findThreeSum(nums, i, target-nums[i], resList);
    //         }
    //     }
    //     return resList;
    // }
    // private void findThreeSum(int[] nums, int start, int target, List<List<Integer>> resList) {
    //     for (int i = start + 1; i < nums.length; i++) {
    //         // 记得去重
    //         if (i > start + 1 && nums[i] == nums[i - 1]) {
    //             continue;
    //         }
    //         else {
    //             findTwoSum(nums, i, target - nums[i], resList, start);
    //         }
    //     }
    // }
    // private void findTwoSum(int[] nums, int start, int target, List<List<Integer>> resList, int preStart) {
    //     // 双指针，一个从start开始往后，一个从末尾开始往前
    //     int left = start + 1, right = nums.length - 1;
    //     while (left < right) {
    //         int currSum = nums[left] + nums[right];
    //         if (currSum == target) {
    //             // 保存答案
    //             List<Integer> currList = new ArrayList<>();
    //             currList.add(nums[start]);
    //             currList.add(nums[left]);
    //             currList.add(nums[right]);
    //             currList.add(nums[preStart]);
    //             resList.add(currList);
    //             left++;
    //             right--;
    //             // 记得去重，同时不能超过对方的位置
    //             while (nums[left] == nums[left - 1] && left < right) {
    //                 left++;
    //             }
    //             while (nums[right] == nums[right + 1] && right > left) {
    //                 right--;
    //             }
    //         }
    //         else if (currSum < target) {
    //             left++;
    //         }
    //         else {
    //             right--;
    //         }
    //     }
    // }
}
// @lc code=end

