import java.util.Map;
import java.util.PriorityQueue;

/*
 * @lc app=leetcode id=253 lang=java
 *
 * [253] Meeting Rooms II
 */

// @lc code=start
class Solution {
    public int minMeetingRooms(int[][] intervals) {
        // 先把数组按照起始时间从小按大排列
        Arrays.sort(intervals, (firstArr, secondArr) -> {
            return firstArr[0] - secondArr[0];
        });

        // pq里的是各个会议室里的会议的结束时间，按照从小按大排列
        PriorityQueue<Integer> pq = new PriorityQueue<>();

        for (int[] interval : intervals) {
            // pq里的首元素小于等于当前会议的起始时间
            // 说明有最早结束时间的那个会议已经结束，可以用那个会议室开始当前会议了
            // 于是就先把那个会议的结束时间从pq里移除，再把当前会议时间加进去
            if (!pq.isEmpty() && pq.peek() <= interval[0]) {
                pq.poll();
            }

            // 不然的话，说明当前会议的开始时间比最早能结束的会议的结束时间还早，需要一个新的会议室
            // 把当前会议的结束时间放上去
            pq.add(interval[1]);
        }

        // pq里的数量就是需要的会议室的数量
        return pq.size();
    }

    // public int minMeetingRooms(int[][] intervals) {
    //     //使用 TreeMap 来做的
    //     Map<Integer, Integer> timeMap = new TreeMap<>();
    //     // 遍历时间区间，对于起始时间，映射值自增1，对于结束时间，映射值自减1，
    //     for (int[] interval : intervals) {
    //         if (!timeMap.containsKey(interval[0])) {
    //             timeMap.put(interval[0], 0);
    //         }
    //         timeMap.put(interval[0], timeMap.get(interval[0]) + 1);
    //         if (!timeMap.containsKey(interval[1])) {
    //             timeMap.put(interval[1], 0);
    //         }
    //         timeMap.put(interval[1], timeMap.get(interval[1]) - 1);
    //     }

    //     int rooms = 0, res = 0;
    //     // 按照所有时间的从小到大顺序遍历
    //     for (Map.Entry<Integer, Integer> currEntry : timeMap.entrySet()) {
    //         // 遇到起始时间，映射是正数，则房间数会增加
    //         // 如果一个时间是一个会议的结束时间，也是另一个会议的开始时间
    //         // 则映射值先减后加仍为0，并不用分配新的房间
    //         // 而结束时间的映射值为负数更不会增加房间数
    //         res = Math.max(res, rooms += currEntry.getValue());
    //     }
    //     return res;
    // }
}
// @lc code=end

