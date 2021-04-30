/*
 * @lc app=leetcode id=37 lang=java
 *
 * [37] Sudoku Solver
 */

// @lc code=start
class Solution {
    public void solveSudoku(char[][] board) {
        helper(board, 0, 0);
    }
    private boolean helper(char[][] board, int i, int j) {
        if (i >= board.length) {
            return true;
        }
        if (j >= board[i].length) {
            return helper(board, i + 1, 0);
        }
        if (board[i][j] != '.') {
            return helper(board, i, j + 1);
        }
        else {
            for (int tryNum = 1; tryNum <= 9; tryNum++) {
                board[i][j] = (char) (tryNum + '0');
                if (isValid(board, i, j)) {
                    if (helper(board, i, j + 1)) {
                        return true;
                    }
                }
                board[i][j] = '.';
            }
        }
        return false;
    }
    private boolean isValid(char[][] board, int i, int j) {
        for (int rowIdx = 0; rowIdx < board.length; rowIdx++) {
            if (rowIdx != i && board[rowIdx][j] == board[i][j]) {
                return false;
            }
        }
        for (int colIdx = 0; colIdx < board[0].length; colIdx++) {
            if (colIdx != j && board[i][colIdx] == board[i][j]) {
                return false;
            }
        }
        int cellSize = (int)Math.sqrt(board.length);
        for (int rowIdx = cellSize * (i / cellSize); rowIdx < cellSize * (i / cellSize) + cellSize; rowIdx++) {
            for (int colIdx = cellSize * (j / cellSize); colIdx < cellSize * (j / cellSize) + cellSize; colIdx++) {
                if ( (rowIdx != i || colIdx != j) && board[rowIdx][colIdx] == board[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
// @lc code=end

