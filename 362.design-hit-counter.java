/*
 * @lc app=leetcode id=362 lang=java
 *
 * [362] Design Hit Counter
 */

// @lc code=start
class HitCounter {
    Queue<Integer> timeQueue;
    /** Initialize your data structure here. */
    public HitCounter() {
        timeQueue = new LinkedList<>();
    }
    
    /** Record a hit.
        @param timestamp - The current timestamp (in seconds granularity). */
    public void hit(int timestamp) {
        timeQueue.offer(timestamp);
    }
    
    /** Return the number of hits in the past 5 minutes.
        @param timestamp - The current timestamp (in seconds granularity). */
    public int getHits(int timestamp) {
        // 把离现在超过5分钟的timestamp删掉
        while (!timeQueue.isEmpty() && timestamp - timeQueue.peek() >= 300) {
            timeQueue.poll();
        }
        return timeQueue.size();
    }
}

/**
 * Your HitCounter object will be instantiated and called as such:
 * HitCounter obj = new HitCounter();
 * obj.hit(timestamp);
 * int param_2 = obj.getHits(timestamp);
 */
// @lc code=end

