/*
 * @lc app=leetcode id=218 lang=java
 *
 * [218] The Skyline Problem
 */

// @lc code=start
class Solution {
    public List<List<Integer>> getSkyline(int[][] buildings) {
       List<List<Integer>> resList = new ArrayList<>();
       List<List<Integer>> heights = new ArrayList<>();
       for (int[] building: buildings) {
           // 建立左边界和负高度的 pair
           List<Integer> start = new ArrayList<>();
           start.add(building[0]);
           // 将左边界的高度存为负数
           start.add(-building[2]);
           heights.add(start);
           // 建立右边界和高度的 pair
           List<Integer> end = new ArrayList<>();
           end.add(building[1]);
           end.add(building[2]);
           heights.add(end);
       }
       
       Collections.sort(heights, (a, b) -> {
           if (a.get(0) - b.get(0) != 0) {
               return a.get(0) - b.get(0);
           }
           return a.get(1) - b.get(1);
           
       });
       // 一个从大到小的顺序排列的heap
       PriorityQueue<Integer> heightMaxHeap = new PriorityQueue<>((a, b) -> (b - a));
       
       heightMaxHeap.offer(0);
       
       int prev = 0;
       for (List<Integer> height: heights) {
           int currHeight = height.get(1);
           // 如果遇到高度为负值的 pair，说明是左边界，那么将正高度加入heap中
           if (currHeight < 0) {
               heightMaxHeap.offer(-currHeight);
           }
           // 遇到右边界，先把之前放进heap的左边界的高度删除，这样整个这一个building就都删除了
           else {
               heightMaxHeap.remove(currHeight);
           }
           // 取出此时集合中最高的高度
           int curr = heightMaxHeap.peek();
           // 如果当前高度和之前高度不一样，说明是个拐点，记录下来
           if (prev != curr) {
               List<Integer> tmp = new ArrayList<>();
               tmp.add(height.get(0));
               tmp.add(curr);
               resList.add(tmp);
               // 更新当前高度为之前高度
               prev = curr;
           }
       }
       return resList;
   }
}
// @lc code=end

