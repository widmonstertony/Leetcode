import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/*
 * @lc app=leetcode id=281 lang=java
 *
 * [281] Zigzag Iterator
 */

// @lc code=start
public class ZigzagIterator {
    Queue<Iterator<Integer>> listItQueue;
    // 这个写法可以兼容不止两个list，多个list也可以
    public ZigzagIterator(List<Integer> v1, List<Integer> v2) {
        listItQueue = new LinkedList<>();
        listItQueue.add(v1.iterator());
        listItQueue.add(v2.iterator());
    }
    public int next() {
        while (!listItQueue.isEmpty() && hasNext()) {
            Iterator<Integer> currList = listItQueue.poll();
            if (currList.hasNext()) {
                listItQueue.add(currList);
                return currList.next();
            }
            else {
                continue;
            }
        }
        return -1;
    }
    public boolean hasNext() {
        while (!listItQueue.isEmpty()) {
            Iterator<Integer> currList = listItQueue.peek();
            if (currList.hasNext()) {
                return true;
            }
            else {
                listItQueue.poll();
            }
        }
        return false;
    }
}
// public class ZigzagIterator {
//     private Iterator<Integer> v1It, v2It;
//     private boolean isUsingV1;
//     public ZigzagIterator(List<Integer> v1, List<Integer> v2) {
//         this.v1It = v1.iterator();
//         this.v2It = v2.iterator();
//         isUsingV1 = true;
//     }

//     public int next() {
//         isUsingV1 = !isUsingV1;
//         if (!isUsingV1) {
//             if (v1It.hasNext()) {
//                 return v1It.next();
//             }
//             else {
//                 return v2It.next();
//             }
//         }
//         else {
//             if (v2It.hasNext()) {
//                 return v2It.next();
//             }
//             else {
//                 return v1It.next();
//             }
//         }
//     }

//     public boolean hasNext() {
//         return v1It.hasNext() || v2It.hasNext();
//     }
// }
/**
 * Your ZigzagIterator object will be instantiated and called as such:
 * ZigzagIterator i = new ZigzagIterator(v1, v2);
 * while (i.hasNext()) v[f()] = i.next();
 */
// @lc code=end

