/*
 * @lc app=leetcode id=410 lang=java
 *
 * [410] Split Array Largest Sum
 */

// @lc code=start
class Solution {
    // 二分法
    public int splitArray(int[] nums, int m) {
        // left是数组里的最大值，right是数组的sum
        long left = 0, right = 0;
        for (int i = 0; i < nums.length; i++) {
            left = Math.max(left, (long)nums[i]);
            right += nums[i];
        }
        // 最后答案一定在这两个之间
        // 要么每个数字一组，要么全部数字一共一组
        while (left < right) {
            long mid = left + (right - left) / 2;
            // 取中值，如果数组可以分成m组最大值不超过mid的
            // 说明当前mid是一个解，再把mid缩小看看能不能分成m组
            if (canSplit(nums, m, mid)) {
                right = mid;
            }
            // 否则说明mid不是一个解，并且mid太小了
            else {
                left = mid + 1;
            }
        }
        return (int)right;
    }
    boolean canSplit(int[] nums, long m, long sum) {
        long groupCnt = 1, currSum = 0;
        for (int i = 0; i < nums.length; i++) {
            currSum += nums[i];
            // 如果当前sum大于最大的sum，创建新的group
            if (currSum > sum) {
                currSum = nums[i];
                groupCnt++;
                // 如果已经创建的group数多于m，说明没法split
                if (groupCnt > m) {
                    return false;
                }
            }
        }
        return true;
    }
    // dp解法，有点brute force的感觉
    // public int splitArray(int[] nums, int m) {
    //     // 0到i之前的数字总和
    //     long[] sums = new long[nums.length + 1];
    //     for (int i = 1; i <= nums.length; i++) {
    //         sums[i] = sums[i - 1] + nums[i - 1];
    //     }
    //     // dp[i][j] 代表把0到i之前分成j份的sum最小值
    //     long[][] dp = new long[nums.length + 1][m + 1];
    //     for (int i = 0; i <= nums.length; i++) {
    //         Arrays.fill(dp[i], Long.MAX_VALUE);
    //     }
    //     dp[0][0] = 0;
    //     for (int i = 1; i <= nums.length; i++) {
    //         for (int  j = 1; j <= m; j++) {
    //             // 把当前分成从0到k - 1, 和从k到i - 1两个部分
    //             for (int k = 0; k < i; k++) {
    //                 // 0到k - 1分成j - 1份，和k到i - 1这部分，总共就是j份
    //                 // 把0到k - 1分成j - 1份的sum最小值，和k到i - 1的sum对比
    //                 // 那个更大的值这就是把当前两个部分以k为界限分出来的最后sum值
    //                 Long val = Math.max(dp[k][j - 1], sums[i] - sums[k]);
    //                 // 再用这个值去更新把0到i之前分成j份的最小值
    //                 dp[i][j] = Math.min(dp[i][j], val);
    //             }
    //         }
    //     }
    //     return (int)dp[nums.length][m];
    // }
}
// @lc code=end

