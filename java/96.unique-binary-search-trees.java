/*
 * @lc app=leetcode id=96 lang=java
 *
 * [96] Unique Binary Search Trees
 */

// @lc code=start
class Solution {
    // Catalan Number 卡塔兰数
    public int numTrees(int n) {
        int[] dp = new int[n + 1];
        dp[0] = dp[1] = 1;
        // dp[2] =  dp[0] * dp[1]　　　(1为根的情况，则左子树一定不存在，右子树可以有一个数字)
        //        + dp[1] * dp[0]　　  (2为根的情况，则左子树可以有一个数字，右子树一定不存在)
        // dp[3] =  dp[0] * dp[2]　　　(1为根的情况，则左子树一定不存在，右子树可以有两个数字)
        //        + dp[1] * dp[1]　　  (2为根的情况，则左右子树都可以各有一个数字)
        //        + dp[2] * dp[0]　　  (3为根的情况，则左子树可以有两个数字，右子树一定不存在)
        for (int i = 2; i <= n; i++) {
            for (int j = 0; j < i; j++) {
                dp[i] += dp[j] * dp[i - 1 - j];
            }
        }
        return dp[n];
    }
    // 由卡特兰数的递推式还可以推导出其通项公式
    // 即 C(2n,n)/(n+1)，表示在2n个数字中任取n个数的方法再除以 n+1
    // public int numTrees(int n) {
    //     // 在相乘的时候为了防止整型数溢出，要将结果 res 定义为长整型
    //     long res = 1;
    //     for (int i = n + 1; i <= 2 * n; i++) {
    //         res = res * i / (i - n);
    //     }
    //     return (int)(res / (n + 1));
    // }
}
// @lc code=end

