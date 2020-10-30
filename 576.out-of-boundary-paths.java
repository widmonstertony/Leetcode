/*
 * @lc app=leetcode id=576 lang=java
 *
 * [576] Out of Boundary Paths
 */

// @lc code=start
class Solution {
    public int findPaths(int m, int n, int N, int i, int j) {
        List<List<Map<Integer, Integer>>> memeList = new ArrayList<>();
        for (int x = 0; x < m; x++) {
            List<Map<Integer, Integer>> currList = new ArrayList<>();
            memeList.add(currList);
            for (int y = 0; y < n; y++) {
                currList.add(new HashMap<>());
            }
        }
        return helper(m, n, N, i, j, memeList);
    }
    private int helper(int m, int n, int N, int i, int j, List<List<Map<Integer, Integer>>> memeList) {
        if (N == 0) {
            if (i < 0 || j < 0 || i >= m || j >= n) {
                return 1;
            }
            else {
                return 0;
            }
        }
        if (i < 0 || j < 0 || i >= m || j >= n) {
            return 1;
        }
        if (memeList.get(i).get(j).containsKey(N)) {
            return memeList.get(i).get(j).get(N);
        }
        int[][] directions = new int[][]{{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        long currCnt = 0;
        for (int[] direction: directions) {
            currCnt += helper(m, n, N - 1, i + direction[0], j + direction[1], memeList);   
        }
        // 因为结果可能会非常大，所以如果太大就除以1000000007
        int curr = (int)(currCnt % 1000000007);
        memeList.get(i).get(j).put(N, curr);
        return curr;
    }
}
// @lc code=end

