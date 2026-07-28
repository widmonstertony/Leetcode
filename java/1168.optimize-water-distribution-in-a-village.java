import java.util.Arrays;
import java.util.PriorityQueue;

/*
 * @lc app=leetcode id=1168 lang=java
 *
 * [1168] Optimize Water Distribution in a Village
 */

// @lc code=start
class Solution {
    // Union Find 解法
    // 思路 : - 按照cost排序的PriorityQueue
    //        - 把打井的房子全部连在一个虚拟的房子0上
    //        - 一直从pq里拿出最低cost的方式，来union所有的房子，直到大家都连在了一起

    public int minCostToSupplyWater(int n, int[] wells, int[][] pipes) {
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> {return a[2] - b[2];});
        
        for (int i = 0; i < wells.length; i++) {
            pq.offer(new int[]{0, i + 1, wells[i]});
        }
        
        for (int[] pipe : pipes) {
            pq.offer(new int[]{pipe[0], pipe[1], pipe[2]});
        }
        
        int totalCost = 0;
        int[] root = new int[n + 1]; // +1 因为那个虚拟的房子0
        Arrays.fill(root, -1);
        
        while (n > 0) {
            int[] edge = pq.poll();
            if(union(edge[0], edge[1], root)) {
                totalCost += edge[2];
                n--;
            }
        }
        
        return totalCost;
    }
    private boolean union(int houseA, int houseB, int[] root) {
        int rootA = find(houseA, root), rootB = find(houseB, root);
        if (rootA == rootB) {
            return false;
        }
        int sizeA = root[rootA], sizeB = root[rootB];
        if (sizeA < sizeB) {
            root[rootA] += sizeB;
            root[rootB] = rootA;
        }
        else {
            root[rootB] += sizeA;
            root[rootA] = rootB;
        }
        return true;
    }
    private int find(int house, int[] root) {
        int rootHouse = root[house];
        if (rootHouse < 0) {
            return house;
        }
        if (root[rootHouse] >= 0) {
            rootHouse = find(rootHouse, root);
            root[house] = rootHouse;
        }
        return rootHouse;
    }
}
// @lc code=end

