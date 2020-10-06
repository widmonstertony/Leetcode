/*
 * @lc app=leetcode id=348 lang=java
 *
 * [348] Design Tic-Tac-Toe
 */

// @lc code=start
class TicTacToe {
    // 玩家1在第一行某一列放了一个子，那么rows[0]自增1
    // 如果玩家2在第一行某一列放了一个子，则rows[0]自减1
    // 那么只有当rows[0]等于n或者-n的时候，表示第一行的子都是一个玩家放的
    // 则游戏结束返回该玩家即可
    int[] rowArr;
    int[] colArr;
    // 变量对角线crossCnt和逆对角线revCrossCnt
    int crossCnt;
    int revCrossCnt;
    /** Initialize your data structure here. */
    public TicTacToe(int n) {
        rowArr = new int[n];
        colArr = new int[n];
        crossCnt = 0;
        revCrossCnt = 0;
    }
    /** Player {player} makes a move at ({row}, {col}).
    @param row The row of the board.
    @param col The column of the board.
    @param player The player, can be either 1 or 2.
    @return The current winning condition, can be either:
            0: No one wins.
            1: Player 1 wins.
            2: Player 2 wins. */
    public int move(int row, int col, int player) {
        int add = player == 1 ? 1 : -1;
        int n = rowArr.length;
        rowArr[row] += add;
        colArr[col] += add;
        crossCnt += (row == col) ? add : 0;
        revCrossCnt += (row == n - col - 1) ? add : 0;
        if (Math.abs(rowArr[row]) == n || Math.abs(colArr[col]) == n) {
            return player;
        }
        if (Math.abs(crossCnt) == n || Math.abs(revCrossCnt) == n) {
            return player;
        }
        return 0;
    }
}
// 暴力解，move O(n^2)
// class TicTacToe {
//     int[][] mBoardArr;

//     /** Initialize your data structure here. */
//     public TicTacToe(int n) {
//         mBoardArr = new int[n][n];
//     }
    
//     /** Player {player} makes a move at ({row}, {col}).
//         @param row The row of the board.
//         @param col The column of the board.
//         @param player The player, can be either 1 or 2.
//         @return The current winning condition, can be either:
//                 0: No one wins.
//                 1: Player 1 wins.
//                 2: Player 2 wins. */
//     public int move(int row, int col, int player) {
//         mBoardArr[row][col] = player;
//         int allCnt = mBoardArr.length;
//         // 先检查垂直和水平是否全满
//         for (int i = 0; i < mBoardArr.length; i++) {
//             if (mBoardArr[i][col] == player) {
//                 allCnt--;
//             }
//             else {
//                 break;
//             }
//         }
//         if (allCnt == 0) {
//             return player;
//         }
//         allCnt = mBoardArr.length;
//         for (int j = 0; j < mBoardArr.length; j++) {
//             if (mBoardArr[row][j] == player) {
//                 allCnt--;
//             }
//             else {
//                 break;
//             }
//         }
//         if (allCnt == 0) {
//             return player;
//         }
//         // 再检查斜上斜下
//         allCnt = mBoardArr.length + 1;
//         for (int i = row, j = col; i < mBoardArr.length && j < mBoardArr.length; i++, j++) {
//             if (mBoardArr[i][j] == player) {
//                 allCnt--;
//             }
//             else {
//                 break;
//             }
//         }
//         for (int i = row, j = col; i >= 0 && j >= 0; i--, j--) {
//             if (mBoardArr[i][j] == player) {
//                 allCnt--;
//             }
//             else {
//                 break;
//             }
//         }
//         if (allCnt <= 0) {
//             return player;
//         }
//         allCnt = mBoardArr.length + 1;
//         for (int i = row, j = col; i < mBoardArr.length && j >= 0; i++, j--) {
//             if (mBoardArr[i][j] == player) {
//                 allCnt--;
//             }
//             else {
//                 break;
//             }
//         }
//         for (int i = row, j = col; i >= 0 && j < mBoardArr.length; i--, j++) {
//             if (mBoardArr[i][j] == player) {
//                 allCnt--;
//             }
//             else {
//                 break;
//             }
//         }
//         if (allCnt <= 0) {
//             return player;
//         }
//         return 0;
//     }
// }

/**
 * Your TicTacToe object will be instantiated and called as such:
 * TicTacToe obj = new TicTacToe(n);
 * int param_1 = obj.move(row,col,player);
 */
// @lc code=end

