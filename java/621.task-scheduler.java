import java.util.PriorityQueue;

/*
 * @lc app=leetcode id=621 lang=java
 *
 * [621] Task Scheduler
 */

// @lc code=start
class Solution {
    public int leastInterval(char[] tasks, int n) {

        // 分块，每块能装n+1个任务
        int res = 0, cycleNum = n + 1;

        int[] charMap = new int[26];
        
        // 把统计好的个数都存入优先队列中，大的次数会在队列的前面
        PriorityQueue<Integer> pq = new PriorityQueue<Integer>((a, b) -> {
            return charMap[b] - charMap[a];
        });

        for (char task : tasks) {
            charMap[task - 'A']++;
        }

        for (int i = 0; i < 26; i++) {
            if (charMap[i] > 0) {
                pq.add(i);
            }
        }

        while (!pq.isEmpty()) {
            int currCnt = 0;
            List<Integer> currTaskChar = new ArrayList<>();

            // 从优先队列中取，每个任务取一个，装到一个临时数组中
            for (int i = 0; i < cycleNum; i++) {

                if (!pq.isEmpty()) {
                    currTaskChar.add(pq.poll());
                    currCnt++;
                }

            }   

            // 遍历取出的任务
            // 对于每个任务，将其哈希表映射的次数减1
            for (int taskNum: currTaskChar) {

                charMap[taskNum]--;
                //如果减1后，次数仍大于0，则将此任务次数再次排入队列中
                if (charMap[taskNum] > 0) {
                    pq.add(taskNum);
                }
            }

            // 遍历完后如果队列中任务数少于n+1个
            // 那就用currCnt来记录真实取出的个数，当队列为空时，就加上cnt的个数
            if (pq.isEmpty()) {
                res += currCnt;
            }
            // 如果队列不为空，说明该块全部被填满，则结果加上n+1
            else {
                res += cycleNum;
            }

        }

        return res;
    }
}
// @lc code=end

