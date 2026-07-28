/*
 * @lc app=leetcode id=52 lang=java
 *
 * [52] N-Queens II
 */

// @lc code=start
class Solution {
    class WrapInteger {
        int mInteger;
        public WrapInteger() {
            mInteger = 0;
        }
        public void increInt() {
            mInteger++;
        }
        public int getInt() {
            return mInteger;
        }
    }
    public int totalNQueens(int n) {
        WrapInteger resInt = new WrapInteger();
        boolean[][] chessBoard = new boolean[n][n];
        helper(resInt, chessBoard, 0);
        return resInt.getInt();
    }
    private void helper(WrapInteger resInt, boolean[][] chessBoard, int row) {
        if (row >= chessBoard.length) {
            resInt.increInt();
            return;
        }
        for(int i = 0; i < chessBoard.length; i++) {
            if (isVaild(row, i, chessBoard)) {
                chessBoard[row][i] = true;
                helper(resInt, chessBoard, row + 1);
                chessBoard[row][i] = false;

            }
        }
    }
    private boolean isVaild(int row, int column, boolean[][] chessBoard) {
        // 注意，当前row往后的都还没有遍历到，所以不需要去验证row后面的
        for(int i = 0; i < row; i++) {
            if (chessBoard[i][column]) {
                return false;
            }
        }
        for (int i = row, j = column; i >= 0 && j >= 0; i--, j--) {
            if (chessBoard[i][j]) {
                return false;
            }
        }
        for (int i = row, j = column; i >= 0 && j < chessBoard.length; i--, j++) {
            if (chessBoard[i][j]) {
                return false;
            }
        }
        return true;
    }
}
// @lc code=end

