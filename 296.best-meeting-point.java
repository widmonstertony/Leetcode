/*
 * @lc app=leetcode id=296 lang=java
 *
 * [296] Best Meeting Point

 */

// @lc code=start
class Solution {
    // O(mn)
    public int minTotalDistance(int[][] grid) {
        List<Integer> rows = new ArrayList<>();
        List<Integer> cols = new ArrayList<>();
        // 找到所有的1的坐标
        // i一定是从小到大排序的
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == 1) {
                    rows.add(i);
                }
            }
        }
        // j一定是从小到大排序的
        for (int j = 0; j < grid[0].length; j++) {
            for (int i = 0; i < grid.length; i++) {
                if (grid[i][j] == 1) {
                    cols.add(j);
                }
            }
        }
        int res = 0, i = 0, j = rows.size() - 1;
        // 用最后一个坐标减去第一个坐标, 这样也就是这两个坐标相隔的距离
        // 再加上，倒数第二个坐标减去第二个坐标, 以此类推
        // 横竖都加上，这就是曼哈顿距离
        while (i < j) {
            res += rows.get(j) - rows.get(i) + cols.get(j) - cols.get(i);
            i++;
            j--;
        }
        return res;
    }
    // O(mnlogn)
    // public int minTotalDistance(int[][] grid) {
    //     List<Integer> rows = new ArrayList<>();
    //     List<Integer> cols = new ArrayList<>();
    //     // 找到所有的1的坐标
    //     for (int i = 0; i < grid.length; i++) {
    //         for (int j = 0; j < grid[i].length; j++) {
    //             if (grid[i][j] == 1) {
    //                 rows.add(i);
    //                 cols.add(j);
    //             }
    //         }
    //     }
    //     // 给位置排好序
    //     // 因为我们的i一定是从小到大排序的，所以这里只需要排序cols里的j坐标
    //     Collections.sort(cols);
    //     int res = 0, i = 0, j = rows.size() - 1;
    //     // 用最后一个坐标减去第一个坐标, 这样也就是这两个坐标相隔的距离
    //     // 再加上，倒数第二个坐标减去第二个坐标, 以此类推
    //     // 横竖都加上，这就是曼哈顿距离
    //     while (i < j) {
    //         res += rows.get(j) - rows.get(i) + cols.get(j) - cols.get(i);
    //         i++;
    //         j--;
    //     }
    //     return res;
    // }
}
// @lc code=end

