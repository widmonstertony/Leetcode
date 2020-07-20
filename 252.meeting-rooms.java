/*
 * @lc app=leetcode id=252 lang=java
 *
 * [252] Meeting Rooms
 */

// @lc code=start
class Solution {
    public boolean canAttendMeetings(int[][] intervals) {
        // 先把数组按照起始时间从小按大排列
        Arrays.sort(intervals, (firstArr, secondArr) -> {
            return firstArr[0] - secondArr[0];
        });
        for (int i = 0; i < intervals.length - 1; i++) {
            // 如果下一个区间的起始时间，小于之前区间的结束时间，说明有重叠
            if (intervals[i + 1][0] < intervals[i][1]) {
                return false;
            }
        }
        return true;
    }
    // public boolean canAttendMeetings(int[][] intervals) {
        
    //     for(int i = 0; i < intervals.length; i++) {
    //         for (int j = i + 1; j < intervals.length; j++) {
                
    //             // 判断两个区间有没有重叠
    //             // 就是判断一个区间的起始位置，是不是在另一个区间中间
    //             // 也就是一个区间的起始位置，是否大于另一个区间的起始而小于另一个区间的结束
    //             if (intervals[i][0] >= intervals[j][0] && intervals[i][0] < intervals[j][1]) {
    //                 return false;
    //             }
    //             if (intervals[j][0] >= intervals[i][0] && intervals[j][0] < intervals[i][1]) {
    //                 return false;
    //             }
    //         }
    //     }
        
    //     return true;
    // }
}
// @lc code=end

