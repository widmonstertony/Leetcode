import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/*
 * @lc app=leetcode id=444 lang=java
 *
 * [444] Sequence Reconstruction
 */

// @lc code=start
class Solution {
    // Topological Sort解法
    public boolean sequenceReconstruction(int[] org, List<List<Integer>> seqs) {
        Queue<Integer> intQueue = new LinkedList<>();
        Map<Integer, Integer> indegreeMap = new HashMap<>();
        // 创建一个graph，key是from, 一个key可以有多个可以去的integer
        Map<Integer, List<Integer>> intGraph = new HashMap<>();
        // 遍历所有seq，把每一个edge都创建并且加上degree
        for (List<Integer> seq : seqs) {
            // seq里每相邻的两个数就是一个edge，从前面那个数到后面那个数
            for (int i = 0; i < seq.size(); i++) {
                int fromInt = seq.get(i);
                if (!intGraph.containsKey(fromInt)) {
                    intGraph.put(fromInt, new ArrayList<>());
                }
                if (!indegreeMap.containsKey(fromInt)) {
                    indegreeMap.put(fromInt, 0);
                }
                // 更新两个数的edge数量，并且创建edge更新graph
                if (i < seq.size() - 1) {
                    int toInt = seq.get(i + 1);
                    if (!intGraph.containsKey(toInt)) {
                        intGraph.put(toInt, new ArrayList<>());
                    }
                    if (!indegreeMap.containsKey(toInt)) {
                        indegreeMap.put(toInt, 0);
                    }
                    intGraph.get(fromInt).add(toInt);
                    indegreeMap.put(toInt, indegreeMap.get(toInt) + 1);
                }
            }
        }
        // 注意这里，size是graph的size，因为要确保每个数都有去处
        int[] resArr = new int[intGraph.size()];
        // 把edge数为零的integer都加进queue里
        for (Map.Entry<Integer, Integer> mEntry : indegreeMap.entrySet()) {
            if (mEntry.getValue() == 0) {
                intQueue.add(mEntry.getKey());
                // 一旦当前queue数量大于1，说明一个integer有不止一个解，不符合题意
                if (intQueue.size() > 1) {
                    return false;
                }
            }
        }
        int i = 0;
        while (!intQueue.isEmpty()) {
            int fromInt = intQueue.poll();
            resArr[i] = fromInt;
            i++;
            // 和一般Topo Sort不一样的地方，这里是遍历所有的fromInt edge能去的地方
            // 会比遍历seq来的更快些
            for (int toInt : intGraph.get(fromInt)) {
                // 把每一个能到的toInt的degree都减1
                indegreeMap.put(toInt, indegreeMap.get(toInt) - 1);
                if (indegreeMap.get(toInt) == 0) {
                    intQueue.add(toInt);
                }
            }
            if (intQueue.size() > 1) {
                return false;
            }
        }
        // 最后对比生成的array和org是否一致
        return Arrays.equals(org, resArr);
    }
    // public boolean sequenceReconstruction(int[] org, List<List<Integer>> seqs) {
    //     if (seqs.isEmpty()) {
    //         return false;
    //     }
    //     // 记录org中每个数字对应的位置
    //     int[] numIdx = new int[org.length + 1];
    //     for (int i = 0; i < org.length; i++) {
    //         numIdx[org[i]] = i;
    //     }
    //     // 标记当前数字和其前面一个数字是否和org中的顺序一致
    //     boolean[] hasVerified = new boolean[org.length + 1];
    //     // 还需要验证顺序的数字的个数, n个数字只需要验证n-1对顺序即可
    //     int inValidCnt = org.length - 1;

    //     for (List<Integer> seq : seqs) {
    //         for (int i = 0; i < seq.size(); i++) {
    //             int curr = seq.get(i);
    //             // 判断数字是否越界
    //             if (curr <= 0 || curr > org.length) {
    //                 return false;
    //             }
    //             if (i == 0) {
    //                 continue;
    //             }
    //             // 取出当前数字cur，和其前一位置上的数字pre
    //             int pre = seq.get(i - 1);
    //             // 如果在org中，pre在cur之后，那么直接返回false
    //             if (numIdx[pre] >= numIdx[curr]) {
    //                 return false;
    //             }
    //             // 如果cur的顺序没被验证过，而且pre是在cur的前一个
    //             // 那么标记cur已验证，且inValidCnt自减1
    //             if (!hasVerified[curr] && numIdx[pre] + 1 == numIdx[curr]) {
    //                 hasVerified[curr] = true;
    //                 inValidCnt--;
    //             }
    //         }
    //     }
    //     // 如果cnt为0了，说明所有顺序被成功验证了
    //     return inValidCnt == 0;
    // }
}
// @lc code=end

