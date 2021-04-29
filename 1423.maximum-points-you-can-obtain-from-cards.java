/*
 * @lc app=leetcode id=1423 lang=java
 *
 * [1423] Maximum Points You Can Obtain from Cards
 */

// @lc code=start
class Solution {
    public int maxScore(int[] cardPoints, int k) {
        // fontSum[i] 代表从前面拿i张的和
        // backSum[i] 代表从后面拿i张的和
        int[] frontSum = new int[k + 1];
        int[] backSum = new int[k + 1];
        for (int i = 0; i <= k; i++) {
            if (i == 0) {
                frontSum[i] = 0;
                backSum[i] = 0;
            }
            else {
                frontSum[i] = frontSum[i - 1] + cardPoints[i - 1];
                backSum[i] = backSum[i - 1] + cardPoints[cardPoints.length - i];
            }
        }
        int currSum = Integer.MIN_VALUE;
        for (int i = 0; i <= k; i++) {
            // 从前面拿i张和从后面拿k - i张的总和
            currSum = Math.max(currSum, frontSum[i] + backSum[k - i]);
        }
        return currSum;
    }
}
// @lc code=end

