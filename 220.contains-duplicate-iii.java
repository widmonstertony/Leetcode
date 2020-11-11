import java.util.TreeSet;

/*
 * @lc app=leetcode id=220 lang=java
 *
 * [220] Contains Duplicate III
 */

// @lc code=start
class Solution {
    public boolean containsNearbyAlmostDuplicate(int[] nums, int k, int t) {
        TreeSet<Long> numSet = new TreeSet<>();
        for (int i = 0; i < nums.length; i++) {
            long currNum = nums[i];
            // 找到在set里 最接近又不大于当前数字加上t后的数的最大数
            Long floorNum = numSet.floor(currNum + t);
            // 找到在set里 最接近又不小于数字减去t后的数的最小数
            Long ceilingNum = numSet.ceiling(currNum - t);
            if (floorNum != null && floorNum >= currNum) {
                return true;
            }
            if (ceilingNum != null && ceilingNum <= currNum) {
                return true;
            }
            numSet.add(currNum);
            // 把离当前位置k个位置的数从set里删除，因为已经不符合题意了
            if (i >= k) {
                numSet.remove((long) nums[i - k]);
            }
        }
        return false;
    }
}
// @lc code=end

