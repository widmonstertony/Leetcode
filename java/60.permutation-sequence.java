import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * @lc app=leetcode id=60 lang=java
 *
 * [60] Permutation Sequence
 */

// @lc code=start
class Solution {
    public String getPermutation(int n, int k) {
        StringBuilder resBuilder = new StringBuilder();
        StringBuilder numBuilder = new StringBuilder("123456789");
        int[] factorials = new int[n];
        factorials[0] = 1;
        for (int i = 1; i < n; i++) {
            // 生成阶乘数组 0!, 1!, ..., (n - 1)!
            factorials[i] = factorials[i - 1] * i;
        }
        k--;
        for (int i = n; i >= 1; i--) {
            int numIdx = k / factorials[i - 1];
            k %= factorials[i - 1];
            resBuilder.append(numBuilder.charAt(numIdx));
            numBuilder.delete(numIdx, numIdx + 1);
        }
        return resBuilder.toString();
    }

    // 解法和46,47 permutation一样，只是加了个Counter，试到k就结束
    // public String getPermutation(int n, int k) {
    //     List<String> resList = new ArrayList<>();
    //     int[] nums = new int[n];
    //     for (int i = 1; i <= n; i++) {
    //         nums[i - 1] = i;
    //     }
    //     boolean[] visited = new boolean[n];
    //     WrapCounter resCnt = new WrapCounter();
    //     helper(nums, resList, resCnt, "", k, visited);
    //     return resList.get(k - 1);
    // }
    // private void helper(int[] nums, List<String> resList, WrapCounter resCnt, String currStr, int k, boolean[] visited) {
    //     if (currStr.length() == nums.length) {
    //         resList.add(currStr);
    //         resCnt.incre();
    //         return;
    //     }
    //     if (resCnt.getCnt() == k) {
    //         return;
    //     }
    //     for (int i = 0; i < nums.length; i++) {
    //         if (visited[i]) {
    //             continue;
    //         }
    //         visited[i] = true;
    //         helper(nums, resList, resCnt, currStr + nums[i], k, visited);
    //         visited[i] = false;

    //     }
    // }
    // class WrapCounter {
    //     private int mCnt;
    //     protected WrapCounter() {
    //         mCnt = 0;
    //     }
    //     protected void incre() {
    //         mCnt++;
    //     }
    //     protected int getCnt() {
    //         return mCnt;
    //     }
    // }
}
// @lc code=end

