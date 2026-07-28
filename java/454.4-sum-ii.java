import java.util.Map;

/*
 * @lc app=leetcode id=454 lang=java
 *
 * [454] 4Sum II
 */

// @lc code=start
class Solution {
    public int fourSumCount(int[] A, int[] B, int[] C, int[] D) {
        Map<Integer, Integer> resultMap = new HashMap<Integer, Integer>();
        // 先把 C和D的总和放进hashmap
        for (int i = 0; i < C.length; i++) {
            for (int j = 0; j < D.length; j++) {
                int currSum = C[i] + D[j];
                if (resultMap.containsKey(currSum)) {
                    resultMap.put(currSum, resultMap.get(currSum) + 1);
                }
                else {
                    resultMap.put(currSum, 1);
                }
            }
        }
        int res = 0;
        // 然后计算A和B的和，在hashmap里找负的那个情况
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < B.length; j++) {
                int currSum = A[i] + B[j];
                if (resultMap.containsKey(-currSum)) {
                    res += resultMap.get(-currSum);
                }
            }
        }
        return res;
    }
}
// @lc code=end

