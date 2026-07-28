import java.util.HashSet;
import java.util.Map;

/*
 * @lc app=leetcode id=403 lang=java
 *
 * [403] Frog Jump
 */

// @lc code=start
class Solution {
    public boolean canCross(int[] stones) {
        Map<Integer, Set<Integer>> stoneMap = new HashMap<>();
        // dp[i]表示青蛙跳到位置为i的石头时的最大弹跳力
        int[] dp = new int[stones.length];
        stoneMap.put(0, new HashSet<>(){{ add(0); }});
        int k = 0;
        for (int i = 1; i < stones.length; i++) {
            // 找到刚好能跳到i上的石头k
            // 如果i和k的距离大于青蛙在k上的最大弹跳力 +1
            // 则说明青蛙在当前k石头上到不了i，则k自增1
            while(dp[k] + 1 < stones[i] - stones[k]) {
                k++;
            }
            // 从k遍历到i
            for (int j = k; j < i; j++) {
                int currDistance = stones[i] - stones[j];
                Set<Integer> currSet = stoneMap.get(j);
                // 如果青蛙能从中间某个石头上跳到i上
                if (currSet != null && 
                  (currSet.contains(currDistance - 1) ||
                   currSet.contains(currDistance) || 
                   currSet.contains(currDistance + 1))) {
                    // 更新石头i上的弹跳力
                    if (!stoneMap.containsKey(i)) {
                        stoneMap.put(i, new HashSet<>());
                    }   
                    stoneMap.get(i).add(currDistance);
                    // 更新石头i上的最大弹跳力
                    dp[i] = Math.max(dp[i], currDistance);
                }
            }
        }
        return dp[dp.length - 1] > 0;
    }
    
    // public boolean canCross(int[] stones) {
    //     // 哈希表，青蛙在pos位置和拥有jump跳跃能力时是否能跳到对岸
    //     Map<Integer, Boolean> dp = new HashMap<>();
    //     return tryAllHelper(stones, dp, 0, 0);
    // }
    // private boolean tryAllHelper(int[] stones, Map<Integer, Boolean> dp, int currIdx, int currJump) {
    //     // 判断currIdx是否已经到最后一个石头了，是的话直接返回true
    //     if (currIdx >= stones.length - 1) {
    //         return true;
    //     }
    //     // 将currJump左移很多位并或上currIdx
    //     // 由于题目中对于位置大小有限制，所以不会产生冲突
    //     int key = currIdx | currJump << 11;
    //     // 当前这种情况是否已经出现在哈希表中，是的话直接从哈希表中取结果
    //     if (dp.containsKey(key)) {
    //         return dp.get(key);
    //     }
    //     // 遍历余下的所有石头, 直到遍历到最后一块石头或者从某个石头可以跳到终点
    //     for (int i = currIdx + 1; i < stones.length; i++) {
    //         int currDistance = stones[i] - stones[currIdx];
    //         // 如果当前距离小于currJump - 1, 说明没法跳到当前石头，换下一个距离更长的
    //         if (currDistance < currJump - 1) {
    //             continue;
    //         }
    //         // 如果当前距离大于currJump + 1，说明跳不过去了，把当前情况记录为false
    //         else if (currDistance > currJump + 1) {
    //             dp.put(key, false);
    //             return false;
    //         }
    //         // 可以跳到石头i
    //         // 如果可以从石头i跳到终点，说明当前石头可以到终点
    //         // 把当前情况记录为true，不然的话继续试下一块石头
    //         if (tryAllHelper(stones, dp, i, currDistance)) {
    //             dp.put(key, true);
    //             return true;
    //         }
    //     }
    //     // 遍历了所有石头都不行，说明当前情况不可以，mark false
    //     dp.put(key, false);
    //     return false;
    // }

    // TLE
    // public boolean canCross(int[] stones) {
    //     Set<Integer> stoneSet = new HashSet<>();
    //     for(int stone: stones) {
    //         stoneSet.add(stone);
    //     }
    //     Map<Integer, Integer> dp = new HashMap<>();
    //     int lastStone = stones[stones.length - 1];
    //     // for (int stone: stones) {
    //     //     if (stone != 0 && !dp.containsKey(stone)) {
    //     //         continue;
    //     //     }
    //     //     int lastStep = stone == 0 ? 0 : dp.get(stone);
    //     //     if (tryAllStep(stone, lastStep - 1, dp, stoneSet, lastStone) ||
    //     //     tryAllStep(stone, lastStep, dp, stoneSet, lastStone) ||
    //     //     tryAllStep(stone, lastStep + 1, dp, stoneSet, lastStone)) {
    //     //         return true;
    //     //     }
    //     // }
    //     return tryAllStep(0, 1, dp, stoneSet, lastStone);
    // }
    // private boolean tryAllStep(int stone, int currStep, Map<Integer, Integer> dp,  Set<Integer> stoneSet, int lastStone) {
    //     if (currStep <= 0) {
    //         return false;
    //     }
    //     int nextStone = stone + currStep;
    //     if (lastStone == nextStone) {
    //         return true;
    //     }
    //     if (stoneSet.contains(nextStone)) {
    //         if (dp.containsKey(nextStone) && dp.get(nextStone) == currStep) {
    //             return false;
    //         }
    //         dp.put(nextStone, currStep);
    //         if (tryAllStep(nextStone, currStep - 1, dp, stoneSet, lastStone) ||
    //         tryAllStep(nextStone, currStep, dp, stoneSet, lastStone) ||
    //         tryAllStep(nextStone, currStep + 1, dp, stoneSet, lastStone)) {
    //             return true;
    //         }
    //     }
    //     return false;
    // }
}
// @lc code=end

