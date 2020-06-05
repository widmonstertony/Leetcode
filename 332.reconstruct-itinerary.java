import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

/*
 * @lc app=leetcode id=332 lang=java
 *
 * [332] Reconstruct Itinerary
 */

// @lc code=start
class Solution {
    public List<String> findItinerary(List<List<String>> tickets) {
        // PQ，把能飞去的机场名字以字母表顺序排列
        HashMap<String, PriorityQueue<String>> orderedRoute = new HashMap<>();
        
        // 把所有起飞的城市和能到达的城市加进hashmap
        for (List<String> ticket : tickets) {
            String startAirport = ticket.get(0);
            if (orderedRoute.containsKey(startAirport)) {
                orderedRoute.get(startAirport).offer(ticket.get(1));
            }
            else {
                PriorityQueue<String> pq = new PriorityQueue<>();
                pq.offer(ticket.get(1));
                orderedRoute.put(startAirport, pq);
            }
        }

        String startAirport = "JFK";
        List<String> resList = new ArrayList<>();
        // 从JFK开始，DFS所有可以飞的路线
        dfs(orderedRoute, startAirport, resList);
        return resList;
    }

    private void dfs(HashMap<String, PriorityQueue<String>> orderedRoute, String startAirport, List<String> resList) {
        if (orderedRoute.containsKey(startAirport)) {
            PriorityQueue<String> pq = orderedRoute.get(startAirport);
            // 如果当前开始的城市还有城市没有飞过
            // 把所有没有飞过的城市都飞一遍
            while (!pq.isEmpty()) {
                dfs(orderedRoute, pq.poll(), resList);
            }
        }
        //在把当前这次所有的可以飞的路线都飞完后，把自己加在最前面
        resList.add(0, startAirport);
    }
    
}
// @lc code=end

