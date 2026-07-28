/*
 * @lc app=leetcode id=503 lang=java
 *
 * [503] Next Greater Element II
 */

// @lc code=start
class Solution {
    public int[] nextGreaterElements(int[] nums) {
        int numLength = nums.length;
        int[] resArr = new int[numLength];
        Arrays.fill(resArr, -1);
        // 一个单调递减的stack
        // 一旦遍历时的数不递减了，说明找到那个大于栈顶元素的数字了
        Stack<Integer> numSt = new Stack<>();
        // 遍历两倍的数组, 超过n的部分只是为了给之前栈中的数字找较大值
        for (int i = 0; i < 2 * numLength; i++) {
            int currNum = nums[i % numLength];
            while (!numSt.isEmpty() && nums[numSt.peek()] < currNum) {
                resArr[numSt.pop()] = currNum;
            }
            // res的长度必须是n，超过n的部分不能压入栈
            if (i < numLength) {
                numSt.push(i);
            }
        }
        return resArr;
    }
}
// @lc code=end

