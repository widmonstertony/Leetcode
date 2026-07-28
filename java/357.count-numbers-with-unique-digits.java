import java.util.ArrayList;
import java.util.List;

/*
 * @lc app=leetcode id=357 lang=java
 *
 * [357] Count Numbers with Unique Digits
 */

// @lc code=start
class Solution {
    public int countNumbersWithUniqueDigits(int n) {
        if (n == 0) {
            return 1;
        }
        int res = 10, cnt = 9;
        // 通项公式为f(k) = 9 * 9 * 8 * ... (9 - k + 2)
        for (int i = 2; i <= n; i++) {
            cnt *= (11 - i);
            res += cnt;
        }
        return res;
    }

    // backtracking 回溯法
    // public int countNumbersWithUniqueDigits(int n) {
    //     boolean[] visited = new boolean[10];
    //     List<String> resList = new ArrayList<>();
    //     // 空字符开始，代表0这个数字
    //     helper(resList, "", n, visited);
    //     return resList.size();
    // }
    // private void helper(List<String> resList, String currNum, int n, boolean[] visited) {
    //     // 如果位数已经超过n，停止尝试
    //     if (currNum.length() > n) {
    //         return;
    //     }
    //     resList.add(currNum);
    //     // 尝试从0到9的没有给数字
    //     for (int i = 0; i <= 9; i++) {
    //         // 如果当前生成的字符为空，则跳过试0的这一步
    //         // 如果已经visited过，也跳过
    //         if ((i == 0 && currNum.isEmpty()) || visited[i]) {
    //             continue;
    //         }
    //         visited[i] = true;
    //         helper(resList, currNum + i, n, visited);
    //         visited[i] = false;
    //     }
    // }
}
// @lc code=end

