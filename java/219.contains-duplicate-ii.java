import java.util.HashMap;
import java.util.Map;

/*
 * @lc app=leetcode id=219 lang=java
 *
 * [219] Contains Duplicate II
 */

// @lc code=start
class Solution {
    public boolean containsNearbyDuplicate(int[] nums, int k) {
        Map<Integer, Integer> numsMap = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int currNum = nums[i];
            if (numsMap.containsKey(currNum) && numsMap.get(currNum) + k >= i) {
                return true;
            }
            numsMap.put(currNum, i);
        }
        return false;
    }
}
// @lc code=end

