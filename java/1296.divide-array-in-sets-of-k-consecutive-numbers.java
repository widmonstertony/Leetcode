import java.util.Map;

/*
 * @lc app=leetcode id=1296 lang=java
 *
 * [1296] Divide Array in Sets of K Consecutive Numbers
 */

// @lc code=start
class Solution {
    public boolean isPossibleDivide(int[] nums, int k) {
        // 先把整个卡组排个序
        Arrays.sort(nums);
        LinkedList<Integer> numsQueue = new LinkedList<>();
        Map<Integer, Integer> numCntMap = new HashMap<>();
        for (int num: nums) {
            if (!numCntMap.containsKey(num)) {
                numsQueue.add(num);
            }
            numCntMap.put(num, numCntMap.getOrDefault(num, 0) + 1);
        }
        while (!numsQueue.isEmpty()) {
            Stack<Integer> newCardsSt = new Stack<>();
            int previousNum = -1;
            int currCnt = 0;
            // 一直拿出可以连续的数字
            while (currCnt < k && !numsQueue.isEmpty()) {
                int currNum = numsQueue.poll();
                if (currCnt == 0 || currNum == previousNum + 1) {
                    previousNum = currNum;
                    currCnt++;
                }
                else {
                    return false;
                }
                numCntMap.put(currNum, numCntMap.get(currNum) - 1);
                // 如果当前数字的次数不为零，说明还没有用完，要加回去
                if (numCntMap.get(currNum) != 0) {
                    newCardsSt.add(currNum);
                }
            }
            if (currCnt != k) {
                return false;
            }
            // 把还没用完的加回到queue里去
            while (!newCardsSt.isEmpty()) {
                numsQueue.addFirst(newCardsSt.pop()); 
            }
        }
        return true;
    }
}
// @lc code=end

