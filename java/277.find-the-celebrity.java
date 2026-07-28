/*
 * @lc app=leetcode id=277 lang=java
 *
 * [277] Find the Celebrity
 */

// @lc code=start
/* The knows API is defined in the parent class Relation.
      boolean knows(int a, int b); */
public class Solution extends Relation {
    // 还可以再实现一个hashmap来缓存knows的结果来减少call
    public int findCelebrity(int n) {
        int celebrityCadidate = 1;
        // 首先找到一个名人候选人
        // 这个候选人从他开始的后面的人都认识他
        for (int i = 0; i < n; i++) {
            if (knows(celebrityCadidate, i)) {
                celebrityCadidate = i;
            }
        }
        // 然后再验证候选人前面的人也认识他，并且他不认识前面的人
        for (int i = 0; i < celebrityCadidate; i++) {
            if (knows(celebrityCadidate, i) || !knows(i, celebrityCadidate)) {
                return -1;
            }
        }
        // 再验证候选人不认识他后面的人
        for (int i = celebrityCadidate + 1; i < n; i++) {
            if (!knows(i, celebrityCadidate)) {
                return -1;
            }
        }
        return celebrityCadidate;
    }
}
// @lc code=end

