import java.util.PriorityQueue;

/*
 * @lc app=leetcode id=179 lang=java
 *
 * [179] Largest Number
 */

// @lc code=start
class Solution {
    public String largestNumber(int[] nums) {
        // 排序的规则是，如果ab > ba 则a排在前面
        // 因为是greater function，反过来比就是从大到小
        PriorityQueue<Integer> numPQ = new PriorityQueue<Integer>((a, b) -> {
            String intA = a.toString(), intB = b.toString();
            return (intB + intA).compareTo(intA + intB);
        });
        for (int num: nums) {
            numPQ.add(num);
        }
        StringBuilder resBuilder = new StringBuilder();
        while (!numPQ.isEmpty()) {
            int currNum = numPQ.poll();
            // 跳过所有leading 0
            if (!numPQ.isEmpty() && resBuilder.length() == 0 && currNum == 0) {
                continue;
            }
            resBuilder.append(currNum);
        }
        return resBuilder.toString();
    }
}
// @lc code=end

