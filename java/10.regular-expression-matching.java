/*
 * @lc app=leetcode id=10 lang=java
 *
 * [10] Regular Expression Matching
 */

// @lc code=start
class Solution {
    public boolean isMatch(String s, String p) {
        if (p.isEmpty()) {
            return s.isEmpty();
        }
        if (p.length() == 1) {
            return s.length() == 1 && (s.charAt(0) == p.charAt(0) || p.charAt(0) == '.');
        }
        //  若p的第二个字符不为*
        if (p.charAt(1) != '*') {
            // 若此时s为空返回 false
            if(s.isEmpty()) {
                return false;
            }
            // 否则判断首字符是否匹配，且从各自的第二个字符开始调用递归函数匹配。
            else {
                return (s.charAt(0) == p.charAt(0) || p.charAt(0) == '.' )
                        && isMatch(s.substring(1), p.substring(1));
            }
        }
        // 若p的第二个字符为*，进行下列循环
        // 条件是若s不为空且首字符匹配（包括 p[0] 为点）
        // 调用递归函数匹配s和去掉前两个字符的p
        while (!s.isEmpty() && (s.charAt(0) == p.charAt(0) || p.charAt(0) == '.')) {
            // 这样做的原因是假设此时的星号的作用是让前面的字符出现0次，验证是否匹配
            if (isMatch(s, p.substring(2))) {
                return true;
            }
            // 如果不能匹配，那么就要用*去匹配s的第一个字母
            // 因为此时首字母匹配了，我们可以去掉s的首字母
            // 而p由于星号的作用，可以有任意个首字母，所以不需要去掉
            else {
                s = s.substring(1);
            }
        }
        // 返回调用递归函数匹配s和去掉前两个字符的p的结果
        //（这么做的原因是处理星号无法匹配的内容
        // 比如 s="ab", p="a*b"，直接进入 while 循环后
        // 我们发现 "ab" 和 "b" 不匹配，所以s变成 "b"
        // 那么此时跳出循环后，就到最后的 return 来比较 "b" 和 "b" 了
        // 返回 true。再举个例子，比如 s="", p="a*"，由于s为空，
        // 不会进入任何的 if 和 while，只能到最后的 return 来比较了，返回 true，正确）
        return isMatch(s, p.substring(2));
    }
}
// @lc code=end

