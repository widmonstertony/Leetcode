import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * @lc app=leetcode id=802 lang=java
 *
 * [802] Find Eventual Safe States
 */

// @lc code=start
class Solution {
    public List<Integer> eventualSafeNodes(int[][] graph) {
        List<Integer> ansList = new ArrayList<Integer>();
        Set<Integer> safeNodeSet = new HashSet<Integer>();
        // 确认每个node被visited过，不会重复visit
        boolean[] isNodeVisited = new boolean[graph.length];
        //遍历每个node，确认每个node都是safe的
        for (int i = 0; i < graph.length; i++) {
            isSafeNode(graph, i, safeNodeSet, isNodeVisited);
        }
        for (int i = 0; i < graph.length; i++) {
            if (safeNodeSet.contains(i)) {
                ansList.add(i);
            }
        }
        return ansList;
    }
    private boolean isSafeNode(int[][] graph, int i, Set<Integer> safeNodeSet, boolean[] isNodeVisited) {
        if (safeNodeSet.contains(i)) {
            return true;
        }
        if (isNodeVisited[i]) {
            return false;
        }
        isNodeVisited[i] = true;
        // 这一段不需要，因为如果没有node是不safe的，那么这个node就是safe的
        // if (graph[i].length == 0) {
        //     safeNodeSet.add(i);
        //     return true;
        // }
        boolean isCurrNodeSafe = true;
        // 如果当前node是safenode，那么就要确保它的所有子node是safe的
        for (int j = 0; j < graph[i].length; j++) {
            if (!isSafeNode(graph, graph[i][j], safeNodeSet, isNodeVisited)) {
                isCurrNodeSafe = false;
            }
        }
        if (isCurrNodeSafe) {
            safeNodeSet.add(i);
        }
        return isCurrNodeSafe;
    }
}
// @lc code=end

