/*
 * @lc app=leetcode id=157 lang=java
 *
 * [157] Read N Characters Given Read4
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
    List<Character> bufList = new LinkedList<>();
    public int read(char[] buf, int n) {
        int totalCopied = 0;
        char[] buf4 = new char[4];
        int read4Copied = read4(buf4);
        // 只要当前还能从file里复制出字符，并且总复制数没有到n，那么就继续复制
        while (read4Copied > 0 && totalCopied < n) {
            // 把还需要的字符复制到buf里去
            for (int i = 0; i < read4Copied && totalCopied < n; i++, totalCopied++) {
                buf[totalCopied] = buf4[i];
            }
            // 如果这时候还需要复制，就读取下一个buf4，继续循环
            if (totalCopied < n) {
                read4Copied = read4(buf4);
            }
            // 否则停止循环
            else {
                break;
            }
        }
        return totalCopied;
    }
}
// @lc code=end

