/*
 * @lc app=leetcode id=417 lang=java
 *
 * [417] Pacific Atlantic Water Flow
 */

// @lc code=start
class Solution {
    public List<List<Integer>> pacificAtlantic(int[][] matrix) {
        List<List<Integer>> resList = new ArrayList<>();
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return resList;
        }
        // 先把可以留到pacific的记录下来
        boolean[][] iSToPacific = new boolean[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            markPacific(i, 0, iSToPacific, matrix);
        }
        for (int j = 0; j < matrix[0].length; j++) {
            markPacific(0, j, iSToPacific, matrix);
        }
        // 然后尝试可以留到atlantic的位置，如果那个位置也可以留到pacific
        // 那么它就是答案之一
        boolean[][] hasVisited = new boolean[matrix.length][matrix[0].length];
        for (int i = matrix.length - 1; i >= 0; i--) {
            findPacific(i, matrix[0].length - 1, iSToPacific, matrix, resList, hasVisited);
        }
        for (int j = matrix[0].length - 1; j >= 0; j--) {
            findPacific(matrix.length - 1, j, iSToPacific, matrix, resList, hasVisited);
        }
        return resList;
    }
    private void markPacific(int i, int j, boolean[][] iSToPacific, int[][] matrix) {
        if (i == 0 || j == 0) {
            iSToPacific[i][j] = true;
        }
        int[][] directions = new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        for (int[] direction : directions) {
            int newI = direction[0] + i;
            int newJ = direction[1] + j;
            if (newI < 0 || newJ < 0 || newI >= matrix.length || newJ >= matrix[0].length) {
                continue;
            }
            if (matrix[newI][newJ] >= matrix[i][j] && !iSToPacific[newI][newJ]) {
                iSToPacific[newI][newJ] = true;
                markPacific(newI, newJ, iSToPacific, matrix);
            }
        }
    }
    private void findPacific(int i, int j, boolean[][] iSToPacific, int[][] matrix, List<List<Integer>> resList, boolean[][] hasVisited) {
        if (hasVisited[i][j]) {
            return;
        }
        hasVisited[i][j] = true;
        if (iSToPacific[i][j]) {
            List<Integer> newResList = new ArrayList<>();
            newResList.add(i);
            newResList.add(j);
            resList.add(newResList);
        }
        int[][] directions = new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        for (int[] direction : directions) {
            int newI = direction[0] + i;
            int newJ = direction[1] + j;
            if (newI < 0 || newJ < 0 || newI >= matrix.length || newJ >= matrix[0].length) {
                continue;
            }
            if (matrix[newI][newJ] >= matrix[i][j]) {
                findPacific(newI, newJ, iSToPacific, matrix, resList, hasVisited);
            }
        }
    }
}
// @lc code=end

