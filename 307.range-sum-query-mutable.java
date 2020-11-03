/*
 * @lc app=leetcode id=307 lang=java
 *
 * [307] Range Sum Query - Mutable
 */

// @lc code=start
class NumArray {
    int[] tree;
    int n;
    public NumArray(int[] nums) {
        n = nums.length;
        tree = new int[n * 2];
        // 先给 tree 数组的后n个数字按顺序填上 nums 数字
        for (int i = n; i < n * 2; i++) {
            tree[i] = nums[i - n];
        }
        // 左边的就是每一层的和，一直加到root
        for (int i = n - 1; i > 0; i--) {
            tree[i] = tree[i * 2] + tree[i * 2 + 1];
        }
    }
    public void update(int i, int val) {
        // 由于 nums 数组在 tree 数组中是从位置n开始的，所以i也要加n
        tree[i + n] = val;
        i += n;
        // tree 数组中i位置的父结点是在 tree[i/2], 所以我们要更新 tree[i/2]
        // 那么要更新父结点值，就要知道左右子结点值
        while (i > 0) {
            // tree[i / 2] = tree[i] + tree[i ^ 1];
            int left = i;
            int right = i;
            // 若i是偶数，则说明其是左子结点
            if (i % 2 == 0) {
                right = i + 1;
            } 
            // 若i是奇数，则说明其是右子结点
            else {
                left = i - 1;
            }
            tree[i / 2] = tree[left] + tree[right];
            i /= 2;
        }
    }
    public int sumRange(int i, int j) {
        int sum = 0;
        // 先进行坐标变换，i、j加n,然后进行累加
        for (i += n, j += n; i <= j; i /= 2, j /= 2) {
            // 若i是左子结点
            // 那么跟其成对儿出现的右边的结点就在要求的区间里
            // 则此时直接加上父结点值即可
            // 若i是右子结点，那么只需要加上结点i本身即可
            // 若j是右子结点，那么跟其成对儿出现的左边的结点就在要求的区间里
            // 则此时直接加上父结点值即可
            
            // 若i是奇数，则说明其是右子结点，则加上 tree[i] 本身
            // 然后i自增1
            if (i % 2 == 1) {
                sum += tree[i];
                i++;
            }
            // 若j是偶数，则说明其是左子结点
            // 则加上 tree[j] 本身，然后j自减1
            if (j % 2 == 0) {
                sum += tree[j];
                j--;
            } 
        }
        return sum;
    }
    // 暴力解
    // int[] sums;
    // int[] nums;
    // public NumArray(int[] nums) {
    //     sums = new int[nums.length];
    //     this.nums = nums;
    //     for (int i = 0; i < nums.length; i++) {
    //         if (i == 0) {
    //             sums[i] = nums[i];
    //         }
    //         else {
    //             sums[i] = sums[i - 1] + nums[i];
    //         }
    //     }
    // }
    
    // public void update(int i, int val) {
    //     this.nums[i] = val;
    //     for (int x = i; x < nums.length; x++) {
    //         if (x == 0) {
    //             sums[x] = this.nums[x];
    //         }
    //         else {
    //             sums[x] = sums[x - 1] + this.nums[x];
    //         }
    //     }
    // }
    
    // public int sumRange(int i, int j) {
    //     if (i == 0) {
    //         return sums[j];
    //     }
    //     return sums[j] - sums[i - 1];
    // }
}

/**
 * Your NumArray object will be instantiated and called as such:
 * NumArray obj = new NumArray(nums);
 * obj.update(i,val);
 * int param_2 = obj.sumRange(i,j);
 */
// @lc code=end

