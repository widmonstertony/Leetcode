import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/*
 * @lc app=leetcode id=846 lang=java
 *
 * [846] Hand of Straights
 */

// @lc code=start
class Solution {
    public boolean isNStraightHand(int[] hand, int W) {
        // 先把整个卡组排个序
        Arrays.sort(hand);
        // 这个算法有一个缺陷，如果hand里有非常多的重复数字的话，会TLE
        // 这种情况需要再加一个hashmap，具体解法在1296
        LinkedList<Integer> cardsQueue = new LinkedList<>();
        for (int eachCard: hand) {
            cardsQueue.add(eachCard);
        }
        while (!cardsQueue.isEmpty()) {
            Stack<Integer> newCardsSt = new Stack<>();
            int previousNum = cardsQueue.poll();
            int currCnt = 1;
            // 一直拿出可以连续的卡，如果有相同的就先跳过
            while (currCnt != W && !cardsQueue.isEmpty()) {
                int currNum = cardsQueue.poll();
                if (currNum == previousNum + 1) {
                    currCnt++;
                    previousNum = currNum;
                }
                else{
                    if (currNum == previousNum) {
                        newCardsSt.push(currNum);
                    }
                    else {
                        return false;
                    }
                }
            }
            if (currCnt != W) {
                return false;
            }
            // 把跳过的加回到queue里去
            while (!newCardsSt.isEmpty()) {
                cardsQueue.addFirst(newCardsSt.pop()); 
            }
        }
        return true;
    }
}
// @lc code=end

