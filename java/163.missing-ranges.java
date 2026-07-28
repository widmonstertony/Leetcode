/*
 * @lc app=leetcode id=163 lang=java
 *
 * [163] Missing Ranges
 */

// @lc code=start
class Solution {
    public List<String> findMissingRanges(int[] nums, int lower, int upper) {
        if (nums.length == 0) {
            return Collections.singletonList(formatRange(lower, upper));
        }
        List<String> resList = new ArrayList<>();
        
        if (nums[0] > lower) {
            resList.add(formatRange(lower, nums[0] - 1));
        }
        
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] - nums[i - 1] > 1) {
                resList.add(formatRange(nums[i - 1] + 1, nums[i] - 1));
            }
        }
        
        if (nums[nums.length - 1] < upper) {
            resList.add(formatRange(nums[nums.length - 1] + 1, upper));
        }
        return resList;
    }
    private String formatRange(int lower, int upper) {
        if (lower == upper) {
            return String.valueOf(lower);
        }
        else {
            return lower + "->" + upper;
        }
    }
}
// @lc code=end

