/*
 * @lc app=leetcode id=419 lang=java
 *
 * [419] Battleships in a Board
 */

// @lc code=start
class Solution {
    public int countBattleships(char[][] board) {
        int res = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                // 如果当前坐标是battleship
                if (board[i][j] == 'X') {
                    // 并且它的左边和上边都没有battleship，说明它是这个ship的左上顶点
                    // 计数加一
                    if ((i - 1 >= 0 && board[i - 1][j] == 'X') 
                        || (j - 1 >= 0 && board[i][j - 1] == 'X')) {
                        continue;
                    }
                    res++;
                }
            }
        }
        return res;
    }
    // dfs解法
    // public int countBattleships(char[][] board) {
    //     // 也可以不用，直接修改原数组
    //     boolean[][] visited = new boolean[board.length][board[0].length];
    //     final int[][] directions = new int[][]{{0, 1}, {0, -1}, {-1, 0}, {1, 0}};
    //     int res = 0;
    //     for (int i = 0; i < board.length; i++) {
    //         for (int j = 0; j < board[i].length; j++) {
    //             if (!visited[i][j] && board[i][j] == 'X') {
    //                 res++;
    //                 dfsFindShip(board, i, j, visited, directions);
    //             }
    //         }
    //     }
    //     return res;
    // }

    // private void dfsFindShip(char[][] board, int i, int j, boolean[][] visited, int[][] directions) {
    //     if (i < 0 || j < 0 || i >= board.length || j >= board[i].length) {
    //         return;
    //     }
    //     if (visited[i][j] || board[i][j] != 'X') {
    //         return;
    //     }
    //     visited[i][j] = true;
    //     for (int[] direction : directions) {
    //         dfsFindShip(board, i + direction[0], j + direction[1], visited, directions);
    //     }
    // }
}
// @lc code=end

