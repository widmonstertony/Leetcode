import java.util.ArrayList;

/*
 * @lc app=leetcode id=401 lang=java
 *
 * [401] Binary Watch
 */

// @lc code=start
class Solution {
    public List<String> readBinaryWatch(int num) {
        List<String> resList = new ArrayList<>();
        int[] hours = new int[]{8, 4, 2, 1};
        int[] minutes = new int[]{32, 16, 8, 4, 2, 1};
        for (int i = 0; i <= num; i++) {
            // 先分别把分钟和时钟的所有排列拿出来，再合起来
            List<Integer> hourList = generateCombo(hours, i);
            List<Integer> minuteList = generateCombo(minutes, num - i);
            for (int hour: hourList) {
                if (hour > 11) {
                    continue;
                }
                for (int minute: minuteList) {
                    if (minute > 59) {
                        continue;
                    }
                    resList.add(hour + (minute < 10 ? ":0" : ":") + minute);
                }
            }
        }
        return resList;
    }
    private List<Integer> generateCombo(int[] nums, int num) {
        List<Integer> resList = new ArrayList<>();
        helper(nums, num, 0, 0, resList);
        return resList;
    }
    private void helper(int[] nums, int currLEDs, int startPos, int currSum, List<Integer> resList) {
        if (currLEDs == 0) {
            resList.add(currSum);
            return;
        }
        for (int i = startPos; i < nums.length; i++) {
            helper(nums, currLEDs - 1, i + 1, currSum + nums[i], resList);
        }
    }
}
// @lc code=end

