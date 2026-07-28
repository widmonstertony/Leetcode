/*
 * @lc app=leetcode id=134 lang=java
 *
 * [134] Gas Station
 */

// @lc code=start
class Solution {
    public int canCompleteCircuit(int[] gas, int[] cost) {
        int totalGas = 0, currSum = 0, startIdx = 0;
        for (int i = 0; i < gas.length; i++) {
            totalGas += gas[i] - cost[i];
            currSum += gas[i] - cost[i];
            // 若currSum小于0了，则说明从起点到这个点中间的任何一个点都不能作为起点
            // 则把起点设为下一个点
            if (currSum < 0) {
                startIdx = i + 1;
                currSum = 0;
            }
        }
        // gas的总量要大于cost的总量，这样才会有起点的存在
        if (totalGas < 0) {
            return -1;
        }
        else {
            return startIdx;
        }
    }
    // 暴力解
    // public int canCompleteCircuit(int[] gas, int[] cost) {
    //     for (int i = 0; i < gas.length; i++) {
    //         int startGas = gas[i];
    //         int j = i, currGasNum = 0;
    //         while (startGas >= cost[j] && currGasNum < gas.length) {
    //             startGas -= cost[j];
    //             if (j == gas.length - 1) {
    //                 j = 0;
    //             }
    //             else {
    //                 j++;
    //             }
    //             startGas += gas[j];
    //             currGasNum++;
    //         }
    //         if (currGasNum == gas.length) {
    //             return i;
    //         }
    //     }
    //     return -1;
    // }
}
// @lc code=end

