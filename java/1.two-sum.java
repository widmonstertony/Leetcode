import java.util.HashMap;
import java.util.Map;

/*
 * @lc app=leetcode id=1 lang=java
 *
 * [1] Two Sum
 */

// @lc code=start
class Solution {
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> numsMap = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int currNum = nums[i];
            if(numsMap.containsKey(target - currNum)) {
                return new int[]{numsMap.get(target - currNum), i};
            }
            numsMap.put(currNum, i);
        }
        return new int[]{};
    }
}
// @lc code=end

