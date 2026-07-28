/*
 * @lc app=leetcode id=356 lang=java
 *
 * [356] . Line Reflection
 */

// @lc code=start
class Solution {
    public boolean isReflected(int[][] points) {
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        Map<Integer, Set<Integer>> pointsMap = new HashMap<>();
        // 先把最小X和最大X找出来
        // 并把所有point存进map里
        for (int[] point : points) {
            minX = Math.min(point[0], minX);
            maxX = Math.max(point[0], maxX);
            Set<Integer> currSet = pointsMap.getOrDefault(point[0], new HashSet<>());
            currSet.add(point[1]);
            pointsMap.put(point[0], currSet);
        }
        // 对称线Y = （maxX + minX） / 2
        for (int[] point : points) {
            // 计算出当前X基于Y对称的新X的坐标
            int newX = maxX + minX - point[0];
            if (!pointsMap.containsKey(newX) || !pointsMap.get(newX).contains(point[1])) {
                return false;
            }
        }
        return true;
    }
}
// @lc code=end

