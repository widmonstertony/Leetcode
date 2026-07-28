/*
 * @lc app=leetcode id=465 lang=java
 *
 * [465] Optimal Account Balancing
 */

// @lc code=start
class Solution {
    public int minTransfers(int[][] transactions) {
        int[] res = new int[]{Integer.MAX_VALUE};
        Map<Integer, Integer> personAccountMap = new HashMap();
        // 把所有人的账户信息放进map
        for (int[] transaction: transactions) {
            int fromPerson = transaction[0], toPerson = transaction[1];
            personAccountMap.put(fromPerson, personAccountMap.getOrDefault(fromPerson, 0) - transaction[2]);
            personAccountMap.put(toPerson, personAccountMap.getOrDefault(toPerson, 0) + transaction[2]);
        }

        // 再把账户为正的放入postiveList，负的放入negativeList
        List<Integer> positiveList = new ArrayList();
        List<Integer> negativeList = new ArrayList();

        for (int person : personAccountMap.keySet()) {
            int currAmount = personAccountMap.get(person);
            if (currAmount < 0) {
                negativeList.add(currAmount);
            }
            if (currAmount > 0) {
                positiveList.add(currAmount);
            }
        }
        // Collections.sort(positiveList);
        // Collections.sort(negativeList);
        // 然后暴力试所有方法
        dfs(positiveList, negativeList, 0, res);
        return res[0];
    }
    private void dfs(List<Integer> positiveList, List<Integer> negativeList, int currCnt, int[] res) {
        if (positiveList.isEmpty() && negativeList.isEmpty()) {
            res[0] = Math.min(res[0], currCnt);
            return;
        }
        if (currCnt >= res[0]) {
            return;
        }
        // 不需要，因为正负一定会抵消为0全部消掉
        // if (positiveList.isEmpty()) {
        //     res[0] = Math.min(res[0], currCnt + negativeList.size());
        //     return;
        // }
        // if (negativeList.isEmpty()) {
        //     res[0] = Math.min(res[0], currCnt + positiveList.size());
        //     return;
        // }
        int positiveAmount = positiveList.get(positiveList.size() - 1);
        for (int i = 0; i < negativeList.size(); i++) {
            int negativeAmount = negativeList.get(i);
            if (positiveAmount + negativeAmount > 0) {
                negativeList.remove(i);
                positiveList.set(positiveList.size() - 1, positiveAmount + negativeAmount);
                dfs(positiveList, negativeList, currCnt + 1, res);
                positiveList.set(positiveList.size() - 1, positiveAmount);
                negativeList.add(i, negativeAmount);
            }
            else if (positiveAmount + negativeAmount < 0) {
                positiveList.remove(positiveList.size() - 1);
                negativeList.set(i, positiveAmount + negativeAmount);
                dfs(positiveList, negativeList, currCnt + 1, res);
                positiveList.add(positiveAmount);
                negativeList.set(i, negativeAmount);
            }
            else {
                positiveList.remove(positiveList.size() - 1);
                negativeList.remove(i);
                dfs(positiveList, negativeList, currCnt + 1, res);
                positiveList.add(positiveAmount);
                negativeList.add(i, negativeAmount);
            }
        }

    }
    
}
// @lc code=end

