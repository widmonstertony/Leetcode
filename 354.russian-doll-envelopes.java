import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * @lc app=leetcode id=354 lang=java
 *
 * [354] Russian Doll Envelopes
 */

// @lc code=start
class Solution {
    public int maxEnvelopes(int[][] envelopes) {
        Arrays.sort(envelopes, (a, b) -> {
            if (a[0] == b[0]) {
                return b[1] - a[1];
            }
            return a[0] - b[0];
        });
        // 二分法 Longest Increasing Subsequence解法
        // 信封的宽度从小到大排，但是宽度相等时，高度大的在前面
        // 那么问题就变成了找高度数字中的LIS
        List<Integer> dp = new ArrayList<>();
        for (int i = 0; i < envelopes.length; i++) {
            int left = 0, right = dp.size(), currHeight = envelopes[i][1];
            while (left < right) {
                int mid = left + (right - left) / 2;
                if (dp.get(mid) < currHeight) {
                    left = mid + 1;
                }
                else {
                    right = mid;
                }
            }
            if (left < dp.size()) {
                dp.set(left, currHeight);
            }
            else {
                dp.add(currHeight);
            }
        }
        return dp.size();
        // int[] dp = new int[envelopes.length];
        // Arrays.fill(dp, 1);
        // int maxRes = 0;
        // for (int i = 0; i < envelopes.length; i++) {
        //     // 对于每一个信封，我们都遍历其前面所有的信封
        //     for (int j = 0; j < i; j++) {
        //         // 如果当前信封的长和宽都比前面那个信封的大
        //         // 就可以更新dp[i]了
        //         if (envelopes[i][0] > envelopes[j][0] &&
        //             envelopes[i][1] > envelopes[j][1]) {
        //             dp[i] =  Math.max(dp[i], dp[j] + 1);
        //         }
        //     }
        //     maxRes = Math.max(dp[i], maxRes);
        // }
        // return maxRes;
    }
}
// @lc code=end

