import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.List;

/*
 * @lc app=leetcode id=313 lang=java
 *
 * [313] Super Ugly Number
 */

// @lc code=start
class Solution {
    public int nthSuperUglyNumber(int n, int[] primes) {
        if (n == 1) {
            return 1;
        }
        n--;
        int size = primes.length, res = 0;
        ArrayList<Integer>[] v = new ArrayList[size];
        for (int i = 0; i < size; i++) {
            v[i] = new ArrayList<>();
            v[i].add(primes[i]);
        }
        PriorityQueue<int[]> minHeap = new PriorityQueue<>((a, b) -> {
            if (a[0] == b[0]) {
                return a[1] - b[1];
            }
            return a[0] - b[0];
        });
        for (int i = 0; i < size; i++) {
            minHeap.offer(new int[]{primes[i], i});
        }
        int[] indexes = new int[size];
        while (n >= 1) {
            int[] currArr = minHeap.poll();
            res = currArr[0];
            for (int i = currArr[1]; i < size; i++) {
                v[i].add(currArr[0] * primes[i]);
            }
            minHeap.add(new int[]{v[currArr[1]].get(++indexes[currArr[1]]), currArr[1]});
            n--;
        }
        return res;
    }

}
// @lc code=end

