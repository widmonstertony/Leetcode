/*
 * @lc app=leetcode id=275 lang=java
 *
 * [275] H-Index II
 */

// @lc code=start
class Solution {
    public int hIndex(int[] citations) {
        int left = 0, right = citations.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int largerPaperCnt = citations.length - mid;
            // 找lower bound，因为会有相同的数
            // 那么最小的那个才是我们要找的
            if (largerPaperCnt > citations[mid]) {
                left = mid + 1;
            }
            else {
                right = mid - 1;
            }
        }
        return citations.length - left;
    }
}
// @lc code=end

