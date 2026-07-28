import java.util.Arrays;

public class GoogleOA {
    // 
    public int minPairSum(int[] nums) {
        // 首先把nums排好序
        Arrays.sort(nums);
        int leftPtr = 0, rightPtr = nums.length - 1;
        int minMaxSum = Integer.MIN_VALUE;
        // 然后依次把最大的和最小的配对
        // 这样就能确保加起来后的数尽可能地小
        // 题目写了数组是偶数个
        while (leftPtr <= rightPtr) {
            // 如果当前 配对和 大于 目前最小的和，更新目前最小的和
            minMaxSum = Math.max(nums[leftPtr] + nums[rightPtr], minMaxSum);
            leftPtr++;
            rightPtr--;
        }
        return minMaxSum;
    }


}
