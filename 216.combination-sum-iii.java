import java.awt.List;
import java.util.ArrayList;

/*
 * @lc app=leetcode id=216 lang=java
 *
 * [216] Combination Sum III
 */

// @lc code=start
class Solution {
    public List<List<Integer>> combinationSum3(int k, int n) {
        List<List<Integer>> resList = new ArrayList<>();
        boolean[] visited = new boolean[n + 1];
        helper(resList, new ArrayList<>(), visited, k, n, 1);
        return resList;
    }
    private void helper(List<List<Integer>> resList, List<Integer> currList, boolean[] visited, int k, int n, int startNum) {
        if (k == 0 && n == 0) {
            resList.add(new ArrayList<>(currList));
            return;
        }
        // 题目要求1到9，所以最大尝试的数是不超过9
        int tryMax = n;
        if (tryMax > 9) {
            tryMax = 9;
        }
        // 从指定开始数到能尝试的最大数，都试一遍
        for (int i = startNum; i <= tryMax; i++) {
            if (visited[i]) {
                continue;
            }
            visited[i] = true;
            currList.add(i);
            // 确保startNum从当前位数的后一位开始，消除重复
            helper(resList, currList, visited, k - 1, n - i, i + 1);
            currList.remove(currList.size() - 1);
            visited[i] = false;
        }
    }
}
// @lc code=end

