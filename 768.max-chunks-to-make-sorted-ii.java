import java.util.Arrays;
import java.util.Stack;

/*
 * @lc app=leetcode id=768 lang=java
 *
 * [768] Max Chunks To Make Sorted II
 */

// @lc code=start
class Solution {

    public int maxChunksToSorted(int[] arr) {

        // maxOfLeft[i] 表示 [0, i] 范围内最大的数字
        // minOfRight[i] 表示 [i, n-1] 范围内最小的数字
        int[] maxOfLeft = new int[arr.length];
        int[] minOfRight = new int[arr.length];

        // 初始化maxOfLeft数组，也就是dp，把每一个当前值和之前左边的值对比
        for (int i = 0; i < arr.length; i++) {
            if (i == 0) {
                maxOfLeft[i] = arr[i];
            }
            else {
                maxOfLeft[i] = Math.max(maxOfLeft[i - 1], arr[i]);
            }
        }

        // 初始化minOfRight数组，也就是dp，把每一个当前值和之前右边的值对比
        for (int i = arr.length - 1; i >= 0; i--) {
            if (i == arr.length - 1) {
                minOfRight[i] = arr[i];
            }
            else {
                minOfRight[i] = Math.min(minOfRight[i + 1], arr[i]);
            }
        }

        int res = 1;
        // 可以拆分为块儿的前提是
        //  之后的数字不能比当前块儿中的任何数字小
        //  不然那个较小的数字就无法排到前面
        for (int i = 0; i < arr.length - 1; i++) {
            // 之后的数的最小值都不小于之前的数的最大值
            // 说明这里可以分块
            if (maxOfLeft[i] <= minOfRight[i + 1]) {
                res++;
            }
        }

        return res;
    }

    // 可以拆分为块儿的前提是
    // 之后的数字不能比当前块儿中的任何数字小
    // 不然那个较小的数字就无法排到前面
    // public int maxChunksToSorted(int[] arr) {
    //     //创建一个单调递增的stack
    //     Stack<Integer> increSt = new Stack<>();

    //     // 遍历每个数字
    //     for (int currNum : arr) {

    //         // 如果当前值不小于stack最大的值，说明当前值可能可以作为一个块的最大的数
    //         if (increSt.isEmpty() || currNum >= increSt.peek()) {
    //             increSt.add(currNum);
    //         }

    //         else {
    //             int currMax = increSt.pop();

    //             // 如果当前值小于于stack的最大值（top）
    //             // 说明当前的最大值就是一个能拆开的块的最大值
    //             // 把stack里所有大于当前值的取出来，和当前最大值，当前值一起组成新的块
    //             while (!increSt.isEmpty() && currNum < increSt.peek()) {
    //                 increSt.pop();
    //             }

    //             //再把当前最大值放回去，代表这个块里最大的数
    //             increSt.add(currMax);
    //         }

    //     }
    //     // 单调递增stack里的每个数都代表了每个分出的块里的最大值
    //     return increSt.size();
    // }

}
// @lc code=end

