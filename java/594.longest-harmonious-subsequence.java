import java.util.HashMap;
import java.util.Map;

/*
 * @lc app=leetcode id=594 lang=java
 *
 * [594] Longest Harmonious Subsequence
 */

// @lc code=start
class Solution {
    public int findLHS(int[] nums) {
        int res = 0;
        Map<Integer, Integer> numCntMap = new HashMap<>();
        // 遍历一遍
        for (int num: nums) {
            // 建立每个数字跟其出现次数之间的映射
            int currCnt = numCntMap.getOrDefault(num, 0) + 1;
            numCntMap.put(num, currCnt);
            // 在 HashMap 中查找该数字加1和减1是否存在，存在就更新结果
            if (numCntMap.containsKey(num + 1)) {
                res = Math.max(res, currCnt + numCntMap.get(num + 1));
            }
            if (numCntMap.containsKey(num - 1)) {
                res = Math.max(res, currCnt + numCntMap.get(num - 1));
            } 
        }
        return res;
    }
}
// @lc code=end

