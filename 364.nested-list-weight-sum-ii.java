/*
 * @lc app=leetcode id=364 lang=java
 *
 * [364] Nested List Weight Sum II
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
 *     // Return empty list if this NestedInteger holds a single integer
 *     public List<NestedInteger> getList();
 * }
 */
class Solution {
    public int depthSumInverse(List<NestedInteger> nestedList) {
        int currSum = 0;
        // 把同一个level和那个level的总数存进HashMap
        Map<Integer, Integer> levelMap = new HashMap<>();
        for (NestedInteger currInteger: nestedList) {
            getDepth(currInteger, levelMap, 0);
        }
        int maxLevel = Integer.MIN_VALUE;
        for (int level : levelMap.keySet()) {
            maxLevel = Math.max(level, maxLevel);
        }
        for (int level : levelMap.keySet()) {
            currSum += (maxLevel + 1 - level) * levelMap.get(level);
        }
        return currSum;
    }
    private void getDepth(NestedInteger currInteger, Map<Integer, Integer> levelMap, int currLevel) {
        if (currInteger.isInteger()) {
            levelMap.put(currLevel, levelMap.getOrDefault(currLevel, 0) + currInteger.getInteger());
        }
        else {
            for (NestedInteger nextInteger: currInteger.getList()) {
                getDepth(nextInteger, levelMap, currLevel + 1);
            }
        }
    }
}
// @lc code=end

