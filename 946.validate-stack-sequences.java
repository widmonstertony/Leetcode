/*
 * @lc app=leetcode id=946 lang=java
 *
 * [946] Validate Stack Sequences
 */

// @lc code=start
class Solution {
    public boolean validateStackSequences(int[] pushed, int[] popped) {
        Stack<Integer> intSt = new Stack<>();
        int j = 0;
        // greedy 一直push直到有可以pop的就pop
        for (int i = 0; i < pushed.length; i++) {
            intSt.push(pushed[i]);
            while (!intSt.isEmpty() && intSt.peek() == popped[j]) {
                intSt.pop();
                j++;
            }
        }
        return j == popped.length;
    }
}
// @lc code=end

