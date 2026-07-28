/*
 * @lc app=leetcode id=36 lang=java
 *
 * [36] Valid Sudoku
 */

// @lc code=start
class Solution {
    public boolean isValidSudoku(char[][] board) {
        final int ROW_SIZE = 9, COLUMN_SIZE = 9, NUM_SIZE = 9;
        if (board == null || board.length != ROW_SIZE || board[0].length != COLUMN_SIZE) {
            return false;
        }
        boolean[][] rowExisted = new boolean[ROW_SIZE][NUM_SIZE + 1];
        boolean[][] columnExisted = new boolean[COLUMN_SIZE][NUM_SIZE  + 1];
        boolean[][][] cellExisted = new boolean[ROW_SIZE / 3][COLUMN_SIZE / 3][NUM_SIZE + 1];
        for (int i = 0; i < ROW_SIZE; i++) {
            for (int j = 0; j < COLUMN_SIZE; j++) {
                int currNum = board[i][j] - '0';
                if (currNum < 1 || currNum > 9) {
                    continue;
                }
                if (rowExisted[i][currNum]) {
                    return false;
                }
                else if (columnExisted[j][currNum]) {
                    return false;
                }
                else if (cellExisted[i / 3][j / 3][currNum]) {
                    return false;
                }
                else {
                    rowExisted[i][currNum] = true;
                    columnExisted[j][currNum] = true;
                    cellExisted[i / 3][j / 3][currNum] = true;
                }
            }
        }
        return true;

    }
}
// @lc code=end

