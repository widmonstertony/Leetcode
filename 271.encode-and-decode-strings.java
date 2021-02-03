/*
 * @lc app=leetcode id=271 lang=java
 *
 * [271] Encode and Decode Strings
 */

// @lc code=start
public class Codec {
    // 把字符串的长度变成一个4字节的String
    // 一个字节等于8个比特，相当于变成32位比特的一个int
    private String encodeSizeToStr(String str) {
        char[] charArr = new char[4];
        int strLen = str.length();
        // 从最高位到最低位
        for (int i = 0; i < 4; i++) {
            // 0xff相当于只有最右边的8个bit是1，再用&也就相当于只取最右边8位bit
            charArr[i] = (char) (strLen >> ((3 - i) * 8) & 0xff);
        }
        return new String(charArr);
    }
    // Encodes a list of strings to a single string.
    public String encode(List<String> strs) {
        StringBuilder resBuilder = new StringBuilder();
        for (String str : strs) {
            // 把字符串的长度转化成4字节的String放在string前面
            resBuilder.append(encodeSizeToStr(str));
            resBuilder.append(str);
        }
        return resBuilder.toString();
    }
    private int decodeStrToSize(String str) {
        int res = 0;
        for (char currChar : str.toCharArray()) {
            res = (res << 8) + (int) currChar;
        }
        return res;
    }
    // Decodes a single string to a list of strings.
    public List<String> decode(String s) {
        List<String> resList = new ArrayList<>();
        int currIdx = 0;
        while (currIdx < s.length()) {
            int currStrLen = decodeStrToSize(s.substring(currIdx, currIdx + 4));
            currIdx += 4;
            resList.add(s.substring(currIdx, currIdx + currStrLen));
            currIdx += currStrLen;
        }
        return resList;
    }

    // // Encodes a list of strings to a single string.
    // public String encode(List<String> strs) {
    //     if (strs.isEmpty()) {
    //         // 用258来代表空的列表，用来区分列表里只有空的字符串
    //         return Character.toString((char)258);
    //     }
    //     String delimiterStr = Character.toString((char)257);
    //     StringBuilder resStrBuilder = new StringBuilder();
    //     for (int i = 0; i < strs.size(); i++) {
    //         String currStr = strs.get(i);
    //         resStrBuilder.append(currStr);
    //         if (i == strs.size() - 1) {
    //             break;
    //         }
    //         resStrBuilder.append(delimiterStr);
    //     }
    //     return resStrBuilder.toString();
    // }

    // // Decodes a single string to a list of strings.
    // public List<String> decode(String s) {
    //     if (s.equals(Character.toString((char)258))) {
    //         return new ArrayList<String>();
    //     }
    //     String delimiterStr = Character.toString((char)257);
    //     // 注意这里要用-1，因为默认是0，会抛弃空的字符串，-1则不会
    //     return Arrays.asList(s.split(delimiterStr, -1));
    // }
}

// Your Codec object will be instantiated and called as such:
// Codec codec = new Codec();
// codec.decode(codec.encode(strs));
// @lc code=end

