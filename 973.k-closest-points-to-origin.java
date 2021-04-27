/*
 * @lc app=leetcode id=973 lang=java
 *
 * [973] K Closest Points to Origin
 */

// @lc code=start
class Solution {
    public int[][] kClosest(int[][] points, int k) {
        PriorityQueue<int[]> locationPQ = new PriorityQueue<>((a, b) -> {
            int distanceA = a[0] * a[0] + a[1] * a[1];
            int distanceB = b[0] * b[0] + b[1] * b[1];
            if (distanceB - distanceA == 0) {
                return b[0] - a[0];
            }
            return distanceB - distanceA;
        });
        for (int[] location: points) {
            locationPQ.offer(location);
            // 如果满了，就把head移出，也就是最大的那个
            if (locationPQ.size() > k) {
                locationPQ.poll();
            }
        }
        int[][] res = new int[k][2];
        for (int i = 0; i < res.length; i++) {
            res[i] = locationPQ.poll();
        }
        return res;
    }
}
// @lc code=end

