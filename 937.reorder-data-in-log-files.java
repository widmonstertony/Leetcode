/*
 * @lc app=leetcode id=937 lang=java
 *
 * [937] Reorder Data in Log Files
 */

// @lc code=start
class Solution {
    public String[] reorderLogFiles(String[] logs) {
        Arrays.sort(logs, (a, b) -> {
            String[] aLetter = a.split(" ");
            String[] bLetter = b.split(" ");
            boolean isADigitLog = aLetter[1].charAt(0) >= '0' && aLetter[1].charAt(0) <= '9';
            boolean isBDigitLog = bLetter[1].charAt(0) >= '0' && bLetter[1].charAt(0) <= '9';
            if (isADigitLog && !isBDigitLog) {
                return 1;
            }
            else if (!isADigitLog && isBDigitLog) {
                return -1;
            }
            else if (!isADigitLog && !isBDigitLog) {
                int aWordIdx = 1, bWordIdx = 1;
                while (true) {
                    if (aWordIdx == aLetter.length && bWordIdx == bLetter.length) {
                        break;
                    }
                    if (aWordIdx == aLetter.length) {
                        return -1;
                    }
                    if (bWordIdx == bLetter.length) {
                        return 1;
                    }
                    int cmp = aLetter[aWordIdx].compareTo(bLetter[bWordIdx]);
                    if (cmp != 0) {
                        return cmp;
                    }
                    aWordIdx++;
                    bWordIdx++;
                }
                return aLetter[0].compareTo(bLetter[0]);
            }
            else {
                return 0;
            }
        });
        return logs;
    }
}
// @lc code=end

