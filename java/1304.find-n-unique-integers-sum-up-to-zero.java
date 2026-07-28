/*
 * @lc app=leetcode id=1304 lang=java
 *
 * [1304] Find N Unique Integers Sum up to Zero
 */

// @lc code=start
class Solution {
    public int[] sumZero(int n) {
        int[] res = new int[n];
        
        // 如果是奇数，则把最中间的那个数设置为0
        if (n % 2 != 0) {
            res[n / 2] = 0;
        }
        
        // 把每一个数正数和负数分别加进array的两头
        for (int i = 0; i < n / 2; i++) {
            res[i] = -(i + 1);
            res[n - i - 1] = (i + 1);
        }
    
        return res;
    }
}
// @lc code=end

