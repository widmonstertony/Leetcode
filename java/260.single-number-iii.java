/*
 * @lc app=leetcode id=260 lang=java
 *
 * [260] Single Number III
 */

// @lc code=start
class Solution {
    public int[] singleNumber(int[] nums) {
        Set<Integer> numSet = new HashSet<>();
        for (int num : nums) {
            if (numSet.contains(num)) {
                numSet.remove(num);
            }
            else {
                numSet.add(num);
            }
        }
        int[] res = new int[numSet.size()];
        int i = 0;
        for (int num: numSet) {
            res[i] = num;
            i++;
        }
        return res;
    }
}
// @lc code=end

