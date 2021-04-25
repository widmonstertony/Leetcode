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
|14. Longest Common Prefix|[所有字符串的共同最长前缀](https://leetcode.com/problems/longest-common-prefix/)| [一边遍历一边匹配答案](https://github.com/widmonstertony/Leetcode/blob/master/14.longest-common-prefix.java) | 送分
|18. 4Sum|[4个数的总和为sum的所有唯一组合](https://leetcode.com/problems/4sum/)| [先排序数组，把start=0和k传进kSum，kSum里先检测start合法，如果k等于2就直接调用2sum里双指针分别从前和末尾来找sum，否则从start开始，每个数都调用一遍ksum，start为i+1，target变成target-nums[i]，并且k-1，把返回的答案每个list都加上当前数字，并保存到答案然后返回](https://github.com/widmonstertony/Leetcode/blob/master/18.4-sum.java) | 双指针
|31. Next Permutation|[返回当前数组的所有组合排列的下一个](https://leetcode.com/problems/next-permutation/)| [先从后往前找到第一个不是降序的数字，也就是当前数字小于后一位，然后再从后往前找到第一个大于当前数字的数，把这两个数字交换位置，然后再把当前数字后面的所有数字前后颠倒顺序](https://github.com/widmonstertony/Leetcode/blob/master/31.next-permutation.java) | 数组，找规律
|215. Kth Largest Element in an Array|[数组第k大的数](https://leetcode.com/problems/kth-largest-element-in-an-array/)| [quickselect，把比piovt大的数和小的数以piovt为分界线排列好，然后看piovt的坐标看是第几大，然后移动左右坐标](https://github.com/widmonstertony/Leetcode/blob/master/215.kth-largest-element-in-an-array.java) | Quick Select
|218. The Skyline Problem|[找到所有建筑重叠后的天际线](https://leetcode.com/problems/the-skyline-problem/)| [首先把一个建筑的左坐标，右坐标分别和高度pair后放进list，左坐标的高度设置为负，然后按照从小到大list排序，然后创建一个从大到小的heap并把0放进去，遍历list里的每个pair，如果当前坐标的高度为负，说明是左坐标，把正高度放进heap，反之把高度从heap删除，然后对比heap里的当前最高高度和之前的高度是否一样，不一样说明是个拐点，记录当前pair的横坐标和当前最高高度，然后更新之前的高度为当前高度](https://github.com/widmonstertony/Leetcode/blob/master/218.the-skyline-problem.java) | Heap
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
|302. Smallest Rectangle Enclosing Black Pixels|[包含黑像素的最小矩阵](https://leetcode.com/problems/smallest-rectangle-enclosing-black-pixels/)| [与其linear去找每个角的位置，利用题目给的一个黑色坐标，用二分法来寻找每个角的最开始黑色出现的坐标](https://github.com/widmonstertony/Leetcode/blob/master/302.smallest-rectangle-enclosing-black-pixels.java) | 二分查找
|307. Range Sum Query - Mutable|[求一个范围内的数字总和，数组会被修改](https://leetcode.com/problems/range-sum-query-mutable/)| [利用segment tree，创建一个两倍长的数组，后半部分放原数组，前半部分nums[i] = nums[i * 2] + nums[i * 2 + 1]，然后更新时从i + n开始，找i j之间和也是从+ n后开始，一直往中间移直到i == j](https://github.com/widmonstertony/Leetcode/blob/master/307.range-sum-query-mutable.java) | Segment Tree
|315. Count of Smaller Numbers After Self|[计算数组中每个元素右侧小于当前元素的个数](https://leetcode.com/problems/count-of-smaller-numbers-after-self/)| [重建一个有序的list，然后二分法找下界，每个元素都去找到那个第一个不小于当前数的数的位置，那么它前面的数就是都小的，再把当前元素放进list找到的那个位置，来确保list是有序的](https://github.com/widmonstertony/Leetcode/blob/master/315.count-of-smaller-numbers-after-self.java) | Segment Tree/ Binary Search
|316. Remove Duplicate Letters|[删掉字符串里的所有重复字符，并且要确保返回的字符串是最小答案](https://leetcode.com/problems/remove-duplicate-letters/)| [创建一个所有字符的次数表，和一个visited表，遍历每个字符，次数减一并且mark visit，并且用stack保存当前字符作为答案，保存前从stack的尾部开始遍历，把比当前字符大的并且次数不为零的字符从stack里删掉，并且visit标为false确保之后会再加回到stack](https://github.com/widmonstertony/Leetcode/blob/master/316.remove-duplicate-letters.java) | stack，贪心法
|319. Bulb Switcher|[第n次每n个更改灯泡的状态，n次后亮的灯泡数量](https://leetcode.com/problems/bulb-switcher/)| [只有平方数有一个相等的因数对，也就少了一次关灯，即所有也只有平方数的灯泡会是点亮的状态](https://github.com/widmonstertony/Leetcode/blob/master/319.bulb-switcher.java) | Math
|324. Wiggle Sort II|[摆动排序 II 把数组一大一小排列好，相等的不能相邻](https://leetcode.com/problems/wiggle-sort-ii/)| [先用快排找到中位数，因为快排会把大于piovt和小于piovt的分别放在piovt的前后，这时只需要分别从中位数的前面和后面各拿一个数放进新数组就行，记得把和piovt相同的先摆在piovt后面](https://github.com/widmonstertony/Leetcode/blob/master/324.wiggle-sort-ii.java) | Quick Select
|328. Odd Even Linked List|[奇偶链表，把偶数的node提出来接在所有奇数node后面](https://leetcode.com/problems/odd-even-linked-list/)| [奇偶两个指针，先把奇指针连到下一个奇指针，移动奇指针，再把偶指针连到下一个偶指针，移动偶指针，最后再把奇指针和偶指针的头尾相连](https://github.com/widmonstertony/Leetcode/blob/master/328.odd-even-linked-list.java) | Two Pointer LinkedList
|334. Increasing Triplet Subsequence|[找到三个数的递增子序列](https://leetcode.com/problems/increasing-triplet-subsequence/)| [双指针分别代表当前最小的数和位于first之后，大于first并且距离first最近的元素，遍历每个数并且更新这两个指针，一旦发现一个数大于这两个数，则发现了答案](https://github.com/widmonstertony/Leetcode/blob/master/334.increasing-triplet-subsequence.java) | 双指针，dp
|336. Palindrome Pairs|[寻找所有的不同的可以组成回文串的索引对](https://leetcode.com/problems/palindrome-pairs/)| [先反向构建Trie树，把所有字符串的坐标存在最后一个node，并把前缀也是回文串的index全部记录在那个字符的node下，然后对每个字符串进行正向匹配，如果遍历到能形成字符串的node，并且当前字符的后部分也是回文，保存，然后如果当前字符全部匹配上的话，则去遍历当前node下的保存了前缀也是回文串的字符串坐标list，当前字符串和这些也可以匹配](https://github.com/widmonstertony/Leetcode/blob/master/336.palindrome-pairs) | Trie，字典树
|339. Nested List Weight Sum|[嵌套列表权重和](https://leetcode.com/problems/nested-list-weight-sum/)| [dfs写一个函数根据level来计算当前数的和，并把list的迭代再call这个函数，bfs的话可以用queue一层一层的计算总和](https://github.com/widmonstertony/Leetcode/blob/master/339.nested-list-weight-sum.java) | BFS，DFS
|346. Moving Average from Data Stream|[数据流中的移动平均值](https://leetcode.com/problems/moving-average-from-data-stream/)| [保留一个当前总和，有新的就减去再除以size就是平均值](https://github.com/widmonstertony/Leetcode/blob/master/346.moving-average-from-data-stream.java) | Queue
|353. Design Snake Game|[设计贪吃蛇🐍](https://leetcode.com/problems/design-snake-game/)| [用一个queue把snake身体的坐标都存起来，每次移动前，先检查新坐标有没有食物，没有的话就去掉老的尾，然后再看queue里有没有坐标和新的头坐标一样，没有的话再加上新的头，](https://github.com/widmonstertony/Leetcode/blob/master/353.design-snake-game.java) | Queue
|356. Line Reflection|[确认所有点关于某条Y轴平行的直线有镜像](https://leetcode.com/problems/line-reflection/)| [先找到X的最大值和最小值，则Y轴的Y值应该是最大值加上最小值除以二，然后利用Y轴检查每个点有没有关于这条Y轴对称](https://github.com/widmonstertony/Leetcode/blob/master/356.line-reflection.java) | Math
|359. Logger Rate Limiter|[日志速率限制器, 每条信息十秒内只能出现一次](https://leetcode.com/problems/logger-rate-limiter/)| [Hash Table把每条信息上一次发的时间记下来，送分题](https://github.com/widmonstertony/Leetcode/blob/master/359.logger-rate-limiter.java) | Hash Table
|360. Sort Transformed Array|[有序转化数组, 把数组每个数字都apply一个公式并把结果有序地存到新数组](https://leetcode.com/problems/sort-transformed-array/)| [因为公式是一个抛物线方程式，所以根据a的正负，先处理两边的数，对比大小存进去，再处理中间的数](https://github.com/widmonstertony/Leetcode/blob/master/360.sort-transformed-array.java) | Math, Heap
|362. Design Hit Counter|[敲击计数器，返回5分钟内的点击数](https://leetcode.com/problems/design-hit-counter/)| [queue里保存所有timestamp，取出时把前面离现在不止5分钟的poll出来](https://github.com/widmonstertony/Leetcode/blob/master/362.design-hit-counter.java) | Queue
|364. Nested List Weight Sum II|[深度越深，权重越小，返回所有数乘以权重的和](https://leetcode.com/problems/nested-list-weight-sum-ii/)| [unweight把每一个深度的总和累积加进去，然后每遍历完一个深度，把unweight加到总和weight，这样深度越浅的就会被多加几次，返回总和weight](https://github.com/widmonstertony/Leetcode/blob/master/364.nested-list-weight-sum-ii.java) | Queue
|370. Range Addition|[范围内的数都加上或者减去一个数，更新几次后的最终数组结果](https://leetcode.com/problems/range-addition/)| [创建一个记录每个坐标和之前坐标相差多少的数组，每次只需要把范围的开头的数加到那个位置上去，再在范围结束的后一位减去之前加上的数，最后根据这个相邻相差多少的数组生成答案](https://github.com/widmonstertony/Leetcode/blob/master/370.range-addition.java) | 数组
|373. Find K Pairs with Smallest Sums|[找两个从小到大排列好的数组的总和最小的前k对](https://leetcode.com/problems/find-k-pairs-with-smallest-sums/)| [创建一个数组记录每个nums1的数当前配对到nums2的第几位，总共找k次，每次都从nums1的第一个数到末尾，每个数都把nums1的和它配对的nums2的数的总和更新，找到当前遍历的总和最小值，再把nums2配对的那个坐标往后移一位](https://github.com/widmonstertony/Leetcode/blob/master/373.find-k-pairs-with-smallest-sums.java) | Heap，数组
|378. Kth Smallest Element in a Sorted Matrix|[排好序的二维数组里的第k大的数](https://leetcode.com/problems/kth-smallest-element-in-a-sorted-matrix/)| [用二分法，范围从二维数组的左上角和右下角开始，查看中间值是第几大的值，然后移动左右直到中间值为第k大，查看数字是第几大从左下角开始，如果要找的数字比当前坐标的数字等于或者大，则当前坐标i上面的数都比要找的数字少，加入count，再把坐标往右移，否则说明当前坐标的数太大了，当前坐标往上移](https://github.com/widmonstertony/Leetcode/blob/master/378.kth-smallest-element-in-a-sorted-matrix.java) | 二分查找，找上界
|380. Insert Delete GetRandom O(1)|[O(1)时间插入、删除和获取随机元素](https://leetcode.com/problems/insert-delete-getrandom-o1/)| [arraylist把所有数字放进去，hashtable记录下位置，删除时把要删除的元素和arryalist最后一个交换位置再删除，确保O(1)](https://github.com/widmonstertony/Leetcode/blob/master/380.insert-delete-get-random-o-1.java) | hashtable
|381. Insert Delete GetRandom O(1) - Duplicates allowed|[O(1)时间插入、删除和获取随机元素，可重复](https://leetcode.com/problems/insert-delete-getrandom-o1-duplicates-allowed/)| [arraylist把所有数字放进去，hashtable里用set记录下同一元素出现的所有位置，删除时把要删除的元素和arryalist最后一个交换位置再删除，确保O(1)](https://github.com/widmonstertony/Leetcode/blob/master/381.insert-delete-get-random-o-1-duplicates-allowed.java) | hashtable，Set
|394. Decode String|[字符串解码 k[encoded_string]](https://leetcode.com/problems/decode-string/)| [先找到数字，再把括号里的字符串迭代call自己解码，再根据次数加到答案里](https://github.com/widmonstertony/Leetcode/blob/master/394.decode-string.java) | 迭代，stack
|419. Battleships in a Board|[找到board里的所有战舰](https://leetcode.com/problems/battleships-in-a-board/)| [因为战舰只会是一条横着或者竖着，遍历整个board把左边和上边都没有X的X点数记录下来就是战舰数](https://github.com/widmonstertony/Leetcode/blob/master/419.battleships-in-a-board.java) | dfs
|448. Find All Numbers Disappeared in an Array|[找到数组中所有消失的数字](https://leetcode.com/problems/find-all-numbers-disappeared-in-an-array/)| [first pass先把所有不正确的位置和正确位置的数进行交换，直到不能交换为止，然后second pass再把无法交换的数字](https://github.com/widmonstertony/Leetcode/blob/master/394.decode-string.java) | 数组
|490. The Maze|[迷宫](https://leetcode.com/problems/the-maze/)| [dfs尝试每一个方向，每次需要把位置一直移到不能再在那个方向移动后，再dfs下一个方向，用二维数组记录下已经visit过的位置](https://github.com/widmonstertony/Leetcode/blob/master/490.the-maze) | dfs
|491. Increasing Subsequences|[找到所有递增的子序列数组](https://leetcode.com/problems/increasing-subsequences/)| [dfs尝试每一个数，每次用set来记录已经遍历过的相同数字，在循环跳到下一个数字时确认下个是否已经出现过](https://github.com/widmonstertony/Leetcode/blob/master/491.increasing-subsequences) | dfs
|516. Longest Palindromic Subsequence|[最长回文子序列](https://leetcode.com/problems/longest-palindromic-subsequence/)| [dp[i][j]代表从i到j的最长回文子序列长度，如果i的字符等于j的字符，dp[i][j] = dp[i - 1][j + 1] + 2, 否则就等于去掉i或者j后的最大长度](https://github.com/widmonstertony/Leetcode/blob/master/516.longest-palindromic-subsequence.java) | dp
|510. Inorder Successor in BST II|[二叉搜索树的中序后继node, 不给root](https://leetcode.com/problems/inorder-successor-in-bst-ii/)| [如果当前node有右节点，则后继节点一定是右节点下的最左节点，不然一直查看当前node的父结点，看父结点的左子节点是否等于当前节点，不等于的话一直移动当前节点为父结点，找到的话说明这个父结点就是下一个后继节点，因为确保了一定是从当前节点的左边在往上走找到的第一个大于当前节点的节点](https://github.com/widmonstertony/Leetcode/blob/master/510.inorder-successor-in-bst-ii.java) | Inorder递归和迭代，BST
|545. Boundary of Binary Tree|[找到二叉树的所有边界](https://leetcode.com/problems/boundary-of-binary-tree/)| [先从root左边开始一直往左走把不是leave的node都存起来，再从root遍历所有node把所有叶子从左到右存起来，再从root右边开始一直往右走把不是leave的node存到stack里，再从stack里拿出来存到答案里](https://github.com/widmonstertony/Leetcode/blob/master/545.boundary-of-binary-tree.java) | stack，Tree
|656. Coin Path|[给一个数组A，数组元素的值代表当前位置的cost，-1不可以走这个位置，一个整数B表示能走的最大步数。从1开始每次能走B步以内，到达最末尾位置，使得付出总cost值最小，输出字母顺序排列最小路径](https://leetcode.com/problems/coin-path/)| [dp[i]表示从开头到位置i的最小cost值，从后往前跳，字母大的会被小的覆盖掉，才能得到字母顺序的最小路径，用一个root数组表示下一个位置的坐标](https://github.com/widmonstertony/Leetcode/blob/master/656.coin-path.java) | DP
|937. Reorder Data in Log Files|[重新排列日志文件](https://leetcode.com/problems/reorder-data-in-log-files/)| [sort排列，把日志按空格分成两部份，再先对比后面那份，再对比前面的标识符](https://github.com/widmonstertony/Leetcode/blob/master/937.reorder-data-in-log-files.java) | sort
|997. Find the Town Judge|[找那个谁也不信任但所有人都相信的法官](https://leetcode.com/problems/find-the-town-judge/)| [被人信任加1，信任别人则减1，找到那个加到总人数的人就是法官](https://github.com/widmonstertony/Leetcode/blob/master/997.find-the-town-judge.java) | Graph
|1041. Robot Bounded In Circle|[机器人是否会回到原地](https://leetcode.com/problems/robot-bounded-in-circle/)| [检查运行完一次命令后的坐标是否为原点坐标，或者朝向是否是北，如果当前方向和初始方向不一致，说明每次执行完一遍指令，机器人都会运动 长度一样，但方向和初始方向角度相差90或者180的向量，多运行几次，向量就会全部被抵消而归零](https://github.com/widmonstertony/Leetcode/blob/master/1041.robot-bounded-in-circle.java) | Math
|1047. Remove All Adjacent Duplicates In String|[删除字符串中的所有相邻重复项](https://leetcode.com/problems/remove-all-adjacent-duplicates-in-string/)| [把答案用一个stack保存，遍历字符串，每个当前字符确认是否和stack的top一样，如果一样就把top移除，否则把当前字符加入到stack的顶端，最后返回stack的结果](https://github.com/widmonstertony/Leetcode/blob/master/1047.remove-all-adjacent-duplicates-in-string.java) | Math