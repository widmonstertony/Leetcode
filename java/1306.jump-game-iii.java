/*
 * @lc app=leetcode id=1306 lang=java
 *
 * [1306] Jump Game III
 */

// @lc code=start
class Solution {
    // bfs 
    public boolean canReach(int[] arr, int start) {
        boolean[] visited = new boolean[arr.length];
        Queue<Integer> intQueue = new LinkedList();
        intQueue.offer(start);
        
        while (!intQueue.isEmpty()) {
            int currIdx = intQueue.poll();
            if (currIdx < 0 || currIdx >= arr.length || visited[currIdx]) {
                continue;
            }
            if (arr[currIdx] == 0) {
                return true;
            }
            visited[currIdx] = true;
            intQueue.offer(currIdx + arr[currIdx]);
            intQueue.offer(currIdx - arr[currIdx]);
        }
        return false;
    }
}
// @lc code=end

