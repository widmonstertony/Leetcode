import java.util.ArrayList;
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
    public int[] findRedundantConnection(int[][] edges) {
        // 记住所有的node的路径
        Map<Integer, List<Integer>> nodeRoutes = new HashMap<>();
        // 记录下每次dfs一条路径经过的node
        Set<Integer> visitedNodes = new HashSet<>();
        // 遍历每一条edge，找到是否这条edge还有其他能到的路径
        for (int[] edge: edges) {
            // 首先把当前dfs经过的node全部清空
            visitedNodes.clear();
            // 再dfs这条edge开始node的所有可行路线，看是否能到达和这条edge一样的终点
            // 如果可以，说明有loop
            // 如果不行，则把当前的edge加进我们的所有的node的路径里
            if (dfsCanFindOtherRoute(edge[0], edge[1], nodeRoutes, visitedNodes) != true) {
                if (nodeRoutes.get(edge[0]) == null) {
                    nodeRoutes.put(edge[0], new ArrayList<>());
                }
                nodeRoutes.get(edge[0]).add(edge[1]);
                if (nodeRoutes.get(edge[1]) == null) {
                    nodeRoutes.put(edge[1], new ArrayList<>());
                }
                nodeRoutes.get(edge[1]).add(edge[0]);
            }
            else {
                return edge;
            }
        }
        return null;
    }

    private boolean dfsCanFindOtherRoute(int fromNode, int toNode, Map<Integer, List<Integer>> nodeRoutes, Set<Integer> visitedNodes) {
        // 如果当前开始的node没有被visit过
        if (!visitedNodes.contains(fromNode)) {
            // mark当前node
            visitedNodes.add(fromNode);
            // 如果要去的node和当前一样，也属于有loop
            if (fromNode == toNode) {
                return true;
            }
            // 如果是死路，则代表没有loop了
            if (nodeRoutes.get(fromNode) == null) {
                return false;
            }
            // 否则，则把当前node可以去的node全部dfs的方式去一遍，看是否会遇到之前要去的那个node
            for (int toNewNode : nodeRoutes.get(fromNode)) {
                if (dfsCanFindOtherRoute(toNewNode, toNode, nodeRoutes, visitedNodes)) {
                    return true;
                }
            }
        }
        return false;
    }
}
// @lc code=end

