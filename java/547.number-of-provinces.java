/*
 * @lc app=leetcode id=547 lang=java
 *
 * [547] Number of Provinces
 */

// @lc code=start
class Solution {
    public int findCircleNum(int[][] isConnected) {
        if (isConnected == null || isConnected.length == 0 || isConnected[0].length == 0) {
            return 0;
        }
        int numOfStudent = isConnected.length;
        boolean visited[] = new boolean[numOfStudent];
        int circleNum = 0;
        // 遍历每个学生，visit每个学生的朋友，再dfs visit学生的朋友的朋友，
        for (int i = 0; i < numOfStudent; i++) {
            if (!visited[i]) {
                circleNum++;
            }
            dfsVisitedAllFriends(i, visited, isConnected);
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
    // Union Find解法 O(N^3)
    // public int findCircleNum(int[][] isConnected) {
    //     int[] root = new int[isConnected.length];
    //     Arrays.fill(root, -1);
    //     for (int i = 0; i < isConnected.length; i++) {
    //         for (int j = 0; j < isConnected[i].length; j++) {
    //             if (isConnected[i][j] == 1 && i != j) {
    //                 union(i, j, root);
    //             }
    //         }
    //     }
    //     int circleCnt = 0;
    //     for (int i = 0; i < root.length; i++) {
    //         if (root[i] < 0) {
    //             circleCnt++;
    //         }
    //     }
    //     return circleCnt;
    // }
    
    // private void union(int x, int y, int[] root) {
    //     int rootOfX = find(x, root), rootOfY = find(y, root);
    //     // 把数字小的那个作为root，把数字大的root指向数字小的字母
    //     if (rootOfX < rootOfY) {
    //         root[rootOfX] = rootOfY;
    //     } 
    //     else if (rootOfX > rootOfY) {
    //         root[rootOfY] = rootOfX;
    //     }
    // }
    
    // private int find(int x, int[] root) {
    //     int rootValue = root[x];
    //     if (rootValue == -1) {
    //         return x;
    //     }
    //     if (root[rootValue] != -1) {
    //         rootValue = find(rootValue, root);
    //         root[x] = rootValue;
    //     }
    //     return rootValue;
    // }
}
// @lc code=end

