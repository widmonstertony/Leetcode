/*
 * @lc app=leetcode id=69 lang=java
 *
 * [69] Sqrt(x)
 */

// @lc code=start
class Solution {
    public int mySqrt(int x) {
        // 处理了 0 和 1的情况
        if (x <= 1) {
            return x;
        }
        int left = 0, right = x;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            // 如果当前mid值的平方大于x，排除mid右边
            if (x / mid < mid) {
                right = mid - 1;
            }
            // 如果当前mid值的平方小于x，排除mid左边
            else if (x / mid > mid) {
                left = mid + 1;
            }
            else {
                right = mid;
                break;
            }
        }
        // 循环结束后，left是上界，right是下和等于界
        // 所以返回right
        return right;
    }
}
// @lc code=end

