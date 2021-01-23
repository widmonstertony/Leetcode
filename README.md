# Leetcode
## this is the repo for me to practice leetcode and store the solutions with my thoughts

题目|问题|解法|考点
-------- | :-----------: | :-----------: | :-----------: 
|1. Two Sum | [找一个数组里和为指定数字的两个数](https://leetcode.com/problems/two-sum/) | [把数字放进HashMap，直接找一个数是否存在于hashmap](https://github.com/widmonstertony/Leetcode/blob/master/1.two-sum.java) | Hash Table
|2. Add Two Numbers |[把代表两个数的两个linkedlist加起来变成一个](https://leetcode.com/problems/add-two-numbers/) |[写一个recursion来分别把两个linkedlist的每个node和carry一次加起来](https://github.com/widmonstertony/Leetcode/blob/master/2.add-two-numbers.java) | recursion, linkedlist
|3. Longest Substring Without Repeating Characters |[找到字符串的最长的没有重复字符的子字符串](https://leetcode.com/problems/longest-substring-without-repeating-characters/)| [一边遍历字符串一边用hahstable记录下每个字符最后出现的位置，同时用一个left指针代表子字符串的最左边，一旦遇到有重复的字符串就更新left并且更新长度的答案](https://github.com/widmonstertony/Leetcode/blob/master/3.longest-substring-without-repeating-characters.java) | Sliding Window, HashTable
|4. Median of Two Sorted Arrays |[两个有序数组的中位数](https://leetcode.com/problems/median-of-two-sorted-arrays/)| [写一个找两个数组中的第K大的数字的function，然后每次通过找哪个数组的第K/2的数更大就能确定第K大的数在哪个数组里，再把另一个数组的start idx加上K/2, 继续recursion运行这个function](https://github.com/widmonstertony/Leetcode/blob/master/4.median-of-two-sorted-arrays.java) | 分治，二分法
|5. Longest Palindromic Substring|[最长回文子字符串](https://leetcode.com/problems/longest-palindromic-substring/)| [dp[i][j]代表从i到j是否为回文串，通过dp[i + 1][j - 1]判断当前i j是否可以组成回文串](https://github.com/widmonstertony/Leetcode/blob/master/5.longest-palindromic-substring.java) | DP
|6. ZigZag Conversion|[把原字符串用写之字的形式转换](https://leetcode.com/problems/zigzag-conversion/)| [用变量来记录当前遍历的方向，到0往下走，到底往上走](https://github.com/widmonstertony/Leetcode/blob/master/6.zig-zag-conversion.java) | 字符串
|7. Reverse Integer|[把一个数字顺序反转](https://leetcode.com/problems/reverse-integer/)| [一直除以10获得余数，再给答案乘以10加上余数，乘以十前确认没有超过最大整数](https://github.com/widmonstertony/Leetcode/blob/master/7.reverse-integer.java) | 数学，overflow处理
|8. String to Integer (atoi)|[判断一个整数是否是回文数](https://leetcode.com/problems/string-to-integer-atoi/)| [先处理空字符，再处理符号，然后一直给base乘以10加上当前字符，乘以十前确认没有超过最大整数，最后base乘以符号](https://github.com/widmonstertony/Leetcode/blob/master/8.string-to-integer-atoi.java) | 数学，overflow处理