import java.util.HashMap;
import java.util.Map;

/*
 * @lc app=leetcode id=464 lang=java
 *
 * [464] Can I Win
 */

// @lc code=start
class Solution {
    public boolean canIWin(int maxChoosableInteger, int desiredTotal) {
        // 如果能选的最大数大于total，第一次就直接选那个total就能赢
        if (maxChoosableInteger >= desiredTotal) {
            return true;
        }
        // 如果把1到能选的最大数之间的所有数全部加起来，都小于total，说明不可能赢
        if (maxChoosableInteger / 2 < desiredTotal / (maxChoosableInteger + 1)) {
            return false;
        }
        Map<Integer, Boolean> canWinMap = new HashMap<>();
        return canIWinHelper(maxChoosableInteger, desiredTotal, 0, canWinMap);
    }
    private boolean canIWinHelper(int maxChoosableInteger, int desiredTotal, int usedNum, Map<Integer, Boolean> canWinMap) {
        if (canWinMap.containsKey(usedNum)) {
            return canWinMap.get(usedNum);
        }
        // 遍历所有数字，将该数字对应的 mask 算出来
        // 也就是把所有可以选的数字都试一遍
        for (int i = 0; i < maxChoosableInteger; i++) {
            // 使用一个整型数，按位来记录数组中的某个数字是否使用过
            int curr = (1 << i);
            // 如果其和 usedNum 相与为0的话，说明该数字没有使用过
            if ((curr & usedNum) == 0) {
                // 如果此时的目标值小于等于当前数字，说明已经赢了
                // 或者如果对手按照这个状况玩下去不能赢，也代表当前玩家赢了
                if (desiredTotal <= i + 1 || 
                    !canIWinHelper(maxChoosableInteger, desiredTotal - (i + 1), curr | usedNum, canWinMap)) {
                    canWinMap.put(usedNum, true);
                    return true;
                }
            }
        }
        // 遍历完所有数字，标记 false，并返回 false
        canWinMap.put(usedNum, false);
        return false;
    }
}
// @lc code=end

