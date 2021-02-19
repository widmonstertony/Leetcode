/*
 * @lc app=leetcode id=286 lang=java
 *
 * [286] Walls and Gates
 */

// @lc code=start
class Solution {
    private final int EMPTY = Integer.MAX_VALUE;
    private final int GATE = 0;
    private final int[][] DIRECTIONS = new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
    public void wallsAndGates(int[][] rooms) {
        Queue<int[]> roomQueue = new LinkedList<>();
        // 首先把gate都找到
        for (int i = 0; i < rooms.length; i++) {
            for (int j = 0; j < rooms[0].length; j++) {
                if (rooms[i][j] == GATE) {
                    roomQueue.add(new int[]{i, j});
                }
            }
        }
        // 再对每个gate进行bfs的更新
        while (!roomQueue.isEmpty()) {
            int[] currRoom = roomQueue.poll();
            int i = currRoom[0], j = currRoom[1];
            // 对当前room的每个方向进行探索
            for (int[] direction : DIRECTIONS) {
                int newI = i + direction[0], newJ = j + direction[1];
                // 如果超过边界，跳过
                if (newI < 0 || newJ < 0 || newI >= rooms.length || newJ >= rooms[0].length) {
                    continue;
                }
                // 如果新的位置已经有数字了，也就说明之前已经有一条路径到过当前位置
                // 也就意味着那条路径是更短的答案，则跳过新的位置
                if (rooms[newI][newJ] != EMPTY) {
                    continue;
                }
                // 更新新位置的数字
                rooms[newI][newJ] = rooms[i][j] + 1;
                // 把新位置放进queue
                roomQueue.add(new int[]{newI, newJ});
            }
        }
    }
}
// @lc code=end
