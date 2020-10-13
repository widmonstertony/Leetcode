/*
 * @lc app=leetcode id=496 lang=java
 *
 * [496] Next Greater Element I
 */

// @lc code=start
class Solution {
    public int[] nextGreaterElement(int[] nums1, int[] nums2) {
        int[] resArr = new int[nums1.length];
        // 一个单调递减的stack
        // 一旦遍历时的数不递减了，说明找到那个大于栈顶元素的数字了
        Stack<Integer> numSt = new Stack<>();
        Map<Integer, Integer> numMap = new HashMap<>();
        for (int num : nums2) {
            // 如果此时栈不为空，且栈顶元素小于当前数字
            // 说明当前数字就是栈顶元素的右边第一个较大数
            while (!numSt.isEmpty() && numSt.peek() < num) {
                // 建立二者的映射，并且去除当前栈顶元素
                numMap.put(numSt.pop(), num);
            }
            // 最后将当前遍历到的数字压入栈
            numSt.push(num);
        }
        // 直接通过哈希表快速的找到子集合中数字的右边较大值
        for (int i = 0; i < nums1.length; i++) {
            resArr[i] = numMap.getOrDefault(nums1[i], -1);
        }
        return resArr;
    }
}
// @lc code=end

