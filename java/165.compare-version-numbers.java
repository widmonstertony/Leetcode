/*
 * @lc app=leetcode id=165 lang=java
 *
 * [165] Compare Version Numbers
 */

// @lc code=start
class Solution {
    public int compareVersion(String version1, String version2) {
        // 把string按照.分开来
        String[] ver1List = version1.split("\\.");
        String[] ver2List = version2.split("\\.");
        // 确保version1的长度不大于version2
        if (ver1List.length > ver2List.length) {
            return -compareVersion(version2, version1);
        }
        // 对比每一位，一旦不相等就返回结果
        for (int i = 0; i < ver1List.length; i++) {
            int currVer1 = Integer.valueOf(ver1List[i]);
            int currVer2 = Integer.valueOf(ver2List[i]);
            if (currVer1 == currVer2) {
                continue;
            }
            return currVer1 < currVer2 ? -1 : 1;
        }
        // 长的那段剩下的部分，一旦有一个大于0的数字就说明version2更大
        for (int i = ver1List.length; i < ver2List.length; i++) {
            if (Integer.valueOf(ver2List[i]) > 0) {
                return -1;
            }
        }
        return 0;
    }
}
// @lc code=end

