#
# @lc app=leetcode id=49 lang=python3
#
# [49] Group Anagrams
#

# @lc code=start
from collections import defaultdict
class Solution:
    def groupAnagrams_first_version(self, strs: List[str]) -> List[List[str]]:
        answers = {}
        for s in strs:
            sorted_str = ''.join(sorted(s))
            if (sorted_str in answers):
                answers[sorted_str].append(s)
            else:
                answers[sorted_str] = [s]
        return list(answers.values())

    def groupAnagrams(self, strs: List[str]) -> List[List[str]]:
        answers = defaultdict(list)
        for str in strs:
            str_counts = [0] * 26
            for char in str:
                str_counts[ord(char) - ord('a')] += 1
            answers[tuple(str_counts)].append(str)
        return list(answers.values())
# @lc code=end  

