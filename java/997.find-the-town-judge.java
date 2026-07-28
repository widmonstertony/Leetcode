/*
 * @lc app=leetcode id=997 lang=java
 *
 * [997] Find the Town Judge
 */

// @lc code=start
class Solution {
    // 一个array来计数，from则减，to则加
    public int findJudge(int N, int[][] trust) {
        int[] countArr = new int[N + 1];
        for (int[] eachTrust: trust) {
            countArr[eachTrust[0]]--;
            countArr[eachTrust[1]]++;
        }
        for (int i = 1; i <= N; i++) {
            // 只加不减的，说明他是judge
            if (countArr[i] == N - 1) {
                return i;
            }
        }
        return -1;
    }
    // 两个array来记录from和to
    // public int findJudge(int N, int[][] trust) {
    //     int[] fromArr = new int[N + 1];
    //     int[] toArr = new int[N + 1];
    //     for (int[] eachTrust : trust) {
    //         fromArr[eachTrust[0]]++;
    //         toArr[eachTrust[1]]++;
    //     }
    //     for (int i = 1; i <= N; i++) {
    //         // trust no one， everyone trust him
    //         // 说明他是judge
    //         if (fromArr[i] == 0 && toArr[i] == N - 1) {
    //             return i;
    //         }
    //     }
    //     return -1;
    // }
}
// @lc code=end

