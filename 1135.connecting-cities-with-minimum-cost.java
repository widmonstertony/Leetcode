import java.util.Arrays;

/*
 * @lc app=leetcode id=1135 lang=java
 *
 * [1135] Connecting Cities With Minimum Cost
 */

// @lc code=start
class Solution {
    public int minimumCost(int N, int[][] connections) {
        // 先把所有连接按照cost从小到大排列
        Arrays.sort(connections, (a, b) -> {
            return a[2] - b[2];
        });
        int costSum = 0;
        int[] root = new int[N + 1];
        Arrays.fill(root, -1);
        for (int [] connection : connections) {
            // 如果当前两个edge可以连接，则把cost加到Sum里
            // 并且确认是否只剩下一个edge了，如果是，说明全部都连在一起了
            if (union(connection[0], connection[1], root)) {
                costSum += connection[2];
                N--;
                if (N == 1) {
                    return costSum;
                }
            }
        }
        return -1;
    }
    private boolean union(int a, int b, int[] root) {
        int rootA = find(a, root), rootB = find(b, root);
        if (rootA == rootB) {
            return false;
        }
        int sizeA = root[rootA], sizeB = root[rootB];
        if (sizeA < sizeB) {
            root[rootA] += sizeB;
            root[rootB] = rootA;
        }
        else {
            root[rootB] += sizeA;
            root[rootA] = rootB;
        }
        return true;
    }
    private int find(int x, int[] root) {
        int rootX = root[x];
        if (rootX < 0) {
            return x;
        }
        if (root[rootX] > 0) {
            rootX = find(rootX, root);
            root[x] = rootX;
        }
        return rootX;
    }
}
// @lc code=end

