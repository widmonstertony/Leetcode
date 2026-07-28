/*
 * @lc app=leetcode id=1631 lang=java
 *
 * [1631] Path With Minimum Effort
 */

// @lc code=start
class Solution {
    public int minimumEffortPath(int[][] heights) {
        int left = 0, right = 1000000;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (!canReachEndwithK(heights, mid)) {
                left = mid + 1;
            }
            else {
                right = mid - 1;
            }
        }
        return left;
    }
    private boolean canReachEndwithK(int[][] heights, int k) {
        boolean[][] visited = new boolean[heights.length][heights[0].length];
        return canReachEndwithK(heights, k, 0, 0, visited);
    }
    private boolean canReachEndwithK(int[][] heights, int k, int startX, int startY, boolean[][] visited) {
        if (visited[startX][startY]) {
            return false;
        }
        if (startX == heights.length - 1 && startY == heights[startX].length - 1) {
            return true;
        }
        visited[startX][startY] = true;
        final int[][] DIRECTIONS = new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        for (int[] direction : DIRECTIONS) {
            int newX = direction[0] + startX, newY = direction[1] + startY;
            if (newX < 0 || newY < 0 || newX >= heights.length || newY >= heights[newX].length || visited[newX][newY]) {
                continue;
            }
            if (Math.abs(heights[startX][startY] - heights[newX][newY]) > k) {
                continue;
            }
            if (canReachEndwithK(heights, k, newX, newY, visited)) {
                return true;
            }
        }
       return false;
    }
    // brute force
    // int maxSoFar = Integer.MAX_VALUE;
    // public int minimumEffortPath(int[][] heights) {
    //     boolean[][] isVisiting = new boolean[heights.length][heights[0].length];
    //     helper(0, 0, isVisiting, heights, 0);
    //     return maxSoFar;
    // }
    // private int helper(int startX, int startY, boolean[][] isVisiting, int[][] heights, int currMaxDiff){
    //     if (startX == heights.length - 1 && startY == heights[startX].length - 1) {
    //         maxSoFar = Math.min(maxSoFar, currMaxDiff);
    //         return currMaxDiff;
    //     }
    //     int nextMinDiff = Integer.MAX_VALUE;
    //     isVisiting[startX][startY] = true;
    //     final int[][] DIRECTIONS = new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
    //     for (int[] direction : DIRECTIONS) {
    //         int newX = direction[0] + startX, newY = direction[1] + startY;
    //         if (newX < 0 || newY < 0 || newX >= heights.length || newY >= heights[newX].length || isVisiting[newX][newY]) {
    //             continue;
    //         }
    //         int nextMaxDiff = Math.max(currMaxDiff, Math.abs(heights[startX][startY] - heights[newX][newY]));
    //         if (nextMaxDiff >= maxSoFar) {
    //             continue;
    //         }
    //         nextMinDiff = Math.min(helper(newX, newY, isVisiting, heights, nextMaxDiff), nextMinDiff);
    //     }
    //    isVisiting[startX][startY] = false;
    //    return Math.max(currMaxDiff, nextMinDiff);
    // }
}
// @lc code=end

