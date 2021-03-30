/*
 * @lc app=leetcode id=397 lang=java
 *
 * [397] Integer Replacement
 */

// @lc code=start
class Solution {
    public int integerReplacement(int n) {
        // 利用hash table来缓存之前已经跑过的结果
        Map<Long, Integer> smallMap = new HashMap();
        return integerReplacement(n, smallMap);
    }
    // 使用long因为int + 1会变成复数
    private int integerReplacement(long n, Map<Long, Integer> smallMap) {
        if (smallMap.containsKey(n)) {
            return smallMap.get(n);
        }
        if (n == 1) {
            return 0;
        }
        if (n % 2 == 0) {
            return integerReplacement(n / 2, smallMap) + 1;
        }
        int res = Math.min(integerReplacement(n + 1, smallMap), integerReplacement(n - 1, smallMap)) + 1;
        smallMap.put(n, res);
        return res;
    }
}
// @lc code=end

