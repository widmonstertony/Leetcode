import java.lang.reflect.Array;

/*
 * @lc app=leetcode id=135 lang=java
 *
 * [135] Candy
 */

// @lc code=start
class Solution {
    public int candy(int[] ratings) {
        int res = 0;
        int[] candys = new int[ratings.length];
        Arrays.fill(candys, 1);
        // 第一遍从左向右遍历
        // 如果右边的小盆友的等级高，等加一个糖果
        // 这样保证了一个方向上高等级的糖果多
        for (int i = 0; i < ratings.length - 1; i++) {
            if (ratings[i + 1] > ratings[i]) {
                candys[i + 1] = candys[i] + 1;
            }
        }
        // 然后再从右向左遍历一遍
        // 如果相邻两个左边的等级高，而左边的糖果又少的话
        // 则左边糖果数为右边糖果数加一
        for (int i = ratings.length - 1; i > 0; i--) {
            if (ratings[i - 1] > ratings[i]) {
                candys[i - 1]= Math.max(candys[i - 1], candys[i] + 1);
            }
        }
        // 因为从左是最优解，同时也满足了从右的最优解，这也是全局的最优解
        for (int candy : candys) {
            res += candy;
        }
        return res;
    }
}
// @lc code=end

