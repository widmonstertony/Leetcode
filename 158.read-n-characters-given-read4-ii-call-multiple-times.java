/*
 * @lc app=leetcode id=158 lang=java
 *
 * [158] Read N Characters Given Read4 II - Call multiple times
 */

// @lc code=start
/**
 * The read4 API is defined in the parent class Reader4.
 *     int read4(char[] buf4); 
 */

public class Solution extends Reader4 {
    /**
     * @param buf Destination buffer
     * @param n   Number of characters to read
     * @return    The number of actual characters read
     */
    LinkedList<Character> bufLeft = new LinkedList<>();
    public int read(char[] buf, int n) {
        int totalCopied = 0;
        // 先把之前剩下的放进buf
        while (!bufLeft.isEmpty() && totalCopied < n) {
            buf[totalCopied] = bufLeft.pollFirst();
            totalCopied++;
        }
        // 如果还不够，再读取新的
        if (totalCopied < n) {
            char[] buf4 = new char[4];
            int read4Num = read4(buf4);
            while (read4Num > 0 && totalCopied < n) {
                int i = 0;
                for (; i < read4Num && totalCopied < n; i++, totalCopied++) {
                    buf[totalCopied] = buf4[i];
                }
                // 把没读完的不需要的放进bufLeft
                while (i < read4Num) {
                    bufLeft.add(buf4[i]);
                    i++;
                }
                // 如果还需要读更多，继续读取4个字符
                if (totalCopied < n) {
                    read4Num = read4(buf4);
                }
                else {
                    break;
                }
            }
        }
        return totalCopied;
    }
}
// @lc code=end

