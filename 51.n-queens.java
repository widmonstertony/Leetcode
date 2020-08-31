/*
 * @lc app=leetcode id=51 lang=java
 *
 * [51] N-Queens
 */

// @lc code=start
class Solution {
    public List<List<String>> solveNQueens(int n) {
        List<List<String>> resList = new ArrayList<>();
        boolean[][] chessBoard = new boolean[n][n];
        helper(resList, chessBoard, 0);
        return resList;
    }
    private void helper(List<List<String>> resList, boolean[][] chessBoard, int row) {
        if (row >= chessBoard.length) {
            List<String> currList = new ArrayList<>();
            for (int i = 0; i < chessBoard.length; i++) {
                StringBuilder currLevelStr = new StringBuilder();
                for (int j = 0; j < chessBoard.length; j++) {
                    if (chessBoard[i][j]) {
                        currLevelStr.append('Q');
                    }
                    else {
                        currLevelStr.append('.');
                    }
                }
                currList.add(currLevelStr.toString());
            }
            resList.add(currList);
            return;
        }
        for(int i = 0; i < chessBoard.length; i++) {
            if (isVaild(row, i, chessBoard)) {
                chessBoard[row][i] = true;
                helper(resList, chessBoard, row + 1);
                chessBoard[row][i] = false;

            }
        }
    }
    private boolean isVaild(int row, int column, boolean[][] chessBoard) {
        // 注意，当前row往后的都还没有遍历到，所以不需要去验证row后面的
        // for(int i = 0; i < chessBoard.length; i++) {
        //     if (chessBoard[row][i]) {
        //         return false;
        //     }
        // }
        for(int i = 0; i < row; i++) {
            if (chessBoard[i][column]) {
                return false;
            }
        }
        // for (int i = row, j = column; i < chessBoard.length && j < chessBoard.length; i++, j++) {
        //     if (chessBoard[i][j]) {
        //         return false;
        //     }
        // }
        for (int i = row, j = column; i >= 0 && j >= 0; i--, j--) {
            if (chessBoard[i][j]) {
                return false;
            }
        }
        // for (int i = row, j = column; i < chessBoard.length && j >= 0; i++, j--) {
        //     if (chessBoard[i][j]) {
        //         return false;
        //     }
        // }
        for (int i = row, j = column; i >= 0 && j < chessBoard.length; i--, j++) {
            if (chessBoard[i][j]) {
                return false;
            }
        }
        return true;
    }
}
// @lc code=end

