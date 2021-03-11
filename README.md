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
|8. String to Integer (atoi)|[字符串转换整数 (atoi)](https://leetcode.com/problems/string-to-integer-atoi/)| [先处理空字符，再处理符号，然后一直给base乘以10加上当前字符，乘以十前确认没有超过最大整数，最后base乘以符号](https://github.com/widmonstertony/Leetcode/blob/master/8.string-to-integer-atoi.java) | 数学，overflow处理
|9. Palindrome Number|[判断一个整数是否是回文数](https://leetcode.com/problems/palindrome-number/)| [一直除以10然后一直给base乘以10加上当前数字除以十的余数，最后确认base是否和一开始的数相等](https://github.com/widmonstertony/Leetcode/blob/master/9.palindrome-number.java) | 数学，overflow处理
|10. Regular Expression Matching|[正则表达式匹配](https://leetcode.com/problems/regular-expression-matching/)| [先处理表达式0和1长度的情况，然后处理第二个字符不是* 的情况，判断首字符是否匹配并从第二个字符开始递归这个函数来得到匹配结果，再来处理第二个字符是* 的情况，循环条件为若s不为空且首字符匹配（包括 p[0] 为点，先调用递归函数尝试匹配s和去掉前两个字符的p，如果不能匹配就要用*去匹配掉s的第一个字母，然后继续循环，最后返回递归函数匹配s和去掉前两个字符的p](https://github.com/widmonstertony/Leetcode/blob/master/10.regular-expression-matching.java) | 有病吧，DP也可以，动态表达式很恐怖
|11. Container With Most Water|[盛最多水的容器](https://leetcode.com/problems/container-with-most-water/)| [双指针从头和尾一直往中间移动，每次移动优先排除高度低的，并且更新答案](https://github.com/widmonstertony/Leetcode/blob/master/11.container-with-most-water.java) | 双指针
|256. Paint House|[粉刷房子的最小花费，要求相邻房子不同颜色，只有三种颜色](https://leetcode.com/problems/paint-house/)| [遍历dp上一个位置找出上一个房子不选当前颜色的最小花费，加上当前颜色的花费](https://github.com/widmonstertony/Leetcode/blob/master/256.paint-house.java) | dp
|265. Paint House II|[粉刷房子的最小花费，要求相邻房子不同颜色](https://leetcode.com/problems/paint-house-ii/)| [更新当前房子的所有颜色的最小花费，需要上一个房子的最小和第二小花费的颜色，如果当前颜色是上一个房子的最小花费颜色，那么之前颜色不能选最小花费颜色，则只能取上一个房子的第二小花费的颜色，不然就只需要取上一个房子的最小花费颜色，加上当前颜色的花费就好](https://github.com/widmonstertony/Leetcode/blob/master/265.paint-house-ii.java) | dp
|269. Alien Dictionary|[外星人字典，根据单词排序找出新的字符表顺序](https://leetcode.com/problems/course-schedule-ii/)| [把每两个字符的先后顺序记录为有向图的边和方向，对于有向图中的每个结点（字符），计算其入度，然后从入度为0的结点开始 BFS 遍历这个有向图，然后将遍历路径保存下来返回即可](https://github.com/widmonstertony/Leetcode/blob/master/269.alien-dictionary.java) | Topological sorting
|270. Closest Binary Search Tree Value|[最接近的二叉搜索树值](https://leetcode.com/problems/closest-binary-search-tree-value/)| [先看当前root值比搜索值大还是小，再根据大小选择左右遍历下去搜索并更新答案](https://github.com/widmonstertony/Leetcode/blob/master/270.closest-binary-search-tree-value.java) | 二分法，二叉搜索树
|271. Encode and Decode Strings|[字符串的编码与解码](https://leetcode.com/problems/encode-and-decode-strings/)| [用257 258 char来做定界符，或者把每个字符串长度也encode进去，长度转换成4位的char](https://github.com/widmonstertony/Leetcode/blob/master/271.encode-and-decode-strings.java) | Mask Bit操作（& 0xff)
|274. H-Index|[求H指数（高引用次数，总共有h篇论文分别被引用了至少h次）](https://leetcode.com/problems/h-index/description/)| [先从小到大排序，如果比当前论文被引用次数多的所有论文数量 大于等于 该论文被引次数，该数就是H指数](https://github.com/widmonstertony/Leetcode/blob/master/274.h-index.java) | 排序，恶心
|275. H-Index II|[给一个排好序的数组，求H指数](https://leetcode.com/problems/h-index/description/)| [用找lower bound的二分法来找274的那个比当前论文被引用次数多的所有论文数量 大于等于 该论文被引次数的那个数](https://github.com/widmonstertony/Leetcode/blob/master/275.h-index-ii.java) | 二分法
|276. Paint Fence|[栅栏涂色，不多于两个相同颜色的栅栏相邻](https://leetcode.com/problems/paint-fence/)| [前面和当前一种颜色，则表示更前一个栅栏颜色和右边两个不同, 当前颜色有k-1个颜色可选（排除更前的那个颜色），更前颜色有dp[i - 2]种方式涂，前面和当前不一样颜色，则当前颜色有k-1种选择，前一个颜色总共有dp[i - 1]种方式涂](https://github.com/widmonstertony/Leetcode/blob/master/276.paint-fence.java) | dp，动态规划
|277. Find the Celebrity|[找到那个大家都认识但他不认识大家的名人](https://leetcode.com/problems/find-the-celebrity/)| [先遍历，对于遍历到的人i，若候选人认识i，则将候选人设为i，完成一遍遍历后，来检测候选人是否真正是名人](https://github.com/widmonstertony/Leetcode/blob/master/277.find-the-celebrity.java) | Graph
|280. Wiggle Sort|[一大一小摆动排序](https://leetcode.com/problems/wiggle-sort/)| [一增一减，如果当前数不符合就和后面的数交换位置即可](https://github.com/widmonstertony/Leetcode/blob/master/280.wiggle-sort.java) | 数组，排序
|281. Zigzag Iterator|[锯齿迭代器](https://leetcode.com/problems/zigzag-iterator/)| [主要是用queue，这样可以兼容不止两个list](https://github.com/widmonstertony/Leetcode/blob/master/281.zigzag-iterator.java) | Queue，List
|285. Inorder Successor in BST|[二叉搜索树的中序后继node](https://leetcode.com/problems/inorder-successor-in-bst/)| [可以实现中序遍历来找，也可以利用bst的性质，如果当前根节点值大于要找的node，说明当前根节点可能是要找的后继node，记录当前节点并往左移，不然不记录往右移](https://github.com/widmonstertony/Leetcode/blob/master/285.inorder-successor-in-bst.java) | Inorder递归和迭代，BST
|286. Walls and Gates|[求每个点到门的最近的曼哈顿距离](https://leetcode.com/problems/walls-and-gates/)| [首先把门的位置都排入queue中，然后开始循环，对于门位置的四个相邻点，判断其是否在矩阵范围内，并且位置值是否大于上一位置的值加1，如果满足这些条件，将当前位置赋为上一位置加1，并将次位置排入 queue 中，这样等 queue 中的元素遍历完了，所有位置的值就被正确地更新了](https://github.com/widmonstertony/Leetcode/blob/master/286.walls-and-gates.java) | BFS
|288. Unique Word Abbreviation|[查看缩写是否只来自这个单词](https://leetcode.com/problems/unique-word-abbreviation/)| [如果在起始的字符串数组里至少有两个单词可以表示某一缩略词，把那个缩略词和空字符映射起来，否则缩略词和唯一代表的字符串映射](https://github.com/widmonstertony/Leetcode/blob/master/288.unique-word-abbreviation.java) | Hash Table
|290. Word Pattern|[单词规律，找到每个字符和字符串的映射，字符串之间有空格](https://leetcode.com/problems/word-pattern/)| [因为有空格，所以直接按照空格把字符串分成list，然后再用map和字符一个一个配对尝试就好了](https://github.com/widmonstertony/Leetcode/blob/master/290.word-pattern.java) | Hash Table
|291. Word Pattern II|[单词规律 II，找到每个字符和字符串的映射，字符串之间没有空格](https://leetcode.com/problems/word-pattern-ii/)| [没有空格就只能每个都试一遍，字符和字符串两个index，每匹配到一个就移动idx然后递归call检查是否能到终点，不然把之前记录的配对方式从map里删掉](https://github.com/widmonstertony/Leetcode/blob/master/291.wordpattern-ii.java) | bakctracking
|296. Best Meeting Point|[所有人最佳的碰头地点，求最小的总移动距离](https://leetcode.com/problems/best-meeting-point/)| [先按从小到大顺序分别拿到所有人的横坐标和纵坐标，然后用最大坐标减去最小坐标，倒数第二个坐标减去第二个坐标，以此类推，再全部加起来](https://github.com/widmonstertony/Leetcode/blob/master/296.best-meeting-point.java) | sorting，math
|319. Bulb Switcher|[第n次每n个更改灯泡的状态，n次后亮的灯泡数量](https://leetcode.com/problems/bulb-switcher/)| [只有平方数有一个相等的因数对，也就少了一次关灯，即所有也只有平方数的灯泡会是点亮的状态](https://github.com/widmonstertony/Leetcode/blob/master/319.bulb-switcher.java) | Math
|346. Moving Average from Data Stream|[数据流中的移动平均值](https://leetcode.com/problems/moving-average-from-data-stream/)| [保留一个当前总和，有新的就减去再除以size就是平均值](https://github.com/widmonstertony/Leetcode/blob/master/346.moving-average-from-data-stream.java) | Queue
|656. Coin Path|[给一个数组A，数组元素的值代表当前位置的cost，-1不可以走这个位置，一个整数B表示能走的最大步数。从1开始每次能走B步以内，到达最末尾位置，使得付出总cost值最小，输出字母顺序排列最小路径](https://leetcode.com/problems/coin-path/)| [dp[i]表示从开头到位置i的最小cost值，从后往前跳，字母大的会被小的覆盖掉，才能得到字母顺序的最小路径，用一个root数组表示下一个位置的坐标](https://github.com/widmonstertony/Leetcode/blob/master/656.coin-path.java) | DP
|997. Find the Town Judge|[找那个谁也不信任但所有人都相信的法官](https://leetcode.com/problems/find-the-town-judge/)| [被人信任加1，信任别人则减1，找到那个加到总人数的人就是法官](https://github.com/widmonstertony/Leetcode/blob/master/997.find-the-town-judge.java) | Graph