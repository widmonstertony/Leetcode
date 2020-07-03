import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/*
 * @lc app=leetcode id=1102 lang=java
 *
 * [1102] Path With Maximum Minimum Value
 */

// @lc code=start
class Solution {
    // BFS + PriorityQueue解法
    public int maximumMinimumPath(int[][] A) {
        if (A.length == 0 || A[0].length == 0) {
            return 0;
        }
        // 上下左右四个方向
        int[][] dirs = new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        int R = A.length, C = A[0].length;
        boolean[][] visited = new boolean[R][C];
        // PriorityQueue后面接的是greater funciton，就是a是否比b大，是的话就从小到大排列
        // 因为这里需要从大到小排列，所以用b减a
        Queue<Point> pq = new PriorityQueue<Point>((a, b) -> {return b.getValue() - a.getValue();});
        pq.add(new Point(A[0][0], 0, 0));
        int res = Math.min(A[0][0], A[R - 1][C - 1]);
        // bfs去遍历所有路径
        while (!pq.isEmpty()) {
            Point currPoint = pq.poll();
            for (int[] dir : dirs) {
                int newX = currPoint.getX() + dir[0], newY = currPoint.getY() + dir[1];
                // 如果新的方向在地图里，并且没有visit过
                if (newX >= 0 && newX < R && newY >= 0 && newY < C && !visited[newX][newY]) {
                    res = Math.min(res, currPoint.getValue());
                    if (newX == R - 1 && newY == C - 1) {
                        return res;
                    }
                    visited[newX][newY] = true;
                    pq.add(new Point(A[newX][newY], newX, newY));
                }
            }
        }
        return res;
    }
    class Point {
        int mValue, mX, mY;
        public Point(int value, int x, int y) {
            mValue = value;
            mX = x;
            mY = y;
        }
        public int getValue() {
            return mValue;
        }
        public int getX() {
            return mX;
        }
        public int getY() {
            return mY;
        }
    }
    // Union-Find解法
    // public int maximumMinimumPath(int[][] A) {
    //     if (A.length == 0 || A[0].length == 0) {
    //         return 0;
    //     }
    //     // 上下左右四个方向
    //     int[][] dirs = new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
    //     int R = A.length, C = A[0].length;
    //     int[] root = new int[R * C];
    //     for (int i = 0; i < R * C; i++) {
    //         root[i] = -1;
    //     }
    //     LinkedList<List<Integer>> values = new LinkedList<List<Integer>>();
    //     for (int i = 0; i < R; i++) {
    //         for (int j = 0; j < C; j++) {
    //             // 每一个list里记录着值，和x，y坐标
    //             List<Integer> newValueList = new ArrayList<>();
    //             newValueList.add(A[i][j]);
    //             newValueList.add(i);
    //             newValueList.add(j);
    //             values.add(newValueList);
    //         }
    //     }
    //     // 把当前所有的值，按照大小排列，然后从大的开始逐个visit
    //     Collections.sort(values, (o1, o2) -> {
    //         return o2.get(0) - o1.get(0);
    //     });
    //     // 另一种写法
    //     // Collections.sort(values, new Comparator<List<Integer>>(){
    //     //     @Override
    //     //     public int compare(List<Integer> o1, List<Integer> o2) {
    //     //         return o2.get(0) - o1.get(0);
    //     //     }
    //     // });
    //     Set<Integer> visited = new HashSet<>();
    //     visited.add(0);
    //     visited.add(R * C - 1);
    //     int res = Math.min(A[0][0], A[R - 1][C - 1]);
    //     // 只要最开头和最末尾的值都没有连起来，就一直把数值从大到小visit一遍
    //     // 直到在较小的那个点visit过后, 起终两点联通了，那么就是那个值
    //     while (find(0, root) != find(R * C - 1, root)) {
    //         List<Integer> currValues = values.pollFirst();
    //         int currValue = currValues.get(0);
    //         int currX = currValues.get(1);
    //         int currY = currValues.get(2);
    //         if (!visited.contains(currX * C + currY)) {
    //             visited.add(currX * C + currY);
    //         }
    //         res = Math.min(res, currValue);
    //         // 检查新遍历的点，周围的点
    //         for (int[] dir : dirs) {
    //             int newX = currX + dir[0], newY = currY + dir[1];
    //             // 检查新遍历的点，周围的点（值更大）是否已经访问过
    //             // 如果访问过，则和当前的点连起来
    //             if (newX >= 0 && newX < R && newY >= 0 && newY < C && visited.contains(newX * C + newY)) {
    //                 union(currX * C + currY, newX * C + newY, root);
    //             }
    //         }
    //     }
    //     return res;
    // }
    // private int find(int x, int[] root) {
    //     int rootValue = root[x];
    //     if (rootValue < 0) {
    //         return x;
    //     }
    //     // 注意，是大于等于0，因为我们的坐标从0开始
    //     if (root[rootValue] >= 0) {
    //         rootValue = find(root[rootValue], root);
    //         root[x] = rootValue;
    //     }
    //     return rootValue;
    // }
    // private void union(int x, int y, int[] root) {
    //     int rootOfX = find(x, root), rootOfY = find(y, root);
    //     if (rootOfX == rootOfY) {
    //         return;
    //     }
    //     int rootValueOfX = root[rootOfX], rootValueOfY = root[rootOfY];
    //     if (rootValueOfX < rootValueOfY) {
    //         root[rootOfX] += rootValueOfY;
    //         root[rootOfY] = rootOfX;
    //     }
    //     else {
    //         root[rootOfY] += rootValueOfX;
    //         root[rootOfX] = rootOfY;
    //     }
    // }
}
// @lc code=end

