/*
 * @lc app=leetcode id=494 lang=java
 *
 * [494] Target Sum
 */

// @lc code=start
class Solution {
    // dp解法
    public int findTargetSumWays(int[] nums, int S) {
        // dp[i][j] 表示到第 i-1 个数字且和为j的情况总数
        List<Map<Integer, Integer>> dp = new ArrayList<Map<Integer, Integer>>();
        for (int num: nums) {
            dp.add(new HashMap<>());
        }
        dp.add(new HashMap<>());
        dp.get(0).put(0, 1);
        for (int i = 0; i < nums.length; i++) {
            for (Map.Entry<Integer, Integer> mEntry: dp.get(i).entrySet()) {
                int sum = mEntry.getKey(), cnt = mEntry.getValue();
                Map<Integer, Integer> currMap = dp.get(i + 1);
                currMap.put(sum + nums[i], currMap.getOrDefault(sum + nums[i], 0) + cnt);
                currMap.put(sum - nums[i], currMap.getOrDefault(sum - nums[i], 0) + cnt);
            }
        }
        return dp.get(nums.length).getOrDefault(S, 0);
    }
    
    // dfs解法
    // public int findTargetSumWays(int[] nums, int S) {
    //     List<Map<Integer, Integer>> memeArr = new ArrayList<Map<Integer, Integer>>();
    //     for (int num: nums) {
    //         memeArr.add(new HashMap<>());
    //     }
    //     return helper(nums, 0, S, memeArr);
    // }
    // private int helper(int[] nums, int currIdx, int currSum, List<Map<Integer, Integer>> memeArr) {
    //     if (currIdx >= nums.length) {
    //         if (currSum == 0) {
    //             return 1;
    //         }
    //         return 0;
    //     }
    //     if (memeArr.get(currIdx).containsKey(currSum)) {
    //         return memeArr.get(currIdx).get(currSum);
    //     }
    //     // 把加1和减1的情况都计算好
    //     int cnt1 = helper(nums, currIdx + 1, currSum - nums[currIdx], memeArr);
    //     int cnt2 = helper(nums, currIdx + 1, currSum + nums[currIdx], memeArr);
    //     memeArr.get(currIdx).put(currSum, cnt1 + cnt2);
    //     return cnt1 + cnt2;
    // }
}
// @lc code=end

