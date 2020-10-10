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
// 在hasnext的时候把list里的内容一个一个倒着放进stack
public class NestedIterator implements Iterator<Integer> {

    Stack<NestedInteger> flattenSt;
    public NestedIterator(List<NestedInteger> nestedList) {
        flattenSt = new Stack<>();
        for (int i = nestedList.size() - 1; i >= 0; i--) {
            flattenSt.push(nestedList.get(i));
        }
    }

    @Override
    public Integer next() {
        if (hasNext()) {
            return flattenSt.pop().getInteger();
        }
        return -1;
    }

    @Override
    public boolean hasNext() {
        while (!flattenSt.isEmpty()) {
            NestedInteger topNest = flattenSt.peek();
            if (topNest.isInteger()) {
                return true;
            }
            else {
                flattenSt.pop();
                List<NestedInteger> nestedList = topNest.getList();
                for (int i = nestedList.size() - 1; i >= 0; i--) {
                    flattenSt.push(nestedList.get(i));
                }
            }
        }
        return false;
    }
}

// 直接压扁成queue的做法
// public class NestedIterator implements Iterator<Integer> {

//     Queue<Integer> flattenList;
//     public NestedIterator(List<NestedInteger> nestedList) {
//         flattenList = new LinkedList<>();
//         for (NestedInteger currNext : nestedList) {
//             helper(currNext, flattenList);
//         }
//     }

//     private void helper(NestedInteger currNext, Queue<Integer> flattenList) {
//         if (currNext.isInteger()) {
//             flattenList.add(currNext.getInteger());
//         }
//         else {
//             for (NestedInteger nextInt : currNext.getList()) {
//                 helper(nextInt, flattenList);
//             }
//         }
//     }

//     @Override
//     public Integer next() {
//         if (hasNext()) {
//             return flattenList.poll();
//         }
//         else {
//             return -1;
//         }
//     }

//     @Override
//     public boolean hasNext() {
//         return !flattenList.isEmpty();
//     }
// }

/**
 * Your NestedIterator object will be instantiated and called as such:
 * NestedIterator i = new NestedIterator(nestedList);
 * while (i.hasNext()) v[f()] = i.next();
 */
// @lc code=end

