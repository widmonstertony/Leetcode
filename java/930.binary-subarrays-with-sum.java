/*
 * @lc app=leetcode id=930 lang=java
 *
 * [930] Binary Subarrays With Sum
 */

// @lc code=start
class Solution {
    public int numSubarraysWithSum(int[] A, int S) {
        // 把所有的1的index都记下来
        List<Integer> onesIdx = new ArrayList<>();
        // 把-1和A的长度也加在这个list的头尾
        onesIdx.add(-1);
        for (int i = 0; i < A.length; i++) {
            if (A[i] == 1) {
                onesIdx.add(i);
            }
        }
        onesIdx.add(A.length);
        int resCnt = 0;
        if (S == 0) {
            for (int i = 1; i < onesIdx.size(); i++) {
                int numOfZeros = onesIdx.get(i) - onesIdx.get(i - 1) - 1;
                resCnt += numOfZeros * (numOfZeros + 1) / 2;
            }
            return resCnt;
        }
        for (int i = 1; i < onesIdx.size() - S; i++) {
            int left = onesIdx.get(i) - onesIdx.get(i - 1);
            int right = onesIdx.get(i + S) - onesIdx.get(i + S - 1);
            resCnt += left * right;
        }
        return resCnt;
    }
    // public int numSubarraysWithSum(int[] A, int S) {
    //     int resCnt = 0, currSum = 0;
    //     Map<Integer, Integer> sumCntsMap = new HashMap<>();
    //     sumCntsMap.put(0, 1);
    //     for (int num : A) {
    //         currSum += num;
    //         resCnt += sumCntsMap.getOrDefault(currSum - S, 0);
    //         sumCntsMap.put(currSum, sumCntsMap.getOrDefault(currSum, 0) + 1);
    //     }
    //     return resCnt;
    // }
    // public int numSubarraysWithSum(int[] A, int S) {
    //     int resCnt = 0;
    //     for (int i = 0; i < A.length; i++) {
    //         int currSum = 0;
    //         for (int j = i; j < A.length; j++) {
    //             currSum += A[j];
    //             if (currSum == S) {
    //                 resCnt++;
    //             }
    //         }
    //     }
    //     return resCnt;
    // }
}
// @lc code=end

