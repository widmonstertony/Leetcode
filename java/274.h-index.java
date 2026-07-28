/*
 * @lc app=leetcode id=274 lang=java
 *
 * [274] H-Index
 */

// @lc code=start
class Solution {
    public int hIndex(int[] citations) {
        // 将其发表的所有SCI论文按被引次数从低到高排序
        Arrays.sort(citations);
        // 从后往前查找排序后的列表
        for (int i = citations.length - 1; i >= 0; i--) {
            // 比当前论文被引次数多的所有论文数量
            int largerPaperCnt = citations.length - 1 - i;
            // 如果比当前论文被引用次数多的所有论文数量 大于等于 该论文被引用次数
            // 当前论文之后到尾部的所有论文数量即为H指数
            if (largerPaperCnt >= citations[i]) {
                // 因为这些论文的引用数一定 大于等于 当前论文的被引次数
                // 而且这些论文的数量也 大于等于 当前论文的被引次数
                // 满足题目要求的H篇文章，每篇至少有H次被引用
                return largerPaperCnt;
            }
        }
        return citations.length;
    }
}
// @lc code=end

