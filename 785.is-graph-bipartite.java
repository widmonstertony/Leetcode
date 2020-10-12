/*
 * @lc app=leetcode id=785 lang=java
 *
 * [785] Is Graph Bipartite?
 */

// @lc code=start
class Solution {
    public boolean isBipartite(int[][] graph) {
        // 将相连的两个顶点染成不同的颜色
        // 使用两种颜色，分别用1和 -1 来表示
        int[] colors = new int[graph.length];
        for (int i = 0; i < graph.length; i++) {
            // 如果该顶点未被访问过，则调用递归函数
            // 如果返回 false，那么说明不是二分图
            if (colors[i] == 0 && !vaild(graph, 1, i, colors)) {
                return false;
            }
        }
        return true;
    }
    private boolean vaild(int[][] graph, int assignColor, int currNode, int[] colors) {
        // 如果当前顶点已经染色
        // 如果该顶点的颜色和将要染的颜色相同，则返回 true，否则返回 false
        if (colors[currNode] != 0) {
            return colors[currNode] == assignColor;
        }
        // 如果没被染色，则将当前顶点染色
        colors[currNode] = assignColor;
        // 然后再遍历与该顶点相连的所有的顶点，调用递归函数
        // 染上与当前颜色相反的颜色，代表是另一个集合
        // 如果无法给它们染上另一种颜色，则说明不是valid的颜色
        for (int j : graph[currNode]) {
            if (!vaild(graph, -1 * assignColor, j, colors)) {
                return false;
            }
        }
        return true;
    }
}
// @lc code=end

