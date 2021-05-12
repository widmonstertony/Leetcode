/*
 * @lc app=leetcode id=797 lang=java
 *
 * [797] All Paths From Source to Target
 */

// @lc code=start
class Solution {
    public List<List<Integer>> allPathsSourceTarget(int[][] graph) {
        List<List<Integer>> resList = new ArrayList();
        boolean[] visited = new boolean[graph.length];
        dfsAllPath(visited, resList, new ArrayList(), graph, 0);
        return resList;
    }
    
    private void dfsAllPath(boolean[] visited, List<List<Integer>> resList, List<Integer> currList, int[][] graph, int currNode) {
        if (visited[currNode]) {
            return;
        }
        visited[currNode] = true;
        currList.add(currNode);
        if (currNode == graph.length - 1) {
            resList.add(new ArrayList(currList));
        }
        else {
            for (int nextNode : graph[currNode]) {
                dfsAllPath(visited, resList, currList, graph, nextNode);
            }
        }
        currList.remove(currList.size() - 1);
        visited[currNode] = false;
    }
    // dfs另一种写法
    // public List<List<Integer>> allPathsSourceTarget(int[][] graph) {
    //     boolean[] visited = new boolean[graph.length];
    //     return dfsAllPath(visited, new ArrayList(), graph, 0);
    // }
    
    // private List<List<Integer>> dfsAllPath(boolean[] visited, List<Integer> currList, int[][] graph, int currNode) {
    //     List<List<Integer>> resList = new ArrayList();
    //     if (visited[currNode]) {
    //         return resList;
    //     }
    //     visited[currNode] = true;
    //     currList.add(currNode);
    //     if (currNode == graph.length - 1) {
    //         resList.add(new ArrayList(currList));
    //     }
    //     else {
    //         for (int nextNode : graph[currNode]) {
    //             List<List<Integer>> nextList = dfsAllPath(visited, currList, graph, nextNode);
    //             resList.addAll(nextList);
    //         }
    //     }
    //     currList.remove(currList.size() - 1);
    //     visited[currNode] = false;
    //     return resList;
    // }
}
// @lc code=end

