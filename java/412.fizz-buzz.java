import org.graalvm.compiler.virtual.phases.ea.PartialEscapeBlockState.Final;

/*
 * @lc app=leetcode id=412 lang=java
 *
 * [412] Fizz Buzz
 */

// @lc code=start
class Solution {
    public List<String> fizzBuzz(int n) {
        List<String> resList = new ArrayList<>();
        final String THREE_STR = "Fizz", FIVE_STR = "Buzz";
        for (int i = 1; i <= n; i++) {
            StringBuilder currBuilder = new StringBuilder();
            if (i % 3 == 0) {
                currBuilder.append(THREE_STR);
            }
            if (i % 5 == 0) {
                currBuilder.append(FIVE_STR);
            }
            if (i % 3 != 0 && i % 5 != 0) {
                currBuilder.append(i);
            }
            resList.add(currBuilder.toString());
        }
        return resList;
    }
}
// @lc code=end

