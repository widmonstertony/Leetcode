/*
 * @lc app=leetcode id=261 lang=java
 *
 * [261] Graph Valid Tree
 */

// @lc code=start
class Solution {
    public boolean validTree(int n, int[][] edges) {
        // Union Find 解法
        int[] rootTree = new int[n];
        for (int i = 0; i < n; i++) {
            rootTree[i] = -1;
        }
        for (int[] edge : edges) {
            // 如果不能union，说明有循环，不是valid的tree
            if (!union(edge[0], edge[1], rootTree)) {
                return false;
            }
        }
        // 如果所有edges的长度和number的数量不一样，说明没有全部连起来
        return edges.length == n - 1;
    }
    // 把x和y的root连起来
    private boolean union(int x, int y, int[] rootTree) {
        int rootOfX = find(x, rootTree), rootOfY = find(y, rootTree);
        // 如果两个edge的root一样，说明造成了循环，不能union
        if (rootOfX == rootOfY) {
            return false;
        }
        int valueOfX = rootTree[rootOfX], valueOfY = rootTree[rootOfY];
        if (valueOfX < valueOfY) {
            rootTree[rootOfX] += valueOfY;
            rootTree[rootOfY] = rootOfX;
        }
        else {
            rootTree[rootOfY] += valueOfX;
            rootTree[rootOfX] = rootOfY;
        }
        return true;
    }
    private int find(int x, int[] rootTree) {
        int root = rootTree[x];
        if (root < 0) {
            return x;
        }
        // 如果root的parent不是root，那么继续找root
        if (rootTree[root] > 0) {
            root = find(root, rootTree);
            rootTree[x] = root;
        }
        return root;
    }
}
// @lc code=end

