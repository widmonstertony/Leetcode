/*
 * @lc app=leetcode id=341 lang=java
 *
 * [341] Flatten Nested List Iterator
 */

// @lc code=start
/**
 * // This is the interface that allows for creating nested lists.
 * // You should not implement it, or speculate about its implementation
 * public interface NestedInteger {
 *
 *     // @return true if this NestedInteger holds a single integer, rather than a nested list.
 *     public boolean isInteger();
 *
 *     // @return the single integer that this NestedInteger holds, if it holds a single integer
 *     // Return null if this NestedInteger holds a nested list
 *     public Integer getInteger();
 *
 *     // @return the nested list that this NestedInteger holds, if it holds a nested list
 *     // Return null if this NestedInteger holds a single integer
 *     public List<NestedInteger> getList();
 * }
 */
public class NestedIterator implements Iterator<Integer> {

    List<Integer> flattenList;
    int currPos;
    public NestedIterator(List<NestedInteger> nestedList) {
        flattenList = new ArrayList<>();
        for (NestedInteger currNext : nestedList) {
            helper(currNext, flattenList);
        }
        currPos = 0;
    }

    private void helper(NestedInteger currNext, List<Integer> flattenList) {
        if (currNext.isInteger()) {
            flattenList.add(currNext.getInteger());
        }
        else {
            for (NestedInteger nextInt : currNext.getList()) {
                helper(nextInt, flattenList);
            }
        }
    }

    @Override
    public Integer next() {
        if (hasNext()) {
            return flattenList.get(currPos++);
        }
        else {
            return -1;
        }
    }

    @Override
    public boolean hasNext() {
        return currPos <= flattenList.size() - 1;
    }
}

/**
 * Your NestedIterator object will be instantiated and called as such:
 * NestedIterator i = new NestedIterator(nestedList);
 * while (i.hasNext()) v[f()] = i.next();
 */
// @lc code=end

