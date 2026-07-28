import java.util.ArrayList;
import java.util.List;

/*
 * @lc app=leetcode id=228 lang=java
 *
 * [228] Summary Ranges
 */

// @lc code=start
class Solution {
    public List<String> summaryRanges(int[] nums) {
        List<String> resList = new ArrayList<>();
        int i = 0; 
        while (i < nums.length) {
            int j = 1;
            while (i + j < nums.length && nums[i + j] - nums[i] == j) {
                j++;
            }
            if (j <= 1) {
                resList.add(Integer.toString(nums[i]));
            }
            else {
                resList.add(nums[i] + "->" + nums[i + j - 1]);
            }
            i += j;
        }
        return resList;
    }
    // 一开始自己瞎写的垃圾算法
    // public List<String> summaryRanges(int[] nums) {
    //     List<String> resList = new ArrayList<>();
    //     List<Integer> currList = new ArrayList<>();
    //     if (nums.length == 1) {
    //         resList.add(nums[0] + "");
    //         return resList;
    //     }
    //     for (int i = 0; i < nums.length; i++) {
    //         if (i == 0) {
    //             currList.add(nums[i]);
    //         }
    //         else {
    //             if (nums[i] - nums[i - 1] == 1) {
    //                 currList.add(nums[i]);
    //             }
    //             else {
    //                 if (currList.size() > 1) {
    //                     resList.add(currList.get(0) + "->" + currList.get(currList.size() - 1));
    //                 }
    //                 else {
    //                     resList.add(currList.get(0) + "");
    //                 }
    //                 currList.clear();
    //                 currList.add(nums[i]);
    //             }
    //             if (i == nums.length - 1) {
    //                 if (currList.size() > 1) {
    //                     resList.add(currList.get(0) + "->" + currList.get(currList.size() - 1));
    //                 }
    //                 else {
    //                     resList.add(currList.get(0) + "");
    //                 }
    //             }
    //         }
    //     }
    //     return resList;
    // }
}
// @lc code=end

