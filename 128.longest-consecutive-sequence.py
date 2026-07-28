#
# @lc app=leetcode id=128 lang=python3
#
# [128] Longest Consecutive Sequence
#

# @lc code=start
class Solution:
    def longestConsecutive(self, nums: List[int]) -> int:
        nums_set = set(nums)
        longest_strike = 0
        for num in nums_set:
            curr_num = num
            if (curr_num - 1 in nums_set): # 第一次写漏了这个条件，结果TLE了
                continue
            current_strike = 1
            while (curr_num + 1 in nums_set):
                current_strike += 1
                curr_num += 1
            longest_strike = max(longest_strike, current_strike)
        return longest_strike
        
# @lc code=end

