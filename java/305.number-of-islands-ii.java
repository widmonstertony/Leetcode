import java.util.ArrayList;
import java.util.List;
/*
 * @lc app=leetcode id=305 lang=java
 *
 * [305] Number of Islands II
 */

// @lc code=start
class Solution {
    public List<Integer> numIslands2(int m, int n, int[][] positions) {
        // roots 设为-1，正数代表它的root的那个数
        // 因为数从0开始，所以起始设为-1
        int[] roots = new int[m * n];
        int[] rank = new int[m * n];
        for (int i = 0; i < m * n; i++) {
            roots[i] = -1;
            rank[i] = 0;
        }
        int res = 0;
        List<Integer> resList = new ArrayList<>();
        int[][] directions = new int[][]{{0, -1}, {-1, 0}, {0, 1}, {1, 0}};
        for (int[] position : positions) {
            // position[0]代表行，position[1]代表列
            // 列数x第几行，代表这行之前有多少个数字（因为是行乘以列）
            // 再加上当前行所在的列数，就是它唯一的id
            int id = n * position[0] + position[1];
            // 如果当前值的root值大于等于0，说明它有root
            if (roots[id] >= 0) {
                resList.add(res);
                continue;
            }
            roots[id] = id;
            res++;
            for (int[] direction: directions) {
                int newX = position[0] + direction[0], newY = position[1] + direction[1];
                int newId = n * (newX) + newY;
                // 如果roots[newId]小于0，代表当前值是0，还没有被初始化过，不应该继续计算当前方向的值
                if (newX < 0 || newX >= m || newY < 0 || newY >= n || roots[newId] < 0) {
                    continue;
                }
                int rootOfId = find(id, roots), rootOfNewId = find(newId, roots);
                // 两个岛可以合并成一个岛
                if (rootOfId == rootOfNewId) {
                    continue;
                }
                res--;
                if (rank[rootOfId] > rank[rootOfNewId]) {
                    roots[rootOfNewId] = rootOfId;
                }
                else if (rank[rootOfId] < rank[rootOfNewId]) {
                    roots[rootOfId] = rootOfNewId;
                }
                else {
                    roots[rootOfId] = rootOfNewId;
                    rank[rootOfNewId]++;
                }
            }
            resList.add(res);
        }
        return resList;
    }
    private int find(int id, int[] roots) {
        // 如果当前id的root不是自己，说明它有root
        int rootOfId = roots[id];
        if (rootOfId == id) {
            return id;
        }
        if (roots[rootOfId] > 0) {
            rootOfId = find(rootOfId, roots);
            roots[id] = rootOfId;
        }
        return rootOfId;
    }
}
// @lc code=end

