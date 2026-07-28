import java.awt.List;
import java.util.ArrayList;

/*
 * @lc app=leetcode id=526 lang=java
 *
 * [526] Beautiful Arrangement
 */

// @lc code=start
class Solution {
    public int countArrangement(int N) {
        // 保存所有答案，可以用个count，这里为了debug方便用了List
        List<List<Integer>> resList = new LinkedList<>();
        // 可以用一个数来代替这个list用来表示当前尝试到第几位
        // 这里用它的size来知道遍历到了第几位
        helper(new LinkedList<>(), new boolean[N], resList);
        return resList.size();
    }
    private void helper(List<Integer> currList, boolean[] visited, List<List<Integer>> resList) {
        if (currList.size() == visited.length) {
            resList.add(new LinkedList<>(currList));
            return;
        }
        for (int i = 1; i <= visited.length; i++) {
            if (visited[i - 1]) {
                continue;
            }
            if (i % (currList.size() + 1) == 0 || (currList.size() + 1) % i == 0) {
                visited[i - 1] = true;
                currList.add(i);
                helper(currList, visited, resList);
                currList.remove(currList.size() - 1);
                visited[i - 1] = false;
            }
        }
    }
}
// @lc code=end

