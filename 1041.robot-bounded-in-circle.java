/*
 * @lc app=leetcode id=1041 lang=java
 *
 * [1041] Robot Bounded In Circle
 */

// @lc code=start
class Solution {
    public boolean isRobotBounded(String instructions) {
        // 上左下右，单位为1的四个基础向量
        final int[][] directions = new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        int[] robotIdx = new int[]{0, 0};
        int currDirection = 0;
        for (char instruction : instructions.toCharArray()) {
            switch (instruction) {
                case 'G':
                    robotIdx[0] += directions[currDirection][0];
                    robotIdx[1] += directions[currDirection][1];
                    break;
                case 'L':
                    currDirection = (currDirection + directions.length - 1) % directions.length;
                    break;
                case 'R':
                    currDirection = (currDirection + 1) % directions.length;
                    break;
                default:
                    break;
            }
        }
        // 如果还在原点，说明会回到原点
        // 或者如果当前方向和初始方向不一致
        // 说明每次执行完一遍指令，机器人都会运动 长度一样，但方向和初始方向角度相差90或者180的向量
        // 多运行几次，向量就会全部被抵消而归零
        return (robotIdx[0] == 0 && robotIdx[1] == 0) || currDirection != 0;
    }
}
// @lc code=end

