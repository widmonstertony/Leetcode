/*
 * @lc app=leetcode id=323 lang=java
 *
 * [323] Number of Connected Components in an Undirected Graph
 */

// @lc code=start
class Solution {
    public int countComponents(int n, int[][] edges) {
        if (edges == null) {
            return 0;
        }
        int[] roots = new int[n];
        // root值都为-1，正数代表父亲，负数代表连了多少个数
        for (int i = 0; i < n; i++) {
            roots[i] = -1;
        }
        int res = n;
        for (int i = 0; i < edges.length; i++) {
            int rootValueOfX = find(roots, edges[i][0]), rootValueOfY = find(roots, edges[i][1]);
            if (rootValueOfX == rootValueOfY) {
                continue;
            }
            res--;
            // 把连的少的那个数连到连的多的那个数上
            int xValue = roots[rootValueOfX], yValue = roots[rootValueOfY];
            if (xValue < yValue) {
                roots[rootValueOfX] += yValue;
                roots[rootValueOfY] = rootValueOfX;
            }
            else {
                roots[rootValueOfY] += xValue;
                roots[rootValueOfX] = rootValueOfY;
            }
        }
        return res;
    }
    
    private int find(int[] roots, int x) {
        int rootValue = roots[x];
        // 如果是负数，代表当前数就是root，返回它自己
        if (rootValue < 0) {
            return x;
        }
        // 找到真正的root的值，并且压缩路径
        rootValue = find(roots, rootValue);
        roots[x] = rootValue;
        return rootValue;
    } 
}
// @lc code=end

