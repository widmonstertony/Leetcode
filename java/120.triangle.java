/*
 * @lc app=leetcode id=120 lang=java
 *
 * [120] Triangle
 */

// @lc code=start
class Solution {
    public int minimumTotal(List<List<Integer>> triangle) {
        // 一维dp 
        int[] dp = new int[triangle.get(triangle.size() - 1).size()];
        int minSum = Integer.MAX_VALUE;
        for (int i = 0; i < triangle.size(); i++) {
            // 遍历每层的顺序反过来，因为要保留dp[j - 1]这个信息
            for (int j = triangle.get(i).size() - 1; j >= 0; j--) {
                if (i == 0 && j == 0) {
                    dp[j] = triangle.get(i).get(j);
                }
                else if (j == 0) {
                    dp[j] = dp[j] + triangle.get(i).get(j);
                }
                else if (j == triangle.get(i).size() - 1) {
                    dp[j] = dp[j - 1] + triangle.get(i).get(j);
                }
                else {
                    dp[j] = Math.min(dp[j - 1], dp[j]) + triangle.get(i).get(j);
                }
                if (i == triangle.size() - 1) {
                    minSum = Math.min(minSum, dp[j]);
                }
            }
        }
        return minSum;
    }

    // public int minimumTotal(List<List<Integer>> triangle) {
    //     // 二维dp 
    //     int[][] dp = new int[triangle.size()][triangle.get(triangle.size() - 1).size()];
    //     int minSum = Integer.MAX_VALUE;
    //     for (int i = 0; i < triangle.size(); i++) {
    //         for (int j = 0; j < triangle.get(i).size(); j++) {
    //             if (i == 0 && j == 0) {
    //                 dp[i][j] = triangle.get(i).get(j);
    //             }
    //             else if (j == 0) {
    //                 dp[i][j] = dp[i - 1][j] + triangle.get(i).get(j);
    //             }
    //             else if (j == triangle.get(i).size() - 1) {
    //                 dp[i][j] = dp[i - 1][j - 1] + triangle.get(i).get(j);
    //             }
    //             else {
    //                 dp[i][j] = Math.min(dp[i - 1][j - 1], dp[i - 1][j]) + triangle.get(i).get(j);
    //             }
    //             if (i == triangle.size() - 1) {
    //                 minSum = Math.min(minSum, dp[i][j]);
    //             }
    //         }
    //     }
    //     return minSum;
    // }
}
// @lc code=end

