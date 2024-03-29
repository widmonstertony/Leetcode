/*
 * @lc app=leetcode id=456 lang=java
 *
 * [456] 132 Pattern
 */

// @lc code=start
class Solution {
    public boolean find132pattern(int[] nums) {
        // 这个数代表着132中的2
        int secondIn132 = Integer.MIN_VALUE;
        // 单调递减stack
        // 因为是从后往前遍历，所以大的数并且位置还在后面的就先进栈
        // 同时一旦发现比栈顶还大的数，就先把栈顶取出来
        // 这时候栈顶就是132里的2，因为栈顶比当前位置更靠后
        // 而当前的数可以充当3，保证了栈里存放的都是可以维持3 > 2 的 3值
        Stack<Integer> numSt = new Stack<>();
        for (int i = nums.length - 1; i >= 0; i--) {
            int currInt = nums[i];
            // 当前数字小于2，即 pattern 132 中的1找到了，1 < 2
            // 直接返回 true 
            if (currInt < secondIn132) {
                return true;
            }
            // 如果当前数字大于栈顶元素，栈顶数字取出赋值给 2
            // 直到当前元素不大于栈顶元素，然后将当前数字压入栈
            // 保证了栈里存放的都是可以维持3 > 2 的 3值
            while (!numSt.isEmpty() && currInt > numSt.peek()) {
                secondIn132 = numSt.pop();
            }
            numSt.push(currInt);
        }
        return false;
    }
}
// @lc code=end

