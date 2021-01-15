/*
 * @lc app=leetcode id=229 lang=java
 *
 * [229] Majority Element II
 */

// @lc code=start
class Solution {
    public List<Integer> majorityElement(int[] nums) {
        List<Integer> res = new ArrayList<>();
        // 摩尔投票法 Moore Voting
        int candidateA = 0, candidateB = 0, cnt1 = 0, cnt2 = 0;
        // 第一遍先选出两个候选数
        for (int num: nums) {
            if (num == candidateA) {
                cnt1++;
            }
            else if (num == candidateB) {
                cnt2++;
            }
            else if (cnt1 == 0) {
                candidateA = num;
                cnt1++;
            }
            else if (cnt2 == 0) {
                candidateB = num;
                cnt2++;
            }
            else {
                cnt1--;
                cnt2--;
            }
        }
        // 第二遍遍历重新投票验证这两个候选数是否为符合题意的数
        cnt1 = cnt2 = 0;
        for (int num : nums) {
            if (num == candidateA) {
                cnt1++;
            }
            else if (num == candidateB) {
                cnt2++;
            }
        }
        if (cnt1 > nums.length / 3) {
            res.add(candidateA);
        }
        if (cnt2 > nums.length / 3) {
            res.add(candidateB);
        }
        return res;
    }
}
// @lc code=end

