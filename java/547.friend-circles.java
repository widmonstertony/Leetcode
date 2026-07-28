/*
 * @lc app=leetcode id=547 lang=java
 *
 * [547] Friend Circles
 */

// @lc code=start
class Solution {
    public int findCircleNum(int[][] M) {
        if (M == null || M.length == 0 || M[0].length == 0) {
            return 0;
        }
        int numOfStudent = M.length;
        boolean visited[] = new boolean[numOfStudent];
        int circleNum = 0;
        // 遍历每个学生，visit每个学生的朋友，再dfs visit学生的朋友的朋友，
        for (int i = 0; i < numOfStudent; i++) {
            if (!visited[i]) {
                circleNum++;
            }
            dfsVisitedAllFriends(i, visited, M);
        }
        return circleNum;
    }

    private void dfsVisitedAllFriends(int i, boolean[] visited, int[][] M) {
        if (visited[i]) {
            return;
        }
        visited[i] = true;
        for (int j = 0; j < M.length; j++) {
            if (M[i][j] == 0 || visited[j]) {
                continue;
            }
            dfsVisitedAllFriends(j, visited, M);
        }
    }
}
// @lc code=end

