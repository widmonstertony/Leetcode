/*
 * @lc app=leetcode id=605 lang=java
 *
 * [605] Can Place Flowers
 */

// @lc code=start
class Solution {
    public boolean canPlaceFlowers(int[] flowerbed, int n) {
        int lastFlowerIdx = -1;
        for (int i = 0; i < flowerbed.length; i++) {
            // 如果当前已经插上花了
            if (flowerbed[i] == 1) {
                if (lastFlowerIdx != -1) {
                    // 两个花之间的距离再减一，除以二就是可以插的位置
                    n -= (i - lastFlowerIdx - 2) / 2;
                }
                else {
                    n -= i / 2;
                }
                lastFlowerIdx = i;
            }
        }
        if (lastFlowerIdx == -1) {
            n -= (flowerbed.length + 1)/ 2;
        }
        else {
            n -= (flowerbed.length - lastFlowerIdx - 1) / 2;
        }
        return n <= 0;
    }
    // backtracking, 会TLE
    // public boolean canPlaceFlowers(int[] flowerbed, int n) {
    //     public boolean canPlaceFlowers(int[] flowerbed, int n) {
    //         return canPlaceFlowers(flowerbed, 0, n);
    //     }
    //     private boolean canPlaceFlowers(int[] flowerbed, int start, int n) {
    //         if (n == 0) {
    //             return true;
    //         }
    //         for (int i = start; i < flowerbed.length; i++) {
    //             if (flowerbed[i] == 1) {
    //                 continue;
    //             }
    //             if (i == 0) {
    //                 if (i < flowerbed.length - 1 && flowerbed[i + 1] == 1) {
    //                     continue;
    //                 }
    //                 flowerbed[i] = 1;
    //                 if (canPlaceFlowers(flowerbed, i + 1, n - 1)) {
    //                     flowerbed[i] = 0;
    //                     return true;
    //                 }
    //                 flowerbed[i] = 0;
    //             }
    //             else {
    //                 if (flowerbed[i - 1] != 1) {
    //                     if (i < flowerbed.length - 1 && flowerbed[i + 1] == 1) {
    //                         continue;
    //                     }
    //                     flowerbed[i] = 1;
    //                     if (canPlaceFlowers(flowerbed, i + 1, n - 1)) {
    //                         flowerbed[i] = 0;
    //                         return true;
    //                     }
    //                     flowerbed[i] = 0;
    //                 }
    //             }
    //         }
    //         return false;
    //     }
    // }
}
// @lc code=end

