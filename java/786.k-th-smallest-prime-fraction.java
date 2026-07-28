import java.util.PriorityQueue;

/*
 * @lc app=leetcode id=786 lang=java
 *
 * [786] K-th Smallest Prime Fraction
 */

// @lc code=start
class Solution {
    public int[] kthSmallestPrimeFraction(int[] A, int K) {
        // priority queue, 每个元素是个array，存放分子和分母的坐标
        PriorityQueue<int[]> pq = new PriorityQueue<>(
            (a, b) -> {
                return Double.compare((double)A[a[0]]/A[a[1]], (double)A[b[0]]/A[b[1]]);
            }
        );
        // 先把最小的那几个放进去，也就是分母最大时，把分子依次放进去
        for (int i = 0; i < A.length; i++) {
            pq.offer(new int[]{i, A.length - 1});
        }
        // 然后循环K次
        while(--K > 0) {
            // 每次把最小的拿出来
            // 同时把和最小的相同分子，但是分母小一点的那个数放进去
            // 因为一定比当前拿出来的数大，所以一定不在第K之前
            int[] t = pq.poll();
            pq.offer(new int[]{t[0], t[1] - 1});
        }
        // pq当前最小的就是第K个最小的
        return new int[]{A[pq.peek()[0]], A[pq.peek()[1]]};
    }
}
// @lc code=end

