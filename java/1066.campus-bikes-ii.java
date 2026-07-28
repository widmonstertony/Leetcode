/*
 * @lc app=leetcode id=1066 lang=java
 *
 * [1066] Campus Bikes II

 */

// @lc code=start
class Solution {
    // 用dfs和backtracking遍历每一种答案，优化版
    // 因为bike和worker的数量不超过10，所以可以用一个integer来代替visited数组
    // 同时用一个数组来记录下所有情况，防止重复计算
    public int assignBikes(int[][] workers, int[][] bikes) {
        return helper(0, workers, bikes, 0, new int[(int)Math.pow(2, bikes.length)]);
    }
    private int helper(int bikeBitMask, int[][] workers, int[][] bikes, int currWorker, int[] memoRes) {
        if (currWorker == workers.length) {
            return 0;
        }
        if (memoRes[bikeBitMask] != 0) {
            return memoRes[bikeBitMask];
        }
        int minRes = Integer.MAX_VALUE;
        for (int i = 0; i < bikes.length; i++) {
            // 确认bikeBitMask的第i位是否是1，如果是，代表visited过
            if (isIthBitSet(bikeBitMask, i)) {
                continue;
            }
            int currSum = helper(setIthBitSet(bikeBitMask, i), workers, bikes, currWorker + 1, memoRes);
            minRes = Math.min(minRes, currSum + distance(workers[currWorker], bikes[i]));
        }
        memoRes[bikeBitMask] = minRes;
        return minRes;
    }
    
    // 把bikeBitMask的第i位设置为1，代表已经visit过
    private int setIthBitSet(int bikeBitMask, int i) {
        return bikeBitMask | (1 << i);
    }

    // 把1往左移i位，确认bikeBitMask的第i位是否是1
    private boolean isIthBitSet(int bikeBitMask, int i) {
        return ((1 << i) & bikeBitMask) > 0;
    }

    private int distance(int[] worker, int[] bike) {
        return Math.abs(worker[0] - bike[0]) + Math.abs(worker[1] - bike[1]);
    }
    // 暴力解，用dfs和backtracking遍历每一种答案
    // public int assignBikes(int[][] workers, int[][] bikes) {
    //     return helper(new boolean[bikes.length], workers, bikes, 0);
    // }
    // private int helper(boolean[] visited, int[][] workers, int[][] bikes, int currWorker) {
    //     if (currWorker >= workers.length) {
    //         return 0;
    //     }
    //     int minRes = Integer.MAX_VALUE;
    //     for (int i = 0; i < bikes.length; i++) {
    //         if (visited[i]) {
    //             continue;
    //         }
    //         visited[i] = true;
    //         int currSum = helper(visited, workers, bikes, currWorker + 1);
    //         minRes = Math.min(minRes, currSum + distance(workers[currWorker], bikes[i]));
    //         visited[i] = false;
    //     }
    //     return minRes;
    // }
    // private int distance(int[] worker, int[] bike) {
    //     return Math.abs(worker[0] - bike[0]) + Math.abs(worker[1] - bike[1]);
    // }
}
// @lc code=end

