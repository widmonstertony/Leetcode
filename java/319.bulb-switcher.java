import java.util.Arrays;

/*
 * @lc app=leetcode id=319 lang=java
 *
 * [319] Bulb Switcher
 */

// @lc code=start
class Solution {
    public int bulbSwitch(int n) {
        // 因为每个数的因数都会是一对
        // 比如 16 的（1，16），（2，8），（4，4）
        // 在第1次和16次灯泡会点亮熄灭，相互抵消，同理第2和8次
        // 但是，只有能被开方的数，比如16
        // 在第四次，会被打开后，没有另一个次数去关闭,就会一直是亮的
        // 问题简化成了求1到n的所有完全平方数的个数
        int res = 1;
        while (res * res <= n) {
            res++;
        }
        return res - 1;
    }

    // brute force, TLE
    // public int bulbSwitch(int n) {
    //     boolean[] bulbArr = new boolean[n];
    //     Arrays.fill(bulbArr, true);
    //     for (int i = 2; i < n; i++) {
    //         for (int j = 0; j < n; j++) {
    //             if ((j+1) % i == 0) {
    //                 bulbArr[j] = !bulbArr[j];
    //             }
    //         }
    //     }
    //     if (n > 1) {
    //         bulbArr[n - 1] = !bulbArr[n - 1];
    //     }
    //     int resCnt = 0;
    //     for (boolean bulb : bulbArr) {
    //         if (bulb) {
    //             resCnt++;
    //         }
    //     }
    //     return resCnt;
    // }
}
// @lc code=end

