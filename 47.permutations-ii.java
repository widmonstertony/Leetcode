import java.util.ArrayList;

/*
 * @lc app=leetcode id=47 lang=java
 *
 * [47] Permutations II
 */

// @lc code=start
class Solution {
    public List<List<Integer>> permuteUnique(int[] nums) {
        // 需要先sort一遍，才可以判断是否重复
        Arrays.sort(nums);
        List<List<Integer>> resList = new ArrayList<>();
        boolean visited[] = new boolean[nums.length];
        permuteUniqueHelper(nums, resList, new ArrayList<Integer>(), visited);
        return resList;
    }
    private void permuteUniqueHelper(int[] nums, List<List<Integer>> resList, List<Integer> currList, boolean[] visited) {
        if (currList.size() >= nums.length) {
            resList.add(new ArrayList<Integer>(currList));
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if (visited[i]) {
                continue;
            }
            // 和46不一样的地方，因为数字不是唯一的
            // 所以如果当前数字和前面的数字一样，并且前面的visited是false
            // 因为按道理前面的数字肯定已经visited过了，所以这里如果是false
            // 就代表着前面的数字的结果已经被还原了, 也就代表结果已经被记录下来了
            // 那么也就是说当前的数字的这种情况已经被记录下来了
            // 也就不需要再跑一遍当前数字的情况，所以跳过
            if (i > 0 && nums[i] == nums[i - 1] && !visited[i - 1]) {
                continue;
            }
            visited[i] = true;
            currList.add(nums[i]);
            permuteUniqueHelper(nums, resList, currList, visited);
            visited[i] = false;
            currList.remove(currList.size() - 1);
        }
    }
    // public List<List<Integer>> permuteUnique(int[] nums) {
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
    //         int j = i - 1;
    //         while (j >= start && nums[j] != nums[i]) {
    //             j--;
    //         }
    //         // 确保 nums[start, ..., i - 1] 都和 nums[i]不一样
    //         // 再进行swap操作
    //         if (j != start - 1) {
    //             continue;
    //         }
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

