import java.util.Arrays;

/*
 * @lc app=leetcode id=949 lang=java
 *
 * [949] Largest Time for Given Digits
 */

// @lc code=start
class Solution {
    public String largestTimeFromDigits(int[] A) {
        // 非常ugly但是常数时间的算法
        int[] numCnt = new int[10];
        for (int num : A) {
            numCnt[num]++;
        }
        StringBuilder timeStr = new StringBuilder();
        // 找2开头的
        if (numCnt[2] > 0 && (numCnt[0] > 0 || numCnt[1] > 0 || numCnt[2] > 1 || numCnt[3] > 0)) {
            timeStr.append("2");
            numCnt[2]--;
            for (int i = 3; i >= 0; i--) {
                if (numCnt[i] > 0) {
                    timeStr.append(i);
                    numCnt[i]--;
                    break;
                }
            }
        }
        // 找1开头的
        else if (numCnt[1] > 0) {
            timeStr.append("1");
            numCnt[1]--;
            for (int i = 9; i >= 0; i--) {
                if (numCnt[i] > 0) {
                    timeStr.append(i);
                    numCnt[i]--;
                    break;
                }
            }
        }
        // 找0开头的
        else if (numCnt[0] > 0) {
            timeStr.append("0");
            numCnt[0]--;
            for (int i = 9; i >= 0; i--) {
                if (numCnt[i] > 0) {
                    timeStr.append(i);
                    numCnt[i]--;
                    break;
                }
            }
        }
        // 如果找不到，说明没办法成功
        else {
            return "";
        }
        timeStr.append(":");
        // 秒钟时间，必须小于60秒
        for (int i = 5; i >= 0; i--) {
            if (numCnt[i] > 0) {
                timeStr.append(i);
                numCnt[i]--;
                break;
            }
        }
        // 如果找到了，则把最后那个秒钟补上去
        if (timeStr.length() != 3) {
            for (int i = 9; i >= 0; i--) {
                if (numCnt[i] > 0) {
                    timeStr.append(i);
                    numCnt[i]--;
                    break;
                }
            }
            return timeStr.toString();
        }
        // 如果没找到，从头开始找
        else {
            timeStr = new StringBuilder();
            numCnt = new int[10];
            for (int num : A) {
                numCnt[num]++;
            }
            // 这次从1开始再来一遍，把2留给后面
            if (numCnt[1] > 0) {
                timeStr.append("1");
                numCnt[1]--;
                for (int i = 9; i >= 0; i--) {
                    if (numCnt[i] > 0) {
                        timeStr.append(i);
                        numCnt[i]--;
                        break;
                    }
                }
            }
            else if (numCnt[0] > 0) {
                timeStr.append("0");
                numCnt[0]--;
                for (int i = 9; i >= 0; i--) {
                    if (numCnt[i] > 0) {
                        timeStr.append(i);
                        numCnt[i]--;
                        break;
                    }
                }
            }
            else {
                return "";
            }
            timeStr.append(":");
            for (int i = 5; i >= 0; i--) {
                if (numCnt[i] > 0) {
                    timeStr.append(i);
                    numCnt[i]--;
                    break;
                }
            }
            if (timeStr.length() != 3) {
                for (int i = 9; i >= 0; i--) {
                    if (numCnt[i] > 0) {
                        timeStr.append(i);
                        numCnt[i]--;
                        break;
                    }
                }
                return timeStr.toString();
            }
            else {
                return "";
            }
        }
    }
}
// @lc code=end

