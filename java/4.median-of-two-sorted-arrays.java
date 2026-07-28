/*
 * @lc app=leetcode id=4 lang=java
 *
 * [4] Median of Two Sorted Arrays
 */

// @lc code=start
class Solution {
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int m = nums1.length, n = nums2.length;
        return (findKth(nums1, nums2, 0, 0, (m + n + 1) / 2) +
                findKth(nums1, nums2, 0, 0, (m + n + 2) / 2)) / 2.0;
    }
    // 找第k大的数
    private int findKth(int[] nums1, int[] nums2, int nums1StartIdx, int nums2StartIdx, int k) {
        // 确保start的idx没有超过数组长度
        // 不然第k大的数肯定在另一个数组里
        if (nums1StartIdx >= nums1.length) {
            return nums2[nums2StartIdx + k - 1];
        }
        if (nums2StartIdx >= nums2.length) {
            return nums1[nums1StartIdx + k - 1];
        }
        if (k == 1) {
            return Math.min(nums1[nums1StartIdx], nums2[nums2StartIdx]);
        }
        int nums1HalfKth = Integer.MAX_VALUE;
        int nums2HalfKth = Integer.MAX_VALUE;
        // 确保nums1和nums2有足够的长度包含第k大的数
        // 否则第k大的数肯定在另一个够长的数组里
        if (nums1StartIdx + k / 2 - 1 < nums1.length) {
            nums1HalfKth = nums1[nums1StartIdx + k / 2 - 1];
        }
        if (nums2StartIdx + k / 2 - 1 < nums1.length) {
            nums2HalfKth = nums1[nums2StartIdx + k / 2 - 1];
        }
        // 如果nums1的第一半k大的数比nums2的第一半k大的数更大
        // 说明nums1有第k大的数
        if (nums1HalfKth > nums2HalfKth) {
            return findKth(nums1, nums2, nums1StartIdx, nums2StartIdx + k / 2, k - k / 2);
        }
        else {
            return findKth(nums1, nums2, nums1StartIdx + k / 2, nums2StartIdx, k - k / 2);
        }
    }
}
// @lc code=end

