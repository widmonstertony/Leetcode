/*
 * @lc app=leetcode id=385 lang=java
 *
 * [385] Mini Parser
 */

// @lc code=start
/**
 * // This is the interface that allows for creating nested lists.
 * // You should not implement it, or speculate about its implementation
 * public interface NestedInteger {
 *     // Constructor initializes an empty nested list.
 *     public NestedInteger();
 *
 *     // Constructor initializes a single integer.
 *     public NestedInteger(int value);
 *
 *     // @return true if this NestedInteger holds a single integer, rather than a nested list.
 *     public boolean isInteger();
 *
 *     // @return the single integer that this NestedInteger holds, if it holds a single integer
 *     // Return null if this NestedInteger holds a nested list
 *     public Integer getInteger();
 *
 *     // Set this NestedInteger to hold a single integer.
 *     public void setInteger(int value);
 *
 *     // Set this NestedInteger to hold a nested list and adds a nested integer to it.
 *     public void add(NestedInteger ni);
 *
 *     // @return the nested list that this NestedInteger holds, if it holds a nested list
 *     // Return null if this NestedInteger holds a single integer
 *     public List<NestedInteger> getList();
 * }
 */
class Solution {
    public NestedInteger deserialize(String s) {
        NestedInteger resNest = new NestedInteger();
        if (s == null || s.length() == 0) {
            return resNest;
        }
        // 如果首字符不是'['的话
        if (s.charAt(0) != '[') {
            // 检查是不是数组，不是的话直接返回当前s的数值
            if (s.indexOf(',') == -1) {
                resNest.setInteger(Integer.valueOf(s));
            }
            // 否则把数组里的每个数加进resNest
            else {
                int start = 0;
                for (int i = 1; i < s.length(); i++) {
                    if (s.charAt(i) == ',' || i == s.length() - 1) {
                        resNest.add(deserialize(s.substring(start, i)));
                        start = i + 1;
                    }
                }
            }
            return resNest;
        }
        if (s.length() <= 2) {
            return resNest;
        }
        // start代表左括号开始之后的那个坐标
        // leftCnt代表还剩几个left没有被消掉
        int start = 1, leftCnt = 0;
        for (int i = 1; i < s.length(); i++) {
            char currChar = s.charAt(i);
            // 如果leftCnt为0，代表从start到i是同一深度
            // 并且当时遇到了逗号或者末尾，说明可以对从start到i这部分同一深度的字符串进行处理
            if (leftCnt == 0 && (currChar == ',' || i == s.length() - 1)) {
                resNest.add(deserialize(s.substring(start, i)));
                // 处理完后把start移到i的后面
                start = i + 1;
            }
            else if (currChar == '[') {
                leftCnt++;
            }
            else if (currChar == ']') {
                leftCnt--;
            }
        }
        return resNest;
    }
}
// @lc code=end

