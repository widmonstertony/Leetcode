import java.util.PriorityQueue;

/*
 * @lc app=leetcode id=378 lang=java
 *
 * [378] Kth Smallest Element in a Sorted Matrix
 */

// @lc code=start
class Solution {
    // O(nlgX)
    public int kthSmallest(int[][] matrix, int k) {
        // 最左上角和最右下角作为我们的搜索范围的数
        int left = matrix[0][0], right = matrix[matrix.length - 1][matrix[0].length - 1];
        while (left <= right) {
            // 算出中间数字mid
            int mid = left + (right - left) / 2;
            int currCnt = upperBound(matrix, mid);
            // 中间数的第几小，比k还小，说明中间数取小了，移动左坐标
            if (currCnt < k) {
                left = mid + 1;
            }
            else {
                right = mid - 1;
            }
        }
        return left;
    }
    private int upperBound(int[][] matrix, int target) {
        // 从数组的左下角开始搜索
        int i = matrix.length - 1, j = 0, currCnt = 0;
        while (i >= 0 && j <= matrix.length - 1) {
            // 如果当前数比target小，说明当前列的上面所有数字都小于目标值
            if (matrix[i][j] <= target) {
                currCnt += i + 1;
                j++;
            }
            else {
                i--;
            }
        }
        return currCnt;
    }

    // O(nlgn*lgX) X为最大值和最小值的差值
    // public int kthSmallest(int[][] matrix, int k) {
    //     // 最左上角和最右下角作为我们的搜索范围的数
    //     int left = matrix[0][0], right = matrix[matrix.length - 1][matrix[0].length - 1];
    //     while (left <= right) {
    //         // 算出中间数字mid
    //         int mid = left + (right - left) / 2;
    //         int currCnt = 0;
    //         // 遍历所有行，把大于当前数的所有数个数加起来
    //         // 这样就知道中间数是第几小的数
    //         for (int i = 0; i < matrix.length; i++) {
    //             currCnt += upper_bound(matrix, i, mid);
    //         }
    //         // 中间数的第几小，比k还小，说明中间数取小了，移动左坐标
    //         if (currCnt < k) {
    //             left = mid + 1;
    //         }
    //         else {
    //             right = mid - 1;
    //         }
    //     }
    //     return left;
    // }
    
    // // 找target在matrix[i]的上界，也就是第一个比target大的数的坐标
    // private int upper_bound(int[][] matrix, int i, int target) {
    //     int left = 0, right = matrix[i].length - 1;
    //     while (left <= right) {
    //         int mid = left + (right - left) / 2;
    //         if (matrix[i][mid] <= target) {
    //             left = mid + 1;
    //         }
    //         else {
    //             right = mid - 1;
    //         }
    //     }
    //     return left;
    // }
    // Max Heap
    // public int kthSmallest(int[][] matrix, int k) {
    //     // 用一个最大堆，也就是一个自动从大到小排列的queue
    //     PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> {
    //         return b - a;
    //     });
    //     for (int i = 0; i < matrix.length; i++) {
    //         for (int j = 0; j < matrix[i].length; j++) {
    //             maxHeap.add(matrix[i][j]);
    //             while (maxHeap.size() > k) {
    //                 maxHeap.poll();
    //             }
    //         }
    //     }
    //     return maxHeap.peek();
    // }
    // brute force
    // public int kthSmallest(int[][] matrix, int k) {
    //     int[] visited = new int[matrix.length];
    //     for (int i = 0; i < k; i++) {
    //         int currRes = Integer.MAX_VALUE;
    //         int currIdx = -1;
    //         for (int j = 0; j < matrix.length; j++) {
    //             int currVisitedJ = visited[j];
    //             if (currVisitedJ < matrix[j].length && matrix[j][currVisitedJ] < currRes) {
    //                 currIdx = j;
    //                 currRes =  matrix[j][currVisitedJ];
    //             }
    //         }
    //         visited[currIdx]++;
    //         if (i == k - 1) {
    //             return currRes;
    //         }
    //     }
    //     return -1;
    // }

}
// @lc code=end

