import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * @lc app=leetcode id=684 lang=java
 *
 * [684] Redundant Connection
 */


// @lc code=start
class Solution {
    // Union Find 解法
    class DisjointSetUnion {
        // 如果root的值是正数，表示为该元素的父节点
        // 如果为负数，则表示该元素所在集合的代表
        // 而且值的相反数即为集合中的元素个数
        int[] root;
        public DisjointSetUnion(int size) {
            root = new int[size];
            Arrays.fill(root, -1);
            // for (int i = 0; i < size; i++) {
            //     root[i] = -1;
            // }
        }
        // 找到元素 x 所在的集合的代表
        public int find(int x) {
            // 如果root[x]是负数的话，返回他自己，代表它是root
            if (root[x] < 0) {
                return x;
            }
            // 否则找到它的root，压缩路径
            root[x] = find(root[x]);
            return root[x];
        }
        // 把元素 x 和元素 y 所在的集合合并
        // 要求 x 和 y 所在的集合不相交
        // 如果相交则不合并，并且当前edge就是需要return的。
        public boolean union(int x, int y) {
            int rootOfX = find(x), rootOfY = find(y);
            if (rootOfX == rootOfY) {
                return false;
            }
            // 如果rootOfX的root的值小于右边的值
            // 因为是负值，代表左边的集合的个数多于右边
            if (root[rootOfX] < root[rootOfY]) {
                // 把右边的root的所有集合的个数加给左边
                // 再把rootOfY定为rootOfX，代表X是Y的root
                root[rootOfX] += root[rootOfY];
                root[rootOfY] = rootOfX;
            }
            else {
                root[rootOfY] += root[rootOfX];
                root[rootOfX] = rootOfY;
            }
            return true;
        }
    }
    final int MAX_EDGE_VAL = 1000;

    public int[] findRedundantConnection(int[][] edges) {
        DisjointSetUnion dsu = new DisjointSetUnion(MAX_EDGE_VAL + 1);
        for (int[] edge: edges) {
            // 判断两个元素是否位于同一个集合
            // 如果没法union，代表属于同一个集合
            if (!dsu.union(edge[0], edge[1])) {
                return edge;
            }
        }
        throw new AssertionError();
    }
    // DFS解法
    // public int[] findRedundantConnection(int[][] edges) {
    //     // // 记住所有的node的路径
    //     // Map<Integer, List<Integer>> nodeRoutes = new HashMap<>();
    //     // // 记录下每次dfs一条路径经过的node
    //     // Set<Integer> visitedNodes = new HashSet<>();
    //     // // 遍历每一条edge，找到是否这条edge还有其他能到的路径
    //     // for (int[] edge: edges) {
    //     //     // 首先把当前dfs经过的node全部清空
    //     //     visitedNodes.clear();
    //     //     // 再dfs这条edge开始node的所有可行路线，看是否能到达和这条edge一样的终点
    //     //     // 如果可以，说明有loop
    //     //     // 如果不行，则把当前的edge加进我们的所有的node的路径里
    //     //     if (dfsCanFindOtherRoute(edge[0], edge[1], nodeRoutes, visitedNodes) != true) {
    //     //         if (nodeRoutes.get(edge[0]) == null) {
    //     //             nodeRoutes.put(edge[0], new ArrayList<>());
    //     //         }
    //     //         nodeRoutes.get(edge[0]).add(edge[1]);
    //     //         if (nodeRoutes.get(edge[1]) == null) {
    //     //             nodeRoutes.put(edge[1], new ArrayList<>());
    //     //         }
    //     //         nodeRoutes.get(edge[1]).add(edge[0]);
    //     //     }
    //     //     else {
    //     //         return edge;
    //     //     }
    //     // }
    //     // throw new AssertionError();
    // }

    // private boolean dfsCanFindOtherRoute(int fromNode, int toNode, Map<Integer, List<Integer>> nodeRoutes, Set<Integer> visitedNodes) {
    //     // 如果当前开始的node没有被visit过
    //     if (!visitedNodes.contains(fromNode)) {
    //         // mark当前node
    //         visitedNodes.add(fromNode);
    //         // 如果要去的node和当前一样，也属于有loop
    //         if (fromNode == toNode) {
    //             return true;
    //         }
    //         // 如果是死路，则代表没有loop了
    //         if (nodeRoutes.get(fromNode) == null) {
    //             return false;
    //         }
    //         // 否则，则把当前node可以去的node全部dfs的方式去一遍，看是否会遇到之前要去的那个node
    //         for (int toNewNode : nodeRoutes.get(fromNode)) {
    //             if (dfsCanFindOtherRoute(toNewNode, toNode, nodeRoutes, visitedNodes)) {
    //                 return true;
    //             }
    //         }
    //     }
    //     return false;
    // }
}
// @lc code=end

