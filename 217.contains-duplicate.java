import java.util.HashSet;

/*
 * @lc app=leetcode id=217 lang=java
 *
 * [217] Contains Duplicate
 */

// @lc code=start
class Solution {
    public boolean containsDuplicate(int[] nums) {
        Set<Integer> hashNums = new HashSet<>();
        for (int num : nums) {
            if (hashNums.contains(num)) {
                return true;
            }
            hashNums.add(num);
        }
        return false;
    }
}
// @lc code=end

