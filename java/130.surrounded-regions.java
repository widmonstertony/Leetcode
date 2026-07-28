/*
 * @lc app=leetcode id=130 lang=java
 *
 * [130] Surrounded Regions
 */

// @lc code=start
class Solution {
    public void solve(char[][] board) {
        if (board == null || board.length == 0) {
            return;
        }
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                // 如果当前位置在边缘，说明当前不会被包围着
                // dfs去把所有当前位置连着的位置的0记为$
                if (i == 0 || j == 0 || i == board.length - 1 || j == board[0].length - 1) {
                    dfsMarkEveryO(board, i, j);
                }
            }
        }
        // 然后把剩下没有标记为$的0改成X
        // 把标记为$的0改回来，因为它们没有被包围着
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == 'O') {
                    board[i][j] = 'X';
                }
                else if (board[i][j] == '$') {
                    board[i][j] = 'O';
                }
            }
        }
    }
    private void dfsMarkEveryO(char[][] board, int i, int j) {
        // 边缘检测
        if (i < 0 || i >= board.length || j < 0 || j > board[0].length - 1) {
            return;
        }
        if (board[i][j] == 'O') {
            board[i][j] = '$';
            dfsMarkEveryO(board, i - 1, j);
            dfsMarkEveryO(board, i + 1, j);
            dfsMarkEveryO(board, i, j - 1);
            dfsMarkEveryO(board, i, j + 1);
        }
    }
}
// @lc code=end

