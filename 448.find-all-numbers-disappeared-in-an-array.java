/*
 * @lc app=leetcode id=448 lang=java
 *
 * [448] Find All Numbers Disappeared in an Array
 */

// @lc code=start
class Solution {
    public List<Integer> findDisappearedNumbers(int[] nums) {
        for(int i = 0; i < nums.length; i++) {
            int currNum = nums[i];
            // 如果当前坐标和数字不匹配，则一直交换到正确位置
            while (i != currNum - 1) {
                swap(nums, currNum - 1, i);
                // 更新当前位置新的数
                currNum = nums[i];
                // 如果下一个交换的是一样的数，停止
                if (nums[i] == nums[currNum - 1]) {
                    break;
                }
            }
        }
        List<Integer> resList = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != i + 1) {
                resList.add(i + 1);
            }
        }
        return resList;
    }
    private void swap(int[] nums, int i, int j) {
        int tmp = nums[i];
        nums[i] = nums[j];
        nums[j] = tmp;
    }
}
// @lc code=end

