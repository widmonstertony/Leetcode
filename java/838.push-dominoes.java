/*
 * @lc app=leetcode id=838 lang=java
 *
 * [838] Push Dominoes
 */

// @lc code=start
class Solution {
    public String pushDominoes(String dominoes) {
        StringBuilder resBuilder = new StringBuilder();
        dominoes = "L" + dominoes + "R";
        for (int left = 0, right = 1; right < dominoes.length(); right++) {
            char currRightChar = dominoes.charAt(right);
            char currLeftChar = dominoes.charAt(left);
            // 当右指针指向‘点’时，我们就跳过
            if (currRightChar == '.') {
                continue;
            }
            // 目标是左指针指向小区间的左边界，右指针指向右边界
            // 然后用right - left - 1算出中间‘点’的个数，为0表示中间没有点
            int midCnt = right - left - 1;
            if (left > 0) {
                resBuilder.append(currLeftChar);
            }
            // 如果左右指针方向一致，则中间的肯定会向这个方向倒过去
            if (currLeftChar == currRightChar) {
                while (midCnt-- > 0) {
                    resBuilder.append(currLeftChar);
                }
            }
            // 当左边界的骨牌向左推，右边界的骨牌向右推
            // 那么中间的骨牌不会收到力，所以依然保持坚挺
            else if (currLeftChar == 'L' && currRightChar == 'R') {
                while (midCnt-- > 0) {
                    resBuilder.append('.');
                }
            }
            // 当左边界的骨牌向右推，右边界的骨牌向左推时，就要看中间的骨牌个数了
            // 若是偶数，那么对半分，若是奇数，那么最中间的骨牌保持站立
            // 其余的对半分
            else {
                for (int i = midCnt / 2; i > 0; i--) {
                    resBuilder.append('R');
                }
                for (int i = midCnt % 2; i > 0; i--) {
                    resBuilder.append('.');
                }
                for (int i = midCnt / 2; i > 0; i--) {
                    resBuilder.append('L');
                }
            }
            left = right;
        }
        return resBuilder.toString();
    }
}
// @lc code=end

