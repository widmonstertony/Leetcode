import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/*
 * @lc app=leetcode id=491 lang=java
 *
 * [491] Increasing Subsequences
 */

// @lc code=start
class Solution {
    // dfs的迭代版本
    public List<List<Integer>> findSubsequences(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        Stack<List<Integer>> currSt = new Stack<>();
        currSt.push(new ArrayList<>());
        Map<Integer, Integer> visitMap = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int currSize = currSt.size();
            int currNum = nums[i];
            // 如果当前数字之前出现过，则跳过之前的所有答案，从那之后开始
            int start = visitMap.getOrDefault(currNum, 0);
            visitMap.put(currNum, currSize);
            // 把stack里从start到最后一个的答案都附上当前数字
            for (int j = start; j < currSize; j++) {
                // 先确认当前答案的最后一个数小于当前数字，确保是递增的
                if (currSt.size() >= j && !currSt.get(j).isEmpty() && 
                    currSt.get(j).get(currSt.get(j).size() - 1) > currNum) {
                    continue;
                }
                // 把当前答案做一个新的副本，再加上当前新的数字
                List<Integer> newRes = new ArrayList<>();
                for (int num : currSt.get(j)) {
                    newRes.add(num);
                }
                newRes.add(currNum);
                if (newRes.size() > 1) {
                    res.add(newRes);
                }
                currSt.push(newRes);
            }
        }
        return res;
    }
    // dfs写法
    // public List<List<Integer>> findSubsequences(int[] nums) {
    //     List<List<Integer>> res = new ArrayList<>();
    //     helper(0, nums, res, new ArrayList<>());
    //     return res;
    // }
    // private void helper(int startIdx, int[] nums, List<List<Integer>> res, List<Integer> currList) {
    //     if (currList.size() > 1) {
    //         List<Integer> newRes = new ArrayList<>();
    //         for (int num : currList) {
    //             newRes.add(num);
    //         }
    //         res.add(newRes);
    //     }
    //     // 用set来记录已经遍历过的相同数字
    //     Set<Integer> numSet = new HashSet<>();
    //     for (int i = startIdx; i < nums.length; i++) {
    //         // 跳过之前遍历过的相同数字
    //         if (numSet.contains(nums[i])) {
    //             continue;
    //         }
    //         // 确保子序列一定是increasing的
    //         if (!currList.isEmpty() && currList.get(currList.size() - 1) > nums[i]) {
    //             continue;
    //         }
    //         currList.add(nums[i]);
    //         numSet.add(nums[i]);
    //         helper(i + 1, nums, res, currList);
    //         currList.remove(currList.size() - 1);
    //     }
    // }
}
// @lc code=end

