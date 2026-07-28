import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/*
 * @lc app=leetcode id=128 lang=java
 *
 * [128] Longest Consecutive Sequence
 */

// @lc code=start
class Solution {
    public int longestConsecutive(int[] nums) {
        // 用HashSet把数组变成set
        Set<Integer> numSet = new HashSet<>();
        for (int num : nums) {
            numSet.add(num);
        }
        int maxRes = 0;
        for (int num : nums) {
            // 如果当前数字可以在hashset里被删除
            // 则一直删除它的大一位和小一位的数
            // 直到不能删除了为止
            if (numSet.remove(num)) {
                int pre = num - 1;
                int next = num + 1;
                while (numSet.remove(pre)) {
                    pre--;
                }
                while (numSet.remove(next)) {
                    next++;
                }
                // 记录下这次Consective Sequence的长度，和结果对比
                maxRes = Math.max(maxRes, next - pre - 1);
            }
        }
        return maxRes;
    }
    // Union Find解法
//     public int longestConsecutive(int[] nums) {
//         if (nums.length <= 1) {
//             return nums.length;
//         }
//         // 数字，它的root在nums里对应的坐标
//         Map<Integer, Integer> valueToRootIndexMap = new HashMap<>();
//         // 数字，它自己在nums里对应的坐标
//         Map<Integer, Integer> valueToIndexMap = new HashMap<>();
//         for (int i = 0; i < nums.length; i++) {
//             valueToRootIndexMap.put(nums[i], -1);
//             valueToIndexMap.put(nums[i], i);
//         }
//         int maxRes = 1;
//         for (int i = 0; i < nums.length; i++) {
//             int num = nums[i];
//             // if (valueToRootIndexMap.containsKey(num - 1)) {
//             //     maxRes = Math.max(maxRes, union(num - 1, num, valueToRootIndexMap, nums, valueToIndexMap));
//             // }
//             if (valueToRootIndexMap.containsKey(num + 1)) {
//                 maxRes = Math.max(maxRes, union(num + 1, num, valueToRootIndexMap, nums, valueToIndexMap));
//             }
//         }
//         return maxRes;
//     }
//     private int union(int x, int y, Map<Integer, Integer> valueToRootIndexMap, int[] nums, Map<Integer, Integer> valueToIndexMap) {
//         // 分别拿到当前x，y的值的root值
//         int rootOfX = find(x, valueToRootIndexMap, nums, valueToIndexMap), rootOfY = find(y, valueToRootIndexMap, nums, valueToIndexMap);
//         if (rootOfX == rootOfY) {
//             return 1;
//         }
//         // 拿到root值在rootmap里的数值
//         // 这里应该都是负值
//         // 因为都是root，并且值都代表着root连了多少个
//         int currX = valueToRootIndexMap.get(rootOfX), currY = valueToRootIndexMap.get(rootOfY);
//         // 把连的少的加进连的多的里面
//         if (currX < currY) {
//             valueToRootIndexMap.put(rootOfX, currX + currY);
//             valueToRootIndexMap.put(rootOfY, valueToIndexMap.get(rootOfX));
//         }
//         else {
//             valueToRootIndexMap.put(rootOfY, currX + currY);
//             valueToRootIndexMap.put(rootOfX, valueToIndexMap.get(rootOfY));
//         }
//         // 返回 总共连的值
//         return - (currX + currY);
//     }
//     // 找到i的root值
//     private int find(int i, Map<Integer, Integer> valueToRootIndexMap, int[] nums, Map<Integer, Integer> valueToIndexMap) {
//         // 如果它在rootmap里是负值，代表它就是root了
//         if (valueToRootIndexMap.get(i) < 0) {
//             return i;
//         }
//         // 否则，先拿到当前值的root值
//         int realRoot = nums[valueToRootIndexMap.get(i)];
//         // 如果root值在rootmap里显示它并不是root
//         if (valueToRootIndexMap.get(realRoot) != -1) {
//             // 那么找到真正的它的root值
//             realRoot = find(realRoot, valueToRootIndexMap, nums, valueToIndexMap);
//             // 压缩路线
//             // 把真正的root值的坐标和当前值连起来
//             valueToRootIndexMap.put(i, valueToIndexMap.get(realRoot));
//         }
//         // 把真正的值return出去
//         return realRoot;
//     }
}
// @lc code=end

