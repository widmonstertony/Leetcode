import java.awt.List;
import java.util.ArrayList;

/*
 * @lc app=leetcode id=39 lang=java
 *
 * [39] Combination Sum
 */

// @lc code=start
class Solution {
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> resList = new ArrayList<>();
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
            int candidate = candidates[i];
            currList.add(candidate);
            combinationSumHelper(i, candidates, target - candidate, resList, currList);
            currList.remove(currList.size() - 1);
        }
    }
}
// @lc code=end

