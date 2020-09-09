import java.util.Deque;
import java.util.Map;
import java.util.PriorityQueue;

/*
 * @lc app=leetcode id=239 lang=java
 *
 * [239] Sliding Window Maximum
 */

// @lc code=start
class Solution {
    public int[] maxSlidingWindow(int[] nums, int k) {
        int[] res = new int[nums.length - k + 1];
        int resCnt = 0;
        // 用双向队列保存数字的下标
        Deque<Integer> idxDeque = new LinkedList<>();
        for (int i = 0; i < nums.length; i++) {
            // 如果此时队列的首元素是 i-k 的话，表示此时窗口向右移了一步，则移除队首元素
            if (!idxDeque.isEmpty() && idxDeque.peekFirst() == i - k) {
                idxDeque.pollFirst();
            }
            // 比较队尾元素和将要进来的值，如果小的话就都移除
            while (!idxDeque.isEmpty() && nums[idxDeque.peekLast()] < nums[i]) {
                idxDeque.pollLast();
            }
            idxDeque.offer(i);
            // 如果当前值已经大于k
            // 说明从现在开始每次的循环都需要把当前的最大数字保存下来
            if (i >= k - 1) {
                // 把队首元素加入结果
                res[resCnt] = nums[idxDeque.peekFirst()];
                resCnt++;
            }
        }
        return res;
    }
    
    // PriorityQueue, o(nlogn)
    // public int[] maxSlidingWindow(int[] nums, int k) {
    //     int[] res = new int[nums.length - k + 1];
    //     int resCnt = 0;
    //     // 从大到小排，这样peek第一个数就是最大数
    //     // 这里的int[]的0位是数字，1位是数字的坐标
    //     PriorityQueue<int[]> maxHeap = new PriorityQueue<>((a, b) -> {
    //         return b[0] - a[0];
    //     });
    //     for (int i = 0; i < nums.length; i++) {
    //         // 如果此时i减去队列的首元素坐标大于等于k的话
    //         // 表示此时以k为长度的窗口已经移出了首元素的位置，移除列队首元素
    //         while (!maxHeap.isEmpty() && i - maxHeap.peek()[1] >= k) {
    //             maxHeap.poll();
    //         }
    //         maxHeap.offer(new int[]{nums[i], i});
    //         // 如果当前值已经大于k
    //         // 说明从现在开始每次的循环都需要把当前的最大数字保存下来
    //         if (i >= k - 1) {
    //             res[resCnt] = maxHeap.peek()[0];
    //             resCnt++;
    //         }
    //     }
    //     return res;
    // }
}
// @lc code=end

