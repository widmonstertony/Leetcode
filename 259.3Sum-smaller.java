/*
 * @lc app=leetcode id=259 lang=java
 *
 * [259] 3Sum Smaller
 */

// @lc code=start
class Solution {
    public int threeSumSmaller(int[] nums, int target) {
        int res = 0;
        Arrays.sort(nums);
        for (int i = 0; i < nums.length; i++) {
            // 注意这里不用跳过相同的i的数字，因为都是符合条件的，需要计数
            int j = i + 1, k = nums.length - 1;
            while (j < k) {
                int currSum = nums[i] + nums[j] + nums[k];
                if (currSum < target) {
                    // 当前sum已经小于target，
                    // 需要把k值的所有从j + 1到k的情况全部加进答案，因为这些值都小于k，所以一定都符合条件
                    res += k - j;
                    // 注意这里加完之后需要让j往后移，也就是继续尝试让sum更接近target
                    j++;
                }
                else {
                    k--;
                }
            }
        }
        return res;
    }
}
// @lc code=end

