/*
 * @lc app=leetcode id=787 lang=java
 *
 * [787] Cheapest Flights Within K Stops
 */

// @lc code=start
class Solution {
    public int findCheapestPrice(int n, int[][] flights, int src, int dst, int K) {
        Map<Integer, List<List<Integer>>> flightsMap = new HashMap<>();
        for (int[] flight : flights) {
            List<List<Integer>> currFlightList = flightsMap.getOrDefault(flight[0], new ArrayList<>());
            List<Integer> currList = new ArrayList<>();
            currList.add(flight[1]);
            currList.add(flight[2]);
            currFlightList.add(currList);
            flightsMap.put(flight[0], currFlightList);
        }
        // BFS解法
        int minRes = Integer.MAX_VALUE;
        Queue<int[]> cityQueue = new LinkedList<>();
        cityQueue.add(new int[]{src, 0});
        K++;
        while (K >= 0) {
            int currLevel = cityQueue.size();
            while (currLevel > 0) {
                int[] currCity = cityQueue.poll();
                currLevel--;
                if (currCity[0] == dst) {
                    minRes = Math.min(minRes, currCity[1]);
                }
                if (!flightsMap.containsKey(currCity[0])) {
                    continue;
                }
                for (List<Integer> currList: flightsMap.get(currCity[0])) {
                    // 注意这里要裁枝，不然会TLE
                    // 把已经比当前最小值还大的城市跳过
                    if (currList.get(1) + currCity[1] > minRes) {
                        continue;
                    }
                    cityQueue.add(new int[]{currList.get(0), currList.get(1) + currCity[1]});
                }
            }
            K--;
        }
        return minRes == Integer.MAX_VALUE ? -1 : minRes;
    }
}
// @lc code=end

