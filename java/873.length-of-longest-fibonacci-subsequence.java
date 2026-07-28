/*
 * @lc app=leetcode id=873 lang=java
 *
 * [873] Length of Longest Fibonacci Subsequence
 */

// @lc code=start
class Solution {
    public int lenLongestFibSubseq(int[] A) {
        int maxRes = Integer.MIN_VALUE;
        // 把A里的数值和坐标存在hashmap里，优化查找时间
        Map<Integer, Integer> intMap = new HashMap<>();
        // dp[i][j]是表示以 A[i] 和 A[j] 结尾的斐氏数列的长度
        int[][] dp = new int[A.length][A.length];
        // 先确定一个数字
        for (int i = 0; i < A.length; i++) {
            intMap.put(A[i], i);
            // 然后遍历之前比其小的所有数字
            // 这样 A[i] 和 A[j] 两个数字确定了
            for (int j = 0; j < i; j++) {
                // 默认数列长度为2，因为至少有两个数字
                dp[j][i] = 2;
                // 此时要找一个比 A[i]和 A[j]都小的数，即 A[i] - A[j]
                // 如果能在数组里找到A[i] - A[j]的那个数，那就能构成斐氏数列
                if (intMap.containsKey(A[i] - A[j])) {
                    int startIdx = intMap.get(A[i] - A[j]);
                    // 确保A[i] - A[j]那个数是小于A[j]的，也就是在j前面的
                    // 这样，就可以把i加到以j和startIdx结尾的数列后面，所以长度增加1
                    if (A[i] - A[j] < A[j]) {
                        dp[j][i] = dp[startIdx][j] + 1;
                    }
                }
                // 更新答案
                maxRes = Math.max(maxRes, dp[j][i]);
            }
        }
        return maxRes == 2 ? 0 : maxRes;
    }
    // public int lenLongestFibSubseq(int[] A) {
    //     int maxRes = Integer.MIN_VALUE;
    //     Map<Integer, Integer> intMap = new HashMap<>();
    //     for (int i = 0; i < A.length; i++) {
    //         intMap.put(A[i], i);
    //     }
    //     for (int i = 0; i < A.length; i++) {
    //         for (int j = i + 1; j < A.length; j++) {
    //             maxRes = Math.max(maxRes, lenLongestFibSubseqHelper(A, i, j, 2, intMap));
    //         }
    //     }
    //     return maxRes == 2 ? 0 : maxRes;
    // }

    // private int lenLongestFibSubseqHelper(int[] A, int i, int j, int currLen, Map<Integer, Integer> intMap) {
    //     if (j < A.length && intMap.containsKey(A[i] + A[j])) {
    //         int currValue = lenLongestFibSubseqHelper(A, j, intMap.get(A[i] + A[j]), currLen + 1, intMap);
    //         return currValue;
    //     }
    //     else {
    //         return currLen;
    //     }
    // }
}
// @lc code=end

