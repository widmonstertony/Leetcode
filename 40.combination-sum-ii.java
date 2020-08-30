/*
 * @lc app=leetcode id=40 lang=java
 *
 * [40] Combination Sum II
 */

// @lc code=start
class Solution {
    public List<List<Integer>> combinationSum2(int[] candidates, int target) {
        List<List<Integer>> resList = new ArrayList<>();
        Arrays.sort(candidates);
        combinationSumHelper(0, candidates, target, resList, new ArrayList<Integer>());
        return resList;
    }
    // 注意需要一个start标注当前的数字的坐标，因为不想从比当前数更小的数重新试，这样会有重复
    private void combinationSumHelper(int start, int[] candidates, int target, List<List<Integer>> resList, List<Integer> currList) {
        if (target <= 0) {
            if (currList.size() > 0 && target == 0) {
                resList.add(new ArrayList<>(currList));
            }
            return;
        }
        for (int i = start; i < candidates.length; i++) {
            // 如果当前数字不是一开始那位（避免一个位置上的数被多次使用
            // 并且如果当前数字和之前的一样，则跳过当前数字(避免同一种数字被多次使用)
            if (i > start && candidates[i] == candidates[i - 1]) {
                continue;
            }
            int candidate = candidates[i];
            currList.add(candidate);
            // 从下一个新的位置开始尝试，避免重复使用当前这个位置上的数字
            combinationSumHelper(i + 1, candidates, target - candidate, resList, currList);
            currList.remove(currList.size() - 1);
        }
    }
}
// @lc code=end

