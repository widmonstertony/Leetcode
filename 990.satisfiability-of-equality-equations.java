import java.util.Arrays;

/*
 * @lc app=leetcode id=990 lang=java
 *
 * [990] Satisfiability of Equality Equations
 */

// @lc code=start
class Solution {
    public boolean equationsPossible(String[] equations) {
        int[] roots = new int[256];
        Arrays.fill(roots, -1);
        // 先把所有等于号的数字union起来
        for(String equation: equations) {
            if (equation.charAt(1) == '=') {
                union(equation.charAt(0), equation.charAt(3), roots);
            }
        }
        // 再检测不等于号，如果有同一个root，说明没法union
        for(String equation: equations) {
            if (equation.charAt(1) == '!') {
                int rootOfA = find(equation.charAt(0) - 'a', roots), rootOfB = find(equation.charAt(3) - 'a', roots);
                if (rootOfA == rootOfB) {
                    return false;
                }
            }
        }
        return true;
    }

    private void union(char a, char b, int[] roots) {
        int rootOfA = find(a - 'a', roots), rootOfB = find(b - 'a', roots);
        if (rootOfA == rootOfB) {
            return;
        }
        int valueOfA = roots[rootOfA], valueOfB = roots[rootOfB];
        if (valueOfA < valueOfB) {
            roots[rootOfA] += valueOfB;
            roots[rootOfB] = rootOfA;
        }
        else {
            roots[rootOfB] += valueOfA;
            roots[rootOfA] = rootOfB;
        }
    }

    private int find(int i, int[] roots) {
        int rootValue = roots[i];
        if (rootValue < 0) {
            return i;
        }
        if (roots[rootValue] >= 0) {
            rootValue = find(rootValue, roots);
            roots[i] = rootValue;
        }
        return rootValue;
    }
}
// @lc code=end

