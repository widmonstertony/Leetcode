/*
 * @lc app=leetcode id=324 lang=java
 *
 * [324] Wiggle Sort II
 */

// @lc code=start
class Solution {
    // 这题和280 wiggle sort不一样的地方在于，如果有一连串的相等的数字
    // 则没法用swap的方式分配好
    public void wiggleSort(int[] nums) {
        // 先找到中位数
        int median = findKthLargest(nums, nums.length / 2);
        int[] sortedNums = Arrays.copyOf(nums, nums.length);
        int smallIdx = 0, largeIdx = sortedNums.length / 2;
        for (int i = 0; i < nums.length; i++) {
            if (i % 2 == 1) {
                nums[i] = sortedNums[smallIdx];
                smallIdx++;
            }
            else {
                nums[i] = sortedNums[largeIdx];
                largeIdx++;
            }
        }
    }
    // O(n) runing time average, O(1) space
    // public void wiggleSort(int[] nums) {
    //     // 先找到中位数
    //     int median = findKthLargest(nums, nums.length / 2);
    //     int i = 0, j = 0, k = nums.length - 1;
    //     // virtual indexing
    //     while (j <= k) {
    //         if (nums[newIdx(j, nums)] > median) {
    //             swap(newIdx(i, nums), newIdx(j, nums), nums);
    //             i++;
    //             j++;
    //         }
    //         else if (nums[newIdx(j, nums)] < median) {
    //             swap(newIdx(k, nums), newIdx(j, nums), nums);
    //             k--;
    //         }
    //         else {
    //             j++;
    //         }
    //     }
    // }
    // // Index-rewiring
    // private int newIdx(int i, int[] nums) {
    //     return (1 + 2 * i) % (nums.length | 1);
    // }

    private int findKthLargest(int[] nums, int k) {
        int left = 0, right = nums.length - 1;
        k--;
        while (left <= right) {
            int piovtIdx = partition(nums, left, right);
            if (piovtIdx < k) {
                left = piovtIdx + 1;
            }
            else if (piovtIdx > k) {
                right = piovtIdx - 1;
            }
            else {
                return nums[piovtIdx];
            }
        }
        return -1;
    }

    private int partition(int[] nums, int left, int right) {
        int piovt = nums[right];
        int piovtIdx = left;
        // while (piovtIdx <= right) {
        //     if (nums[piovtIdx] > piovt) {
        //         swap(piovtIdx, left, nums);
        //         piovtIdx++;
        //         left++;
        //     }
        //     else if (nums[piovtIdx] < piovt) {
        //         swap(piovtIdx, right, nums);
        //         right--;
        //     }
        //     else {
        //         piovtIdx++;
        //     }
        // }
        for (int i = piovtIdx; i < right; i++) {
            if (nums[i] > piovt) {
                swap(i, piovtIdx, nums);
                piovtIdx++;
            }
        }
        swap(piovtIdx, right, nums);
        int res = piovtIdx;
        // 把和piovt相同的数字移到piovt后面
        // 这样才能确保选择数的时候，相同的数会被分开来选
        for (int i = res + 1; i <= right; i++) {
            if (nums[i] == piovt) {
                swap(i, res + 1, nums);
                res++;
            }
        }
        return piovtIdx;
    }


    private void swap(int i, int j, int[] nums) {
        int tmpNum = nums[i];
        nums[i] = nums[j];
        nums[j] = tmpNum;
    }


    // public void wiggleSort(int[] nums) {
    //     // 先把nums排序
    //     int[] sortedNums = Arrays.copyOf(nums, nums.length);
    //     Arrays.sort(sortedNums);
    //     // 再分别从数组中间和末尾轮流取数字
    //     // 这样确保一定是一大一小的
    //     int smallIdx = (sortedNums.length + 1) / 2 - 1, largeIdx = sortedNums.length - 1;
    //     for (int i = 0; i < nums.length; i++) {
    //         if (i % 2 == 0) {
    //             nums[i] = sortedNums[smallIdx];
    //             smallIdx--;
    //         }
    //         else {
    //             nums[i] = sortedNums[largeIdx];
    //             largeIdx--;
    //         }
    //     }
    // }

}
// @lc code=end

