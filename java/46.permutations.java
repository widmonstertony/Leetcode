import java.awt.List;
import java.util.ArrayList;

/*
 * @lc app=leetcode id=46 lang=java
 *
 * [46] Permutations
 */

// @lc code=start
class Solution {
    public List<List<Integer>> permute(int[] nums) {
        boolean[] visited = new boolean[nums.length];
        List<List<Integer>> resList = new ArrayList<>();
        permuteHelper(nums, resList, new ArrayList<Integer>(), visited);
        return resList;
    }
    private void permuteHelper(int[] nums, List<List<Integer>> resList, List<Integer> currList, boolean[] visited) {
        if (currList.size() == nums.length) {
            resList.add(new ArrayList<>(currList));
        }
        for (int i = 0; i < nums.length; i++) {
            if (visited[i]) {
                continue;
            }
            visited[i] = true;
            currList.add(nums[i]);
            permuteHelper(nums, resList, currList, visited);
            visited[i] = false;
            currList.remove(currList.size() - 1);
        }
    }
    // public List<List<Integer>> permute(int[] nums) {
    //     List<List<Integer>> resList = new ArrayList<>();
    //     permuteHelper(nums, resList, 0);
    //     return resList;
    // }
    // private void permuteHelper(int[] nums, List<List<Integer>> resList, int start) {
    //     // 如果已经到结尾了，保留当前答案
    //     if (start >= nums.length) {
    //         resList.add(Arrays.stream(nums).boxed().collect(Collectors.toList()));
    //         return;
    //     }
    //     // 把每一个数都和start的数交换位置
    //     // start位置往后移一位
    //     // 试过之后再还原
    //     for (int i = start; i < nums.length; i++) {
    //         int startNum = nums[start];
    //         nums[start] = nums[i];
    //         nums[i] = startNum;
    //         permuteHelper(nums, resList, start + 1);
    //         nums[i] = nums[start];
    //         nums[start] = startNum;
    //     }
    // }
}
// @lc code=end

