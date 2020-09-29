/*
 * @lc app=leetcode id=525 lang=java
 *
 * [525] Contiguous Array
 */

// @lc code=start
class Solution {
    public int findMaxLength(int[] nums) {
        int res = 0, currSum = 0;
        // 建立子数组之和跟结尾位置的坐标之间的映射
        Map<Integer, Integer> sumsIdxMap = new HashMap<>();
        sumsIdxMap.put(0, -1);
        for (int i = 0; i < nums.length; i++) {
            // 遇到1就加1，遇到0，就减1
            // 这样如果某个子数组和为0，就说明0和1的个数相等
            currSum += (nums[i] == 1) ? 1 : -1;
            // 如果某个子数组之和在 HashMap 里存在了
            // 说明当前子数组末尾坐标减去 HashMap 中存的那个子数字坐标
            // 得到的结果是中间一段子数组之和必然为0的长度
            if (sumsIdxMap.containsKey(currSum)) {
                res = Math.max(res, i - sumsIdxMap.get(currSum));
            }
            // 否则把当前子数组之和的末尾坐标存进去
            else {
                sumsIdxMap.put(currSum, i);
            }
        }
        return res;
    }
}
// @lc code=end

