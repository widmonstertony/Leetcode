import java.util.Arrays;

/*
 * @lc app=leetcode id=1167 lang=java
 *
 * [1167] Minimum Cost to Connect Sticks
 */

// @lc code=start
class Solution {
    public int connectSticks(int[] sticks) {
        // 用一个从小到大的heap把所有stick保存起来
        PriorityQueue<Integer> stickPQ = new PriorityQueue<>();
        for (int stick: sticks) {
            stickPQ.offer(stick);
        }
        
        int res = 0;
        while (stickPQ.size() > 1) {
            // 把第一小和第二小的数合并
            int currCost = stickPQ.poll() + stickPQ.poll();;
            res += currCost;
            stickPQ.offer(currCost);
        }
        return res;
    }
}
// @lc code=end

