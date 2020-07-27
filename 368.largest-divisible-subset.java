/*
 * @lc app=leetcode id=368 lang=java
 *
 * [368] Largest Divisible Subset
 */

// @lc code=start
class Solution {
    public List<Integer> largestDivisibleSubset(int[] nums) {
        Arrays.sort(nums);
        List<Integer> resList = new ArrayList<>();
        // dp[i]表示从末尾到数字nums[i]位置最大可整除的子集合的长度
        int[] dp = new int[nums.length];
        // 表示当前i在array里的最大被整除数的index
        int[] lastParentIdx = new int[nums.length];
        int currMaxLength = 0, maxLenIdx = 0;
        // 从后往前遍历数组，对于某个数字再遍历到末尾
        for (int i = nums.length - 1; i >= 0; i--) {
            for (int j = i; j < nums.length; j++) {
                // 如果能找到一个后面的数j能整除当前的i，并且i的最大可整除子集合长度比j还小1以上
                if (nums[j] % nums[i] == 0 && dp[i] < dp[j] + 1) {
                    dp[i] = dp[j] + 1;
                    lastParentIdx[i] = j;
                    if (currMaxLength < dp[i]) {
                        currMaxLength = dp[i];
                        maxLenIdx = i;
                    }
                }
            }
        }
        // 填res数字，根据lastParentIdx数组来找到每一个数字
        for (int i = 0; i < currMaxLength; i++) {
            resList.add(nums[maxLenIdx]);
            maxLenIdx = lastParentIdx[maxLenIdx];
        }
        return resList;
    }
}
// @lc code=end

