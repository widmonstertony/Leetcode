/*
 * @lc app=leetcode id=873 lang=java
 *
 * [873] Length of Longest Fibonacci Subsequence
 */

// @lc code=start
class Solution {
    public int lenLongestFibSubseq(int[] A) {
        int maxRes = Integer.MIN_VALUE;
        Map<Integer, Integer> intMap = new HashMap<>();
        for (int i = 0; i < A.length; i++) {
            intMap.put(A[i], i);
        }
        for (int i = 0; i < A.length; i++) {
            for (int j = i + 1; j < A.length; j++) {
                maxRes = Math.max(maxRes, lenLongestFibSubseqHelper(A, i, j, 2, intMap));
            }
        }
        return maxRes == 2 ? 0 : maxRes;
    }

    private int lenLongestFibSubseqHelper(int[] A, int i, int j, int currLen, Map<Integer, Integer> intMap) {
        if (j < A.length && intMap.containsKey(A[i] + A[j])) {
            int currValue = lenLongestFibSubseqHelper(A, j, intMap.get(A[i] + A[j]), currLen + 1, intMap);
            return currValue;
        }
        else {
            return currLen;
        }
    }
}
// @lc code=end

