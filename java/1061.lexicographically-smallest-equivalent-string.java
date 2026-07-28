import java.util.Arrays;

/*
 * @lc app=leetcode id=1061 lang=java
 *
 * [1061] Lexicographically Smallest Equivalent String

 */

// @lc code=start
class Solution {
    public String smallestEquivalentString(String A, String B, String S) {
        // 初始化root数组都为-1，记录26个字母的root
        int[] root = new int['z' - 'a' + 1];
        Arrays.fill(root, -1);
        for (int i = 0; i < A.length(); i++) {
            union(A.charAt(i) - 'a', B.charAt(i) - 'a', root);
        }
        StringBuilder resBuilder = new StringBuilder();
        for (int i = 0; i < S.length(); i++) {
            resBuilder.append((char)(find(S.charAt(i) - 'a', root) + 'a'));
        }
        return resBuilder.toString();
    }
    private void union(int a, int b, int[] root) {
        int rootOfa = find(a, root), rootOfb = find(b, root);
        // 把数字小的那个作为root，把数字大的root指向数字小的字母
        if (rootOfa == rootOfb) {
            return;
        }
        else if (rootOfa < rootOfb) {
            root[rootOfb] = rootOfa;
        }
        else {
            root[rootOfa] = rootOfb;
        }
        return;
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

