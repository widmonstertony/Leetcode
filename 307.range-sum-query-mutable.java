/*
 * @lc app=leetcode id=307 lang=java
 *
 * [307] Range Sum Query - Mutable
 */

// @lc code=start
class NumArray {
    int[] tree;
    int n;
    // Segment Tree解法
    public NumArray(int[] nums) {
        n = nums.length;
        tree = new int[n * 2];
        // 把所有原数组的数放进从n开始的新数组里作为leaf
        for (int i = 0 ; i < n; i++) {
            tree[n + i] = nums[i];
        }
        // 把所有两个leaf的sum的值放在原坐标除以2的位置
        // 一直更新到最顶上的root
        for (int i = n - 1; i >= 0; i--) {
            // 这个确定了左节点坐标是偶数，右节点坐标是奇数
            tree[i] = tree[i * 2] + tree[i * 2 + 1];
        }
    }
    public void update(int i, int val) {
        // 由于 nums 数组在 tree 数组中是从位置n开始的，所以i也要加n
        i += n;
        tree[i] = val;
        while (i > 0) {
            int left = i, right = i;
            // 如果当前是偶数，也就是左节点
            // 那么我们需要设置右节点的坐标为左节点+1
            if (i % 2 == 0) {
                right = i + 1;
            }
            // 否则需要设置左节点的坐标
            else {
                left = i - 1;
            }
            // 把左右节点的sum更新到他们的父结点，也就是除以2的坐标
            i /= 2;
            tree[i] = tree[left] + tree[right];
        }
    }
    public int sumRange(int i, int j) {
        int sum = 0;
        i += n;
        j += n;
        while (i <= j) {
            // 如果当前i是右节点
            // 那么就需要把当前节点直接加进sum，因为不能包括左节点，范围从i开始往右
            // 加完之后i往右移一位，也就是往j更靠近一点，确保能加上中间的sum
            // 否则的话如果i是左节点就不需要做任何操作，因为之后i会移到父结点，到时候直接加上父结点就好了
            // 父结点就会包括当前i和i的右边那个节点的数值总和
            if (i % 2 == 1) {
                sum += tree[i];
                i++;
            }
            // j也是同理，只是j需要检查的是j是否是左节点，因为范围到j结束，不能包括右节点
            // 如果是偶数，说明j是左节点，直接把j加进去，j往左移1位
            if (j % 2 == 0) {
                sum += tree[j];
                j--;
            }
            i /= 2;
            j /= 2;
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

