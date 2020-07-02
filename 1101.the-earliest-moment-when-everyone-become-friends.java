import java.util.Arrays;
import java.util.Comparator;

/*
 * @lc app=leetcode id=1101 lang=java
 *
 * [1101] The Earliest Moment When Everyone Become Friends
 */

// @lc code=start
class Solution {
    public int earliestAcq(int[][] logs, int N) {
        int[] root = new int[N];
        Arrays.fill(root, -1);
        Arrays.sort(logs, new Comparator<int[]>() {
            @Override
            public int compare(int[] a, int[] b) {
                return a[0] - b[0];
            }
        });
        for (int[] log : logs) {
            union(log[1], log[2], root);
            // 如果发现一个人的下面连了所有人，则说明全部都连起来了
            if (root[find(log[1], root)] == -N) {
                return log[0];
            }
        }
        return -1;
    }
    
    private void union(int friendA, int friendB, int[] root) {
        int rootOfA = find(friendA, root), rootOfB = find(friendB, root);
        if (rootOfA == rootOfB) {
            return;
        }
        int rootValueOfA = root[rootOfA], rootValueOfB = root[rootOfB];
        if (rootValueOfA < rootValueOfB) {
            root[rootOfA] += rootValueOfB;
            root[rootOfB] = rootOfA;
        }
        else {
            root[rootOfB] += rootValueOfA;
            root[rootOfA] = rootOfB;
        }
    }
    
    private int find(int x, int[] root) {
        int rootValue = root[x];
        if (rootValue < 0) {
            return x;
        }
        if (root[rootValue] >= 0) {
            rootValue = find(rootValue, root);
            root[x] = rootValue;
        }
        return rootValue;
    }
}
// @lc code=end

