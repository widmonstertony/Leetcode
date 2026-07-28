#
# @lc app=leetcode id=347 lang=python3
#
# [347] Top K Frequent Elements
#

# @lc code=start
from collections import defaultdict
import heapq


class Solution:
    def topKFrequent(self, nums: List[int], k: int) -> List[int]:
        count = defaultdict(int)
        for num in nums:
            count[num] += 1
        heap = []
        for num, freq in count.items():
            heapq.heappush(heap, (freq, num))
            # 维护 k 个最大频率元素，堆顶始终是最小的
            if len(heap) > k:
                heapq.heappop(heap)
        return [num for freq, num in heap]
            
# @lc code=end

