import java.util.ArrayList;

/*
 * @lc app=leetcode id=373 lang=java
 *
 * [373] Find K Pairs with Smallest Sums
 */

// @lc code=start
class Solution {
    public List<List<Integer>> kSmallestPairs(int[] nums1, int[] nums2, int k) {
        List<List<Integer>> res = new ArrayList<>();
        int minSize = Math.min(k, nums1.length * nums2.length);
        // searchIdx[i] 代表nums1将从nums2数组上配对的下一个坐标
        int[] searchIdx = new int[nums1.length];
        for (int cnt = 0; cnt < minSize; cnt++) {
            int currIdx = 0;
            int sum = Integer.MAX_VALUE;
            // 遍历所有的i，找到下一个最小sum的组合
            for (int i = 0; i < nums1.length; i++) {
                int nums2Idx = searchIdx[i];
                // 如果nums2Idx在nums2的范围里，说明当前nums1的第i位在nums2上还没有配对完
                // 如果当前nums1的i位和nums2的idx加起来比当前sum还小，则更新当前currIdx和sum
                if (nums2Idx < nums2.length && sum >= nums2[nums2Idx] + nums1[i]) {
                    currIdx = i;
                    sum = nums1[i] + nums2[nums2Idx];
                }
            }
            List<Integer> newList = new ArrayList<>();
            newList.add(nums1[currIdx]);
            newList.add(nums2[searchIdx[currIdx]]);
            res.add(newList);
            searchIdx[currIdx]++;
        }
        return res;
    }
    // 暴力解
    // public List<List<Integer>> kSmallestPairs(int[] nums1, int[] nums2, int k) {
    //     List<List<Integer>> res = new ArrayList<>();
    //     // 这里的一点小优化，只遍历到最多k位
    //     for (int i = 0; i < Math.min(nums1.length, k); i++) {
    //         for (int j = 0; j < Math.min(nums2.length, k); j++) {
    //             List<Integer> newList = new ArrayList<>();
    //             newList.add(nums1[i]);
    //             newList.add(nums2[j]);
    //             res.add(newList);
    //         }
    //     }
    //     // 直接排序然后取前k位
    //     Collections.sort(res, (List<Integer> a, List<Integer> b) -> {
    //         return a.get(0) + a.get(1) - b.get(0) - b.get(1);
    //     });
    //     if (res.size() <= 0 || k > res.size()) {
    //         return res;
    //     }
    //     return res.subList(0, k);
    // }
}
// @lc code=end

