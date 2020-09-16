import java.util.HashMap;
import java.util.Map;

/*
 * @lc app=leetcode id=860 lang=java
 *
 * [860] Lemonade Change
 */

// @lc code=start
class Solution {
    public boolean lemonadeChange(int[] bills) {
        int fiveCnt = 0, tenCnt = 0;
        for (int bill : bills) {
            switch (bill) {
                case 5:
                    fiveCnt++;
                    break;
                case 10:
                    tenCnt++;
                    if (fiveCnt < 1) {
                        return false;
                    }
                    fiveCnt--;
                    break;
                // greedy 能用10找零就用10，把5省下来
                case 20:
                    if (fiveCnt > 0 && tenCnt > 0) {
                        fiveCnt--;
                        tenCnt--;
                    }
                    else if (fiveCnt > 3) {
                            fiveCnt -= 3;
                    }
                    else {
                        return false;
                    }
                    break;
                default:
                    return false;
            }   
        }
        return true;
    }
    // backtracking 尝试所有方法 这题目不需要，所以会TLE
    // 但是如果follow up问到greedy有可能会错过，就需要用这个方法尝试所有的组合
    // public boolean lemonadeChange(int[] bills) {
    //     Map<Integer, Integer> billMap = new HashMap<>();
    //     return helper(0, bills, billMap, bills[0]);
    // }
    // private boolean helper(int i, int[] bills, Map<Integer, Integer> billMap, int currAmount) {
    //     if (currAmount < 0) {
    //         return false;
    //     } 
    //     int originalAmount = currAmount;
    //     if (currAmount == bills[i]) {
    //         if (!billMap.containsKey(currAmount)) {
    //             billMap.put(currAmount, 0);
    //         }
    //         billMap.put(currAmount, billMap.get(currAmount) + 1);
    //         currAmount -= 5;
    //     }
    //     if (currAmount == 0) {
    //         if (i == bills.length - 1) {
    //             return true;
    //         }
    //         if (helper(i + 1, bills, billMap, bills[i + 1]) == true) {
    //             return true;
    //         }     
    //         // 注意这里需要把拿的钱还回去，如果当前方式无法成功的话
    //         if (originalAmount == bills[i]) {
    //             billMap.put(originalAmount, billMap.get(originalAmount) - 1);
    //         }
    //         return false;
    //     } 
    //     // 尝试5和10
    //     if (billMap.containsKey(5) && billMap.get(5) > 0) {
    //         billMap.put(5, billMap.get(5) - 1);
    //         if (helper(i, bills, billMap, currAmount - 5)) {
    //             return true;
    //         }
    //         billMap.put(5, billMap.get(5) + 1);
    //     }
    //     if (billMap.containsKey(10) && billMap.get(10) > 0) {
    //         billMap.put(10, billMap.get(10) - 1);
    //         if (helper(i, bills, billMap, currAmount - 10)) {
    //             return true;
    //         }
    //         billMap.put(10, billMap.get(10) + 1);
    //     }
    //     // 不需要尝试20，因为不会用20来找零
    //     if (originalAmount == bills[i]) {
    //         billMap.put(originalAmount, billMap.get(originalAmount) - 1);
    //     }
    //     return false;
    // }
}
// @lc code=end

