/*
 * @lc app=leetcode id=682 lang=java
 *
 * [682] Baseball Game
 */

// @lc code=start
class Solution {
    public int calPoints(String[] ops) {
        List<Integer> scoreList = new ArrayList<>();
        int currSum = 0;
        for (String op : ops) {
            if (op.equals("C")) {
                int currInt = scoreList.get(scoreList.size() - 1);
                currSum -= currInt;
                scoreList.remove(scoreList.size() - 1);
            }
            else {
                int currInt;
                if (op.equals("D")) {
                    currInt = scoreList.get(scoreList.size() - 1) * 2;
                }
                else if (op.equals("+")) {
                    currInt = scoreList.get(scoreList.size() - 1) + scoreList.get(scoreList.size() - 2);
                }
                else {
                    currInt = Integer.valueOf(op);
                }
                scoreList.add(currInt);
                currSum += currInt;
            }
        }
        return currSum;
    }
}
// @lc code=end

