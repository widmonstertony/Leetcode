/*
 * @lc app=leetcode id=685 lang=java
 *
 * [685] Redundant Connection II
 */

// @lc code=start
class Solution {
    final int MAX_EDGE_VAL = 1000;

    public int[] findRedundantDirectedConnection(int[][] edges) {
        int n = edges.length;
        int[] root, first, second;
        root = new int[n + 1];
        first = new int[]{};
        second = new int[]{};
        for (int i = 0; i < n + 1; i++) {
            root[i] = 0;
        }
        // 先找入度为2的点
        for (int[] edge : edges) {
            // 如果edge[1]的root为0，代表它没有root
            // 则把它的root定为edge[0]
            if (root[edge[1]] == 0) {
                root[edge[1]] = edge[0];
            }
            // 否则代表edge[1]同时也有另一个root
            // 说明edge[1]是一个入度至少为2的点
            // 把edge[1]保存下来
            else {
                first = new int[]{root[edge[1]], edge[1]};
                second = new int[]{edge[0], edge[1]};
                edge[1] = 0;
                System.out.println(root[edge[1]]);
            }
        }
        // 重置root数组，确保初始状态都是自己是root的状态
        for (int i = 0; i < n + 1; ++i) {
            root[i] = i;
        }
        for (int[] edge : edges) {
            // 如果当前edge后面那个数为0，
            // 代表是入度为2的那个点，已经提前处理了
            if (edge[1] == 0) {
                continue;
            }
            // Union操作
            int x = find(root, edge[0]), y = find(root, edge[1]);
            // 如果两个edge是同一root
            if (x == y) {
                // 如果first存在，代表属于2度并且有环的情况
                // [[1,2], [2,3], [3,1],[4,3]]
                // 返回的是组成环，且组成入度为2的那条边 
                // 也就是[2, 3]
                if (first.length != 0) {
                    return first;
                }
                // 如果不存在
                // 则返回那个最后加入刚好就组成环的那条边
                else {
                    return edge;
                }
            }
            else {
                root[x] = y;
            }
        }
        // 如果没有环，则返回那条产生入度为2的后加入的那条边
        return second;
    }
    private int find(int[] root, int x) {
        if (root[x] != x) {
            root[x] = find(root, root[x]);
        }
        return root[x];
    }
}
// @lc code=end

