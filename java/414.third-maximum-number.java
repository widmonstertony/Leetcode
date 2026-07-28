import java.util.PriorityQueue;

/*
 * @lc app=leetcode id=414 lang=java
 *
 * [414] Third Maximum Number
 */

// @lc code=start
class Solution {
    public int thirdMax(int[] nums) {
        // 因为nums里有Integer min value
        // 所以这里用long的min value来判断是否有没有被修改过
        long firstMax = Long.MIN_VALUE, secondMax = firstMax, thirdMax = firstMax;
        for (int num : nums) {
            if (num > firstMax) {
                thirdMax = secondMax;
                secondMax = firstMax;
                firstMax = num;
            }
            else if (num > secondMax && num != firstMax) {
                thirdMax = secondMax;
                secondMax = num;
            }
            else if (num > thirdMax && num != secondMax && num != firstMax) {
                thirdMax = num;
            }
        }
        if (thirdMax < Integer.MIN_VALUE) {
            return (int)firstMax;
        }
        return (int)thirdMax;
    }
    // public int thirdMax(int[] nums) {
    //     // 维护一个size为3的PQ
    //     PriorityQueue<Integer> numPQ = new PriorityQueue<>();
    //     Set<Integer> numSet = new HashSet<>();
    //     for (int num : nums) {
    //         // 这里可以用PQ的contains，因为只有3的size，查起来也很快
    //         if (numSet.contains(num)) {
    //             continue;
    //         }
    //         numSet.add(num);
    //         numPQ.add(num);
    //         if (numPQ.size() > 3) {
    //             numPQ.poll();
    //         }
    //     }
    //     int res = 0;
    //     if (numPQ.size() == 3) {
    //         return numPQ.poll();
    //     }
    //     while (!numPQ.isEmpty()) {
    //         res = numPQ.poll();
    //     }
    //     return res;
    // }
}
// @lc code=end

