/*
 * @lc app=leetcode id=957 lang=java
 *
 * [957] Prison Cells After N Days
 */

// @lc code=start
class Solution {
    public int[] prisonAfterNDays(int[] cells, int N) {
        // 用HashMap来记下所有的天数情况，一旦有重复就跳过
        return helper(cells, N, new HashMap<Integer, Integer>());
    }
    public int[] helper(int[] cells, int N, Map<Integer, Integer> stateMap) { 
        if (N == 0) {
            return cells;
        }
        // 因为最多有8个cells，一个int有32个bit，所以可以用来记下每一个情况
        int stateFlag = 0;
        int[] tmpCells = new int[cells.length];
        for (int i = 1; i < cells.length - 1; i++) {
            if (cells[i - 1] == cells[i + 1]) {
                tmpCells[i] = 1;
            }
            else {
                tmpCells[i] = 0;
            }
            stateFlag = stateFlag | (cells[i] << i);
        }
        tmpCells[0] =0;
        tmpCells[cells.length - 1] = 0;
        stateFlag = stateFlag | (cells[0]);
        stateFlag = stateFlag | (cells[cells.length - 1] << cells.length - 1);
        // 如果当前情况之前已经出现过，说明有loop
        if (stateMap.containsKey(stateFlag)) {
            int time = stateMap.get(stateFlag) - N;
            // 如果余数为零，说明当前就是结果
            if (N % time == 0) {
                return cells;
            }
            // 否则跳过loop的那些步骤
            return helper(tmpCells, N % time - 1, stateMap);
        }
        stateMap.put(stateFlag, N);
        return helper(tmpCells, N - 1, stateMap);
    }
}
// @lc code=end

