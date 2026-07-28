# LeetCode Python 重刷手册

> 根据仓库里过去的 Java 解法、题解笔记和提交历史整理。目标不是背每一道题，而是背少量稳定骨架，看到题目后能迅速归类并写出第一版。

[返回仓库首页](../README.md)

## 目录

- [仓库复盘](#仓库复盘)
- [先选模板：题目触发词](#先选模板题目触发词)
- [四大重点背诵卡](#四大重点背诵卡)
- [Java 到 Python 的肌肉记忆](#java-到-python-的肌肉记忆)
- [核心 Python 模板](#核心-python-模板)
- [进阶模板](#进阶但应该会默写的模板)
- [Python 重刷路线](#python-重刷路线)
- [提交前检查](#提交前检查)
- [旧 Java 题解笔记](#旧-java-题解笔记)

## 怎么使用这份手册

1. 先读「选模板」表，只判断题型，不急着写代码。
2. 不看旧 Java 解答，默写对应 Python 骨架。
3. 做完后只记录三件事：`触发信号 / 核心不变量 / 本次错误`。
4. 第二天重写错题；一周后再重写一次。能在 10 分钟内写出骨架才算掌握。

Python、Java 和 SQL 解答分别放在 `python/`、`java/` 和 `sql/`，可以独立继续维护。本手册负责按思路组织这些不同语言的题解。

## 仓库复盘

整理时仓库中共有：

| 内容 | 数量 |
| --- | ---: |
| Java 解答 | 367 |
| Python 解答 | 5 |
| SQL 解答 | 2 |
| Java 代码量 | 约 20,083 行 |
| 旧 README 题解笔记 | 100 道 |

Java 文件中的关键词粗略统计如下；同一文件可能计入多个分类：

| 习惯或结构 | 涉及文件数 | 说明 |
| --- | ---: | --- |
| `List` / `ArrayList` | 109 | 喜欢显式保存路径、层和结果 |
| `left` + `right` | 84 | 双指针和二分是最稳定的主线之一 |
| `Map` / `HashMap` | 52 | 计数、索引、映射和去重很多 |
| `dp[...]` | 46 | 会先写清状态含义，再推转移 |
| `Queue` / `Deque` | 38 | 层序遍历和图搜索较多 |
| 排序 | 33 | 常用“先排序，再双指针/去重/贪心” |
| DFS | 28 | 树、网格、图和回溯共用递归思路 |
| Stack | 28 | 括号、单调栈、字符串消除 |
| Set | 25 | 去重、访问记录、O(1) 查询 |
| Heap | 17 | Top K、合并、最短路 |
| Union-Find | 13 | 连通性题有专门的一组练习 |

### 当前 Python 起点

现在已有的 Python 文件正好暴露了自然的起步路线：

- `1`、`49`、`128`：哈希、分组和 set。
- `347`：哈希计数接 Heap。
- `29`：还是空骨架，属于位运算专题，可以等核心模板恢复后再做。

所以第一组建议先完成「哈希 → 滑动窗口 → 双指针」，再进入位运算；不用按题号强行推进。

### 我以前写 Java 的规律

- **先定义状态，再推进状态。** 常见变量是 `left/right`、`curr`、`visited`、`resList` 和 `helper`。这套习惯很好，Python 中只需缩短样板代码，不要丢掉不变量。
- **喜欢把复杂部分拆成 helper。** 二分、DFS、回溯、partition 等通常单独写函数；Python 继续用内嵌函数，并用闭包保存 `res`、`path`、`ans`。
- **边界检查放得早。** `null`、空数组、越界和 visited 通常在函数开头处理。Python 也保持“先 base case，再主逻辑”。
- **按专题成组刷题。** 提交历史里，2020 年 8 月集中练 DP 和回溯，9–10 月集中练数据结构、栈和图，之后又连续练双指针、二分、树、并查集、迷宫和设计题。Python 重刷也应该按模板成组，而不是按题号。
- **会保留第二种解法。** 很多 Java 文件同时留有 DFS/BFS、暴力/优化或自己的第一版。Python 重刷时建议保留一句对比，不要长期保留整段失效代码。
- **解法说明偏“指针怎么走”。** 这是优点。再补一句“为什么不会漏答案”，就会从记代码升级成记证明。

### Python 重刷时要刻意改掉的成本

- `HashMap`、`HashSet`、`Queue` 不再手写样板，直接用 `dict/set/deque`。
- 栈直接用 `list.append/pop`；队列一定用 `deque.popleft()`，不要用 `list.pop(0)`。
- 最大堆通常把值取负后放进 `heapq`。
- 计数优先想 `Counter/defaultdict(int)`；分组优先想 `defaultdict(list)`。
- 坐标和复合 key 用 tuple，例如 `(row, col)`、`tuple(counts)`。
- 字符串重复拼接改成 list 收集后 `''.join(parts)`。
- Python 递归深度有限。长链图或大网格优先写迭代 DFS/BFS；树和回溯通常仍可递归。
- 不要使用可变默认参数，例如 `def dfs(path=[])`。

## 先选模板：题目触发词

| 看到的信号 | 第一反应 | 记忆句 |
| --- | --- | --- |
| 查找配对、计数、分组、最近位置 | 哈希表 | **边扫边记，先查后放** |
| 连续子数组/子串，右边加入、左边移出 | 滑动窗口 | **右扩、违规左缩、每轮更新** |
| 有序数组、两数/三数、首尾取舍 | 双指针 | **根据单调性排除一边** |
| 有序；或答案越大越容易满足 | 二分 | **先定真假边界，再找第一个真** |
| 最近更大/更小、柱形图、括号 | 栈/单调栈 | **当前元素负责结算栈顶** |
| 链表删改、倒数第 k 个、区间反转 | dummy + 快慢指针 | **头会变就加 dummy** |
| 树的路径、深度、子树信息 | DFS | **向下拿答案，向上交信息** |
| 最少步数、按层、无权图 | BFS | **第一次到达就是最短层数** |
| 岛屿、连通块、网格扩散 | DFS/BFS | **每个点只进一次容器** |
| 枚举所有方案、排列、组合 | 回溯 | **选择、递归、撤销** |
| 最优值/方案数 + 重叠子问题 | DP | **状态、初值、转移、顺序、答案** |
| Top K、每次取当前最小/最大 | Heap | **堆顶永远是下一位候选** |
| 有依赖先后关系 | 拓扑排序 | **入度归零才入队** |
| 动态合并集合、判断连通 | Union-Find | **找到根，再合并根** |
| 带非负权最短路 | Dijkstra | **小根堆弹出当前最短距离** |

### 一张更短的决策卡

```text
连续区间 -> 滑动窗口 / 前缀和
有序或单调答案 -> 双指针 / 二分
所有方案 -> 回溯
最优值或方案数 -> DP
点与边 -> DFS / BFS / 拓扑 / 并查集 / Dijkstra
局部最近更大更小 -> 单调栈
反复取最值 -> Heap
```

## 四大重点背诵卡

根据以前的代码和现在的记忆，优先级最高的是：**二分边界、Stack、Priority Queue、DP**。先把这四张卡默写熟，再扩展其他专题。

### A. 二分：只背一种边界协议

统一使用左闭右开区间 `[left, right)`：

```text
初始化：left = 0, right = len(nums)
循环：  while left < right
中点：  mid = left + (right - left) // 2
左边不要：left = mid + 1
右边保留：right = mid
结束：  left == right，就是答案位置
```

`lower_bound` 和 `upper_bound` 只有一个符号不同：

```python
def lower_bound(nums: list[int], target: int) -> int:
    """第一个 >= target 的位置。"""
    left, right = 0, len(nums)
    while left < right:
        mid = left + (right - left) // 2
        if nums[mid] < target:
            left = mid + 1
        else:
            right = mid
    return left


def upper_bound(nums: list[int], target: int) -> int:
    """第一个 > target 的位置。"""
    left, right = 0, len(nums)
    while left < right:
        mid = left + (right - left) // 2
        if nums[mid] <= target:  # 唯一变化：< 变成 <=
            left = mid + 1
        else:
            right = mid
    return left
```

边界换算表：

| 想找什么 | 写法 |
| --- | --- |
| 第一个 `>= target` | `lower_bound(nums, target)` / `bisect_left(nums, target)` |
| 第一个 `> target` | `upper_bound(nums, target)` / `bisect_right(nums, target)` |
| 最后一个 `< target` | `lower_bound(nums, target) - 1` |
| 最后一个 `<= target` | `upper_bound(nums, target) - 1` |
| `target` 出现次数 | `upper_bound(nums, target) - lower_bound(nums, target)` |
| `target` 的范围 | `[lower_bound(nums, target), upper_bound(nums, target))` |

背诵句：

```text
lower：小于 target 的扔掉。
upper：小于等于 target 的扔掉。
```

必须记住：

- `right = len(nums)`，不是 `len(nums) - 1`。
- 因为是左闭右开，所以保留右半边时写 `right = mid`。
- 返回值允许等于 `len(nums)`，代表所有元素都比 target 小。
- Python 实战直接用 `bisect_left(nums, target)` 和 `bisect_right(nums, target)`；手写版用于面试解释边界。
- 旋转数组、峰值题虽然也用二分，但不是 lower bound；它们仍然遵守“每轮根据单调性安全排除一半”。

答案二分也统一成同一件事。若答案空间呈现：

```text
False False False True True True
```

就找第一个 `True`：

```python
def first_feasible(left: int, right: int) -> int:
    """在整数半开区间 [left, right) 找第一个 True；不存在则返回 right。"""
    while left < right:
        mid = left + (right - left) // 2
        if feasible(mid):
            right = mid
        else:
            left = mid + 1
    return left
```

写之前先回答：**`feasible(x)` 是否单调？我要第一个 True，还是最后一个 False？**

### B. Stack：栈里放的都是“还没结算的人”

先分清三种容器：

| 容器 | 下一个取谁 | Python |
| --- | --- | --- |
| Stack | 最近放进去的 | `list.append()` / `list.pop()` |
| Queue | 最早放进去的 | `deque.append()` / `deque.popleft()` |
| Priority Queue | 优先级最小的 | `heapq.heappush()` / `heapq.heappop()` |

普通栈处理嵌套和匹配；单调栈处理“最近更大/更小”。需要距离或坐标时，栈里存**下标**，不要只存值。

单调栈四问：

```text
1. 栈里存值还是下标？
2. 栈从底到顶递增还是递减？
3. 当前元素满足什么条件时 pop？
4. 当前元素是在回答自己，还是在结算被 pop 的元素？
```

最常见的两个模板：

| 问题 | 栈的状态 | 当前值触发的 pop 条件 | 谁得到答案 |
| --- | --- | --- | --- |
| 下一个更大 | 单调递减 | `stack_top < curr` | 被 pop 的旧元素 |
| 下一个更小 | 单调递增 | `stack_top > curr` | 被 pop 的旧元素 |

```python
def next_greater(nums: list[int]) -> list[int]:
    ans = [-1] * len(nums)
    stack = []  # 下标；对应值从底到顶单调递减

    for i, num in enumerate(nums):
        while stack and nums[stack[-1]] < num:
            prev = stack.pop()
            ans[prev] = num  # 当前 num 结算 prev
        stack.append(i)

    return ans
```

若题目问“前一个更大/更小”，则先把不合法的栈顶 pop 掉，**剩下的栈顶回答当前元素**。

等号不是固定套路，要看“谁回答谁”：

- 找**下一个严格更大**时，只有 `stack_top < curr` 才 pop；相等值不能互相回答。
- 找**前一个严格更大**时，要先 pop 掉 `stack_top <= curr`，剩下的栈顶才能严格大于当前值。
- 柱形图、重复数字题最容易错在 `<` 和 `<=`；先写清严格/非严格关系，再决定相等值由左边还是右边结算。

### C. Priority Queue：先说清堆顶代表谁

Python 的 `heapq` 默认是小根堆：

```python
heap = [3, 1, 5]
heapq.heapify(heap)

heapq.heappush(heap, 2)
smallest = heap[0]          # 只看，不删除
smallest = heapq.heappop(heap)
```

最大堆用负数：

```python
max_heap = []
for value in nums:
    heapq.heappush(max_heap, -value)
largest = -heapq.heappop(max_heap)
```

Top K 只背这个方向：

| 目标 | 维护什么堆 | 堆顶含义 |
| --- | --- | --- |
| 最大的 K 个 | 大小为 K 的小根堆 | 第 K 大 |
| 最小的 K 个 | 大小为 K 的大根堆 | 第 K 小 |
| 每次取全局最小候选 | 小根堆放所有候选 | 下一位要处理的人 |

```python
# 最大的 k 个：小根堆只留下 k 个大值
heap = []
for num in nums:
    heapq.heappush(heap, num)
    if len(heap) > k:
        heapq.heappop(heap)

kth_largest = heap[0]
```

多个字段按 tuple 从左到右比较：

```python
heapq.heappush(heap, (distance, node))
distance, node = heapq.heappop(heap)
```

必须记住：

- Heap 只保证 `heap[0]` 最小，不保证整个 list 有序。
- 改了优先级后不能原地等它自动调整；通常重新 push 新状态。
- Dijkstra 等场景允许旧状态留在堆里，pop 后用 `if curr_dist != dist[node]: continue` 跳过。
- 两个 tuple 的第一项相等时，Python 会继续比较下一项；若下一项对象不可比较，加入唯一序号作为 tie-breaker。
- 中位数数据流使用两个堆：左边最大堆、右边最小堆，并保持长度差不超过 1。

### D. DP：不背答案，背五个问题

DP 最难的通常不是公式，而是**状态定义和计算顺序**。每题先写：

```text
1. State：dp[...] 精确表示什么？
2. Choice：最后一步有哪些选择？
3. Transition：选择后从哪个旧状态转移？
4. Base：最小问题和不可能状态如何初始化？
5. Order：依赖状态必须先算，答案最后在哪里？
```

常见题型映射：

| 信号 | 常见状态 | 仓库代表题 |
| --- | --- | --- |
| 选或不选、不能相邻 | `dp[i]` / 前两个状态 | 198、213 |
| 网格路径 | `dp[row][col]` | 62、64、120 |
| 金额、容量、目标和 | `dp[amount]` | 322、494、518 |
| 子序列 | `dp[i]` 或 `dp[i][j]` | 300、516 |
| 区间合并/最后戳谁 | `dp[left][right]` | 312、375 |
| 持有/未持有等阶段 | `dp[day][state]` | 121、123、188、309 |

背包的遍历方向：

```text
0/1 背包：每个物品最多一次 -> 容量倒序
完全背包：每个物品可重复   -> 容量正序
```

方案数还要区分：

```text
求组合数：先遍历物品，再遍历容量。
求排列数：先遍历容量，再遍历物品。
```

DP 最常见的四个错误：

- `dp[i]` 的含义在推导中途变了。
- 最小值问题没有把不可能状态初始化成 `inf`，或最大值问题没有用 `-inf`。
- 原地压缩成一维后，遍历方向让本轮的新状态污染了旧状态。
- 只返回 `dp[-1]`，但题目真正答案是 `max(dp)`、`sum(dp)` 或某个状态集合。

压缩空间放在最后：先写对二维或完整数组，确认每个状态只依赖上一层后，再压缩。

## Java 到 Python 的肌肉记忆

| Java | Python |
| --- | --- |
| `HashMap<K, V>` | `{}` / `defaultdict` / `Counter` |
| `HashSet<T>` | `set()` |
| `ArrayList<T>` | `[]` |
| `Queue<T>` / `LinkedList<T>` | `deque()` |
| `Stack<T>` | `list` 的 `append()` / `pop()` |
| `PriorityQueue<T>` | `heapq`，默认小根堆 |
| `Arrays.sort(nums)` | `nums.sort()` |
| `Collections.sort(list, cmp)` | `list.sort(key=...)` |
| `map.getOrDefault(x, 0)` | `map.get(x, 0)` |
| `map.containsKey(x)` | `x in map` |
| `Integer.MAX_VALUE` | `inf` |
| `StringBuilder` | list + `''.join(parts)` |
| `Pair<Integer, Integer>` | `(x, y)` |
| `int[][]` | `list[list[int]]` |

常用 import：

```python
from bisect import bisect_left, bisect_right
from collections import Counter, defaultdict, deque
from functools import cache
from math import inf
import heapq
```

统一命名：

```text
left, right    窗口或左右边界
slow, fast     同向指针或链表
curr           当前节点/当前值
res            最终结果集合
ans            单个最优值或计数
path           当前回溯路径
seen           访问过的点
graph          邻接表
indegree       入度
```

## 核心 Python 模板

模板不要逐字符死背。每个模板只背「不变量 + 三五行骨架」。

### 1. 哈希表：边扫边记

对应旧题：[1. Two Sum](../java/1.two-sum.java)、[49. Group Anagrams](../python/49.group-anagrams.py)、[128. Longest Consecutive Sequence](../java/128.longest-consecutive-sequence.java)。

不变量：处理第 `i` 个数时，`pos` 只保存它前面的信息。先查后放可以避免同一个元素使用两次。

```python
class Solution:
    def twoSum(self, nums: list[int], target: int) -> list[int]:
        pos = {}
        for i, num in enumerate(nums):
            need = target - num
            if need in pos:
                return [pos[need], i]
            pos[num] = i
        return []
```

计数和分组：

```python
count = Counter(nums)

groups = defaultdict(list)
for word in words:
    key = tuple(sorted(word))  # 或 26 位字符计数 tuple
    groups[key].append(word)
```

### 2. 前缀和 + 哈希：连续区间计数

对应旧题：[523. Continuous Subarray Sum](../java/523.continuous-subarray-sum.java)、[525. Contiguous Array](../java/525.contiguous-array.java)、[930. Binary Subarrays With Sum](../java/930.binary-subarrays-with-sum.java)。

不变量：`seen[prefix]` 是当前下标之前，这个前缀和出现的次数。若当前前缀为 `prefix`，目标区间前面的前缀必须是 `prefix - k`。

```python
class Solution:
    def subarraySum(self, nums: list[int], k: int) -> int:
        seen = defaultdict(int)
        seen[0] = 1
        prefix = 0
        ans = 0

        for num in nums:
            prefix += num
            ans += seen[prefix - k]
            seen[prefix] += 1

        return ans
```

记忆点：`seen[0] = 1` 是为了统计“从下标 0 开始”的合法区间。

### 3. 双指针：用单调性排除答案

对应旧题：[11. Container With Most Water](../java/11.container-with-most-water.java)、[15. 3Sum](../java/15.3-sum.java)、[259. 3Sum Smaller](../java/259.3Sum-smaller.java)。

```python
def two_sum_sorted(nums: list[int], target: int) -> list[int]:
    left, right = 0, len(nums) - 1

    while left < right:
        total = nums[left] + nums[right]
        if total == target:
            return [left, right]
        if total < target:
            left += 1
        else:
            right -= 1

    return []
```

三数之和只多两件事：

1. 先排序并固定第一个数。
2. 固定数和左右指针都要去重。

同向指针的记忆方式：`slow` 左边始终是已经处理好的答案，`fast` 负责扫描新元素。

### 4. 滑动窗口：右扩，违规左缩

对应旧题：[3. Longest Substring Without Repeating Characters](../java/3.longest-substring-without-repeating-characters.java)、[239. Sliding Window Maximum](../java/239.sliding-window-maximum.java)、[643. Maximum Average Subarray I](../java/643.maximum-average-subarray-i.java)。

不变量：每次更新答案时，窗口 `[left, right]` 必须合法。

```python
class Solution:
    def lengthOfLongestSubstring(self, s: str) -> int:
        count = defaultdict(int)
        left = 0
        ans = 0

        for right, char in enumerate(s):
            count[char] += 1

            while count[char] > 1:
                count[s[left]] -= 1
                left += 1

            ans = max(ans, right - left + 1)

        return ans
```

固定长度窗口：

```python
window = sum(nums[:k])
ans = window
for right in range(k, len(nums)):
    window += nums[right] - nums[right - k]
    ans = max(ans, window)
```

容易错：问“最长合法”通常在收缩后更新；问“最短满足”通常在 `while` 收缩前后不断更新。

### 5. 二分：统一找第一个满足条件的位置

对应旧题：[33. Search in Rotated Sorted Array](../java/33.search-in-rotated-sorted-array.java)、[35. Search Insert Position](../java/35.search-insert-position.java)、[162. Find Peak Element](../java/162.find-peak-element.java)、[410. Split Array Largest Sum](../java/410.split-array-largest-sum.java)。

数组 lower bound，区间固定用左闭右开 `[left, right)`：

```python
def lower_bound(nums: list[int], target: int) -> int:
    left, right = 0, len(nums)

    while left < right:
        mid = left + (right - left) // 2
        if nums[mid] < target:
            left = mid + 1
        else:
            right = mid

    return left
```

答案二分，找第一个 `feasible(x) == True`：

```python
def first_feasible(left: int, right: int) -> int:
    while left < right:
        mid = left + (right - left) // 2
        if feasible(mid):
            right = mid
        else:
            left = mid + 1
    return left
```

二分前必须先说清三句话：

1. 搜索区间是什么？
2. `feasible(mid)` 的真假分别淘汰哪一半？
3. 循环结束时 `left` 代表什么？

### 6. 栈与单调栈：当前元素结算旧元素

对应旧题：[20. Valid Parentheses](../java/20.valid-parentheses.java)、[42. Trapping Rain Water](../java/42.trapping-rain-water.java)、[496. Next Greater Element I](../java/496.next-greater-element-i.java)、[503. Next Greater Element II](../java/503.next-greater-element-ii.java)。

普通括号栈：

```python
def is_valid(s: str) -> bool:
    pairs = {")": "(", "]": "[", "}": "{"}
    stack = []

    for char in s:
        if char not in pairs:
            stack.append(char)
        elif not stack or stack.pop() != pairs[char]:
            return False

    return not stack
```

下一个更大元素，栈中存“还没找到答案的下标”，并保持对应值单调递减：

```python
def next_greater(nums: list[int]) -> list[int]:
    ans = [-1] * len(nums)
    stack = []

    for i, num in enumerate(nums):
        while stack and nums[stack[-1]] < num:
            prev = stack.pop()
            ans[prev] = num
        stack.append(i)

    return ans
```

### 7. 链表：dummy、快慢指针、反转

对应旧题：[19. Remove Nth Node From End of List](../java/19.remove-nth-node-from-end-of-list.java)、[21. Merge Two Sorted Lists](../java/21.merge-two-sorted-lists.java)、[206. Reverse Linked List](../java/206.reverse-linked-list.java)。

删除倒数第 `n` 个：

```python
class Solution:
    def removeNthFromEnd(self, head, n: int):
        dummy = ListNode(0, head)
        slow = fast = dummy

        for _ in range(n):
            fast = fast.next

        while fast.next:
            slow = slow.next
            fast = fast.next

        slow.next = slow.next.next
        return dummy.next
```

反转链表只背三行更新，先保存 `next_node`：

```python
def reverse_list(head):
    prev, curr = None, head
    while curr:
        next_node = curr.next
        curr.next = prev
        prev, curr = curr, next_node
    return prev
```

### 8. 树：DFS 向上返回，BFS 按层处理

对应旧题：[94. Inorder Traversal](../java/94.binary-tree-inorder-traversal.java)、[102. Level Order](../java/102.binary-tree-level-order-traversal.java)、[104. Maximum Depth](../java/104.maximum-depth-of-binary-tree.java)、[236. LCA](../java/236.lowest-common-ancestor-of-a-binary-tree.java)。

DFS 的核心问题：这个函数要向父节点返回什么？

```python
def max_depth(root) -> int:
    def dfs(node) -> int:
        if not node:
            return 0

        left = dfs(node.left)
        right = dfs(node.right)
        return max(left, right) + 1

    return dfs(root)
```

层序 BFS：

```python
def level_order(root) -> list[list[int]]:
    if not root:
        return []

    res = []
    queue = deque([root])

    while queue:
        level = []
        for _ in range(len(queue)):
            curr = queue.popleft()
            level.append(curr.val)
            if curr.left:
                queue.append(curr.left)
            if curr.right:
                queue.append(curr.right)
        res.append(level)

    return res
```

遍历顺序：

```text
前序：处理 -> 左 -> 右
中序：左 -> 处理 -> 右    # BST 会得到有序序列
后序：左 -> 右 -> 处理    # 适合从子树收集信息
```

### 9. 网格和图搜索：每个点只处理一次

对应旧题：[200. Number of Islands](../java/200.number-of-islands.java)、[286. Walls and Gates](../java/286.walls-and-gates.java)、[490. The Maze](../java/490.the-maze.java)、[547. Number of Provinces](../java/547.number-of-provinces.java)。

迭代 DFS，避免 Python 深递归：

```python
DIRS = ((1, 0), (-1, 0), (0, 1), (0, -1))

def visit(grid: list[list[str]], start_row: int, start_col: int) -> None:
    rows, cols = len(grid), len(grid[0])
    stack = [(start_row, start_col)]
    grid[start_row][start_col] = "0"  # 入栈时立刻标记

    while stack:
        row, col = stack.pop()
        for dr, dc in DIRS:
            nr, nc = row + dr, col + dc
            if 0 <= nr < rows and 0 <= nc < cols and grid[nr][nc] == "1":
                grid[nr][nc] = "0"
                stack.append((nr, nc))
```

无权最短路把 `stack.pop()` 换成 `queue.popleft()`，并保存步数。**入队时标记 visited**，否则同一个点会被重复入队。

普通图优先建邻接表：

```python
graph = defaultdict(list)
for start, end in edges:
    graph[start].append(end)
    graph[end].append(start)
```

### 10. 回溯：选择、递归、撤销

对应旧题：[22. Generate Parentheses](../java/22.generate-parentheses.java)、[39. Combination Sum](../java/39.combination-sum.java)、[46. Permutations](../java/46.permutations.java)、[51. N-Queens](../java/51.n-queens.java)。

组合/子集的通用骨架：

```python
def subsets_with_dup(nums: list[int]) -> list[list[int]]:
    nums.sort()
    res = []
    path = []

    def dfs(start: int) -> None:
        res.append(path.copy())

        for i in range(start, len(nums)):
            if i > start and nums[i] == nums[i - 1]:
                continue

            path.append(nums[i])  # 选择
            dfs(i + 1)            # 递归
            path.pop()            # 撤销

    dfs(0)
    return res
```

只需要根据题意改三处：

- `start`：组合避免回头；排列通常改用 `used`。
- 何时把 `path.copy()` 放进 `res`。
- 剪枝条件，例如 `remain < 0`。

去重口诀：**先排序；同一层相同值只选第一次。**

### 11. DP：五步写法

对应旧题：[198. House Robber](../java/198.house-robber.java)、[300. LIS](../java/300.longest-increasing-subsequence.java)、[312. Burst Balloons](../java/312.burst-balloons.java)、[322. Coin Change](../java/322.coin-change.java)。

每次固定写出：

```text
1. 状态：dp[i] / dp[i][j] 表示什么？
2. 初值：最小问题的答案是什么？
3. 转移：当前状态从哪些旧状态来？
4. 顺序：计算当前状态时，依赖项是否已经算好？
5. 答案：dp[-1]、max(dp) 还是 sum(dp)？
```

线性 DP，打家劫舍：

```python
def rob(nums: list[int]) -> int:
    prev2 = 0  # dp[i - 2]
    prev1 = 0  # dp[i - 1]

    for num in nums:
        curr = max(prev1, prev2 + num)
        prev2, prev1 = prev1, curr

    return prev1
```

背包只背遍历方向：

```python
# 0/1 背包：每个物品最多一次，容量倒序
dp = [0] * (target + 1)
for num in nums:
    for total in range(target, num - 1, -1):
        dp[total] = max(dp[total], dp[total - num] + num)

# 完全背包：每个物品可重复，容量正序
dp = [0] * (target + 1)
dp[0] = 1
for num in nums:
    for total in range(num, target + 1):
        dp[total] += dp[total - num]
```

区间 DP，先枚举短区间再枚举长区间：

```python
dp = [[0] * n for _ in range(n)]
for length in range(2, n + 1):
    for left in range(n - length + 1):
        right = left + length - 1
        for mid in range(left, right):
            dp[left][right] = max(
                dp[left][right],
                dp[left][mid] + dp[mid + 1][right],
            )
```

### 12. Heap 与 Dijkstra：堆顶是下一位候选

对应旧题：[215. Kth Largest](../java/215.kth-largest-element-in-an-array.java)、[295. Median from Data Stream](../java/295.find-median-from-data-stream.java)、[505. The Maze II](../java/505.the-maze-ii.java)、[1167. Connect Sticks](../java/1167.minimum-cost-to-connect-sticks.java)。

维护最大的 `k` 个元素，用大小不超过 `k` 的小根堆：

```python
heap = []
for num in nums:
    heapq.heappush(heap, num)
    if len(heap) > k:
        heapq.heappop(heap)

kth_largest = heap[0]
```

Dijkstra：

```python
def dijkstra(graph, start):
    dist = {start: 0}
    heap = [(0, start)]

    while heap:
        curr_dist, node = heapq.heappop(heap)
        if curr_dist != dist[node]:
            continue

        for nei, weight in graph[node]:
            new_dist = curr_dist + weight
            if new_dist < dist.get(nei, inf):
                dist[nei] = new_dist
                heapq.heappush(heap, (new_dist, nei))

    return dist
```

容易错：Dijkstra 只适用于非负边权；弹出过期距离时要 `continue`。

## 进阶但应该会默写的模板

### 13. 拓扑排序

对应旧题：[207. Course Schedule](../java/207.course-schedule.java)、[210. Course Schedule II](../java/210.course-schedule-ii.java)、[269. Alien Dictionary](../java/269.alien-dictionary.java)。

```python
def topological_sort(num_nodes: int, edges: list[list[int]]) -> list[int]:
    graph = [[] for _ in range(num_nodes)]
    indegree = [0] * num_nodes

    for start, end in edges:
        graph[start].append(end)
        indegree[end] += 1

    queue = deque(i for i in range(num_nodes) if indegree[i] == 0)
    order = []

    while queue:
        node = queue.popleft()
        order.append(node)
        for nei in graph[node]:
            indegree[nei] -= 1
            if indegree[nei] == 0:
                queue.append(nei)

    return order if len(order) == num_nodes else []
```

### 14. Union-Find

对应旧题：[323. Connected Components](../java/323.number-of-connected-components-in-an-undirected-graph.java)、[684. Redundant Connection](../java/684.redundant-connection.java)、[990. Equality Equations](../java/990.satisfiability-of-equality-equations.java)。

```python
class UnionFind:
    def __init__(self, n: int):
        self.parent = list(range(n))
        self.size = [1] * n
        self.count = n

    def find(self, x: int) -> int:
        while x != self.parent[x]:
            self.parent[x] = self.parent[self.parent[x]]
            x = self.parent[x]
        return x

    def union(self, a: int, b: int) -> bool:
        root_a, root_b = self.find(a), self.find(b)
        if root_a == root_b:
            return False

        if self.size[root_a] < self.size[root_b]:
            root_a, root_b = root_b, root_a

        self.parent[root_b] = root_a
        self.size[root_a] += self.size[root_b]
        self.count -= 1
        return True
```

`union()` 返回 `False` 说明两点本来已经连通，常用于找冗余边或判断环。

### 15. 区间合并

```python
def merge_intervals(intervals: list[list[int]]) -> list[list[int]]:
    intervals.sort(key=lambda interval: interval[0])
    merged = []

    for start, end in intervals:
        if not merged or merged[-1][1] < start:
            merged.append([start, end])
        else:
            merged[-1][1] = max(merged[-1][1], end)

    return merged
```

不变量：`merged` 始终互不重叠，最后一个区间是唯一可能与新区间相交的区间。

### 16. Trie

对应旧题：[208. Implement Trie](../java/208.implement-trie-prefix-tree.java)、[211. Add and Search Words](../java/211.design-add-and-search-words-data-structure.java)、[336. Palindrome Pairs](../java/336.palindrome-pairs.java)。

```python
class TrieNode:
    def __init__(self):
        self.children = {}
        self.is_word = False


class Trie:
    def __init__(self):
        self.root = TrieNode()

    def insert(self, word: str) -> None:
        node = self.root
        for char in word:
            node = node.children.setdefault(char, TrieNode())
        node.is_word = True

    def search(self, word: str) -> bool:
        node = self.root
        for char in word:
            if char not in node.children:
                return False
            node = node.children[char]
        return node.is_word
```

## Python 重刷路线

不要一次重刷 367 道。先用 42 道代表题把模板恢复，再补同类变体。

### 针对我的优先复健顺序

| 顺序 | 专题 | 先做这些旧题 | 真正的过关动作 |
| ---: | --- | --- | --- |
| 0 | 热身 | 1、49、128、347 | 熟悉 `dict/set/Counter/heapq` |
| 1 | 二分边界 | 35、34、275、378、410 | 不看 README 默写 lower/upper bound，并说出返回值 |
| 2 | Stack | 20、496、503、84、42 | 每题先写“栈里放谁、何时 pop、谁被结算” |
| 3 | Priority Queue | 215、347、373、1167、295 | 写代码前说出堆顶含义和堆的最大尺寸 |
| 4 | DP | 70、198、322、518、300、312、309 | 先写 State/Choice/Transition/Base/Order，再写代码 |

复习间隔建议：当天做题，第二天不看模板重写，第七天只默写骨架。二分的 lower/upper、单调栈和背包遍历方向应该每次一起默写。

### 第一轮：看到题型就能选模板

| 专题 | 从旧题中重刷 | 过关标准 |
| --- | --- | --- |
| 哈希 | 1、49、128、217 | 能解释为什么先查后放、为什么 set 解法是 O(n) |
| 双指针/窗口 | 3、11、15、19、643 | 能说出窗口或指针的不变量 |
| 二分 | 33、35、162、410 | 全部统一成“第一个满足条件” |
| 栈/队列 | 20、42、102、239、503 | 能解释元素在何时入栈、出栈 |
| 树 | 94、104、110、124、236 | 先说清 DFS 返回值再写代码 |
| 图/网格 | 200、207、323、547、787 | 分清 DFS、BFS、拓扑、并查集、Dijkstra |
| 回溯 | 22、39、46、51 | 能不看代码写出选择/递归/撤销 |
| DP | 70、198、300、312、322、518 | 每题先写 DP 五步 |
| Heap/设计 | 215、295、380、1167 | 能解释堆大小或 O(1) 设计的不变量 |

### 第二轮：每个模板连续做 2–3 个变体

例如：

```text
Two Sum -> 3Sum -> 4Sum
Number of Islands -> Surrounded Regions -> Number of Provinces
House Robber -> House Robber II -> Stock 系列
Combination Sum -> Combination Sum II -> Permutations II
Course Schedule -> Course Schedule II -> Alien Dictionary
Next Greater Element I -> II -> Largest Rectangle
```

这种顺序最符合过去的学习规律：先稳定骨架，再只关注题目之间变化的那一行。

### 第三轮：错题只记一张卡

```text
题号：
触发信号：
选择模板：
核心不变量：
本次错误：
下次看到什么条件会立刻想到它：
```

## 提交前检查

- [ ] 空输入、单元素、全相同、答案不存在是否正确？
- [ ] 下标区间是 `[left, right]` 还是 `[left, right)`？
- [ ] `while` 会不会不移动指针而死循环？
- [ ] visited 是入队时标记，还是出队时才标记？
- [ ] 回溯 append 的是 `path.copy()` 吗？
- [ ] 排序后是否需要去重？去重发生在同一层还是全局？
- [ ] Heap 需要小根还是最大值取负？
- [ ] DP 的遍历顺序能保证依赖状态已经计算？
- [ ] 时间和空间复杂度能否各用一句话说明？
- [ ] Python 是否可能遇到递归深度问题？

---

## 旧 Java 题解笔记

以下内容保留原来的题目、思路和考点记录，作为 Python 重刷时的对照。

题目|问题|解法|考点
-------- | :-----------: | :-----------: | :-----------: 
|1. Two Sum | [找一个数组里和为指定数字的两个数](https://leetcode.com/problems/two-sum/) | [把数字放进HashMap，直接找一个数是否存在于hashmap](../java/1.two-sum.java) | Hash Table
|2. Add Two Numbers |[把代表两个数的两个linkedlist加起来变成一个](https://leetcode.com/problems/add-two-numbers/) |[写一个recursion来分别把两个linkedlist的每个node和carry一次加起来](../java/2.add-two-numbers.java) | recursion, linkedlist
|3. Longest Substring Without Repeating Characters |[找到字符串的最长的没有重复字符的子字符串](https://leetcode.com/problems/longest-substring-without-repeating-characters/)| [一边遍历字符串一边用hahstable记录下每个字符最后出现的位置，同时用一个left指针代表子字符串的最左边，一旦遇到有重复的字符串就更新left并且更新长度的答案](../java/3.longest-substring-without-repeating-characters.java) | Sliding Window, HashTable
|4. Median of Two Sorted Arrays |[两个有序数组的中位数](https://leetcode.com/problems/median-of-two-sorted-arrays/)| [写一个找两个数组中的第K大的数字的function，然后每次通过找哪个数组的第K/2的数更大就能确定第K大的数在哪个数组里，再把另一个数组的start idx加上K/2, 继续recursion运行这个function](../java/4.median-of-two-sorted-arrays.java) | 分治，二分法
|5. Longest Palindromic Substring|[最长回文子字符串](https://leetcode.com/problems/longest-palindromic-substring/)| [dp[i][j]代表从i到j是否为回文串，通过dp[i + 1][j - 1]判断当前i j是否可以组成回文串](../java/5.longest-palindromic-substring.java) | DP
|6. ZigZag Conversion|[把原字符串用写之字的形式转换](https://leetcode.com/problems/zigzag-conversion/)| [用变量来记录当前遍历的方向，到0往下走，到底往上走](../java/6.zig-zag-conversion.java) | 字符串
|7. Reverse Integer|[把一个数字顺序反转](https://leetcode.com/problems/reverse-integer/)| [一直除以10获得余数，再给答案乘以10加上余数，乘以十前确认没有超过最大整数](../java/7.reverse-integer.java) | 数学，overflow处理
|8. String to Integer (atoi)|[字符串转换整数 (atoi)](https://leetcode.com/problems/string-to-integer-atoi/)| [先处理空字符，再处理符号，然后一直给base乘以10加上当前字符，乘以十前确认没有超过最大整数，最后base乘以符号](../java/8.string-to-integer-atoi.java) | 数学，overflow处理
|9. Palindrome Number|[判断一个整数是否是回文数](https://leetcode.com/problems/palindrome-number/)| [一直除以10然后一直给base乘以10加上当前数字除以十的余数，最后确认base是否和一开始的数相等](../java/9.palindrome-number.java) | 数学，overflow处理
|10. Regular Expression Matching|[正则表达式匹配](https://leetcode.com/problems/regular-expression-matching/)| [先处理表达式0和1长度的情况，然后处理第二个字符不是* 的情况，判断首字符是否匹配并从第二个字符开始递归这个函数来得到匹配结果，再来处理第二个字符是* 的情况，循环条件为若s不为空且首字符匹配（包括 p[0] 为点，先调用递归函数尝试匹配s和去掉前两个字符的p，如果不能匹配就要用*去匹配掉s的第一个字母，然后继续循环，最后返回递归函数匹配s和去掉前两个字符的p](../java/10.regular-expression-matching.java) | 有病吧，DP也可以，动态表达式很恐怖
|11. Container With Most Water|[盛最多水的容器](https://leetcode.com/problems/container-with-most-water/)| [双指针从头和尾一直往中间移动，每次移动优先排除高度低的，并且更新答案](../java/11.container-with-most-water.java) | 双指针
|14. Longest Common Prefix|[所有字符串的共同最长前缀](https://leetcode.com/problems/longest-common-prefix/)| [一边遍历一边匹配答案](../java/14.longest-common-prefix.java) | 送分
|15. 3Sum|[找一个数组里和为指定数字的三个数](https://leetcode.com/problems/3sum/)| [先sort，再遍历整个数组，每次选中当前数字，然后对着后面的数组部分用双指针分别从头尾往中间移动，找到加起来等于sum的就保存下来，保存后记得双指针去重，移动到下一个和当前数字不一样的位置](../java/15.3-sum.java) | 双指针
|16. 3Sum Closest|[找到三个加起来最接近target的数](https://leetcode.com/problems/3sum-closest/)| [先sort，再遍历整个数组，每次选中当前数字，然后对着后面的数组部分用双指针分别从头尾往中间移动，找到离target最接近的sum](../java/16.3-sum-closest.java) | 双指针
|18. 4Sum|[4个数的总和为sum的所有唯一组合](https://leetcode.com/problems/4sum/)| [先排序数组，把start=0和k传进kSum，kSum里先检测start合法，如果k等于2就直接调用2sum里双指针分别从前和末尾来找sum，否则从start开始，每个数都调用一遍ksum，start为i+1，target变成target-nums[i]，并且k-1，把返回的答案每个list都加上当前数字，并保存到答案然后返回](../java/18.4-sum.java) | 双指针
|19. Remove Nth Node From End of List|[删除链表的倒数第n个结点](https://leetcode.com/problems/remove-nth-node-from-end-of-list/)| [两个pointer，先让第一个走n步，然后再一起走 直到第一个走到终点，把第二个后面那个删掉就好了](../java/19.remove-nth-node-from-end-of-list.java) | 双指针，linkedlist
|20. Valid Parentheses|[验证括号是否valid](https://leetcode.com/problems/valid-parentheses/)| [用stack保存左括号，然后每个右括号都和stack的顶部匹配，匹配不了就不valid，否则把顶部移除](../java/20.valid-parentheses.java) | Stack
|21. Merge Two Sorted Lists|[把两条排好序的链表按顺序合并成一条](https://leetcode.com/problems/merge-two-sorted-lists/)| [创建一个dummy作为答案链表的头的头，然后看l1和l2哪个数字小就把哪个接到当前node后面，然后移动l1或者l2，再移动当前node到下一个，一直循环，再把剩下的接到尾部就好](../java/21.merge-two-sorted-lists.java) | LinkedList
|22. Generate Parentheses|[给定n组括号，返回括号的所有组合](https://leetcode.com/problems/generate-parentheses/)| [dfs遍历，函数里两个变量记录左括号和右括号的总数，只有当左括号数量大于等于右括号时才可以继续，然后继续dfs左边加一的情况和右边加一的情况](../java/22.generate-parentheses.java) | dfs, backtracking
|31. Next Permutation|[返回当前数组的所有组合排列的下一个](https://leetcode.com/problems/next-permutation/)| [先从后往前找到第一个不是降序的数字，也就是当前数字小于后一位，然后再从后往前找到第一个大于当前数字的数，把这两个数字交换位置，然后再把当前数字后面的所有数字前后颠倒顺序](../java/31.next-permutation.java) | 数组，找规律
|32. Longest Valid Parentheses|[找到字符串里最长的valid括号长度](https://leetcode.com/problems/longest-valid-parentheses/)| [创建一个只放左括号的stack，和一个start来记录当前合法括号的起始位置，当遇到左括号就入栈，遇到右括号，如果stack是空说明没有足够的左括号来匹配了，start跳到下一个坐标，否则就把stack里的左括号pop出来，如果这时是空栈,说明从start到当前都完美匹配到，用当前坐标和start来更新答案，否则只能取stack里剩下的左括号坐标的右边到当前坐标这部分](../java/32.longest-valid-parentheses.java) | stack，dp
|33. Search in Rotated Sorted Array|[在一个中间被旋转过的有序数组里找数](https://leetcode.com/problems/search-in-rotated-sorted-array/)| [二分查找，先判断中间的数是不是比尾部的小，如果是说明后半部分是有序的，否则说明前半部分是有序的，在有序里看target是否大于开头小于尾部，也就是在有序的部分之间](../java/33.search-in-rotated-sorted-array.java) | binary search
|34. Find First and Last Position of Element in Sorted Array|[找一个有序数组中某个数的开始和结束部分](https://leetcode.com/problems/find-first-and-last-position-of-element-in-sorted-array/)| [二分查找，先找到那个数，再用双指针一个往前一个往后延展找到头尾](../java/34.find-first-and-last-position-of-element-in-sorted-array.java) | binary search
|35. Search Insert Position|[找一个有序数组中某数字的位置，如果没找到则返回它该插入的位置](https://leetcode.com/problems/search-insert-position/)| [二分查找找upper bound上界](../java/35.search-insert-position.java) | binary search
|36. Valid Sudoku|[检验一个数独是否是合法的](https://leetcode.com/problems/valid-sudoku/)| [用boolean数组或者set记录出现过的数字，然后检测当前行，列，和3x3小方阵是否出现过该数字](../java/36.valid-sudoku.java) | Set
|37. Sudoku Solver|[解数独](https://leetcode.com/problems/sudoku-solver/)| [一个一个坐标试，每个位置试1-9然后先检查那个位置是否能组成valid数独，然后helper下一个位置看是否会返回true](../java/37.sudoku-solver.java) | backtracking
|39. Combination Sum|[找出无重复数组中所有可以使数字和为target的组合](https://leetcode.com/problems/combination-sum/)| [利用回溯把所有组合都试一遍，需要一个start标注当前的数字的坐标，因为不想重新试当前数之前的数，这样会有重复](../java/39.combination-sum.java) | backtracking
|40. Combination Sum II|[找出数组中所有可以使数字和为target的组合，数组可能有重复数字](https://leetcode.com/problems/combination-sum/)| [利用回溯把所有组合都试一遍，需要先排序，再用一个start标注当前的数字的坐标，因为不想重新试当前数之前的数，这样会有重复，同时在for循环时如果当前数字和之前的一样并且当前数字坐标不是start，就跳过它](../java/40.combination-sum-ii.java) | backtracking
|41. First Missing Positive|[找到数组中第一个缺失的正数](https://leetcode.com/problems/first-missing-positive/)| [类似448，first pass先把所有不正确的位置和正确位置的数进行交换，直到不能交换为止，然后second pass再把第一个数字和位置不匹配的数返回](../java/41.first-missing-positive.java) | 数组
|42. Trapping Rain Water|[给定n个非负整数表示每个宽度为1的柱子的高度图，计算下雨之后能接多少雨水](https://leetcode.com/problems/trapping-rain-water/)| [单调递减的Stack，代表水坑的左边，一旦遇到大于stack最低的，说明就可能可以形成水坑，循环处理只要stack最低点比当前高度低，就拿出来作为水坑的中间的底，因为stack是单调递减的，再把stack里的peek作为水坑的左边（没有的话说明形成不了水坑），当前i则是水坑的右边界，当前水坑的高度则为左右边的更低的高度减去中间的底的高度，宽度则为边界的中间部分，计算面积保存，循环结束处理了所有比当前高度低的后再把当前坐标放进stack](../java/42.trapping-rain-water.java) | Stack，单调递减
|44. Wildcard Matching|[实现一个支持 '?' 和 '*' 的通配符匹配](https://leetcode.com/problems/wildcard-matching/)| [可以用backtracking来暴力尝试，也可以用dp[i][j]代表s的i位前和p的j位前是否能匹配，然后如果当前匹配字符是*号，dp[i][j] = dp[i - 1][j] or dp[i][j - 1]，如果是相同字符或者？，dp[i][j] = dp[i - 1][j - 1]](../java/44.wildcard-matching.java) | backtracking, dp
|45. Jump Game II|[每个元素代表你在该位置可以跳跃的最大长度，使用最少的跳跃次数到达数组的最后一个位置](https://leetcode.com/problems/jump-game-ii/)| [遍历数组，一直更新当前花这一步能跳到的最远距离，如果到达了这个距离，把当前花这一步能跳到的最远距离更新成当前能到的最远距离，然后继续下一步](../java/45.jump-game-ii.java) | Greedy，贪心
|55. Jump Game|[每个元素代表你在该位置可以跳跃的最大长度，判断你是否能够到达最后一个下标](https://leetcode.com/problems/jump-game/)| [记录下当前能跳到的最远距离dp[i]，dp[i] = Math.max(dp[i - 1], nums[i] + i)](../java/55.jump-game.java) | dp
|80. Remove Duplicates from Sorted Array II|[原地删除重复出现的元素，使每个元素最多出现两次，返回删除后数组的新长度](https://leetcode.com/problems/remove-duplicates-from-sorted-array-ii/)| [定义res之前坐标的数一定合法，遍历数组，找到和res - 2不一样的数字放在res的位置，然后res++，最后返回res](../java/80.remove-duplicates-from-sorted-array-ii.java) | pointer
|162. Find Peak Element|[假设 nums[-1] = nums[n] = -∞，找到峰值元素并返回其索引](https://leetcode.com/problems/find-peak-element/)| [只需要找到一个上升部分的最后那个数就是peak，如果中间数小于中间数后面那个，说明我们要找的上升部分最后那个数在右边](../java/162.find-peak-element.java) | binary search
|215. Kth Largest Element in an Array|[数组第k大的数](https://leetcode.com/problems/kth-largest-element-in-an-array/)| [quickselect，把比piovt大的数和小的数以piovt为分界线排列好，然后看piovt的坐标看是第几大，然后移动左右坐标](../java/215.kth-largest-element-in-an-array.java) | Quick Select
|218. The Skyline Problem|[找到所有建筑重叠后的天际线](https://leetcode.com/problems/the-skyline-problem/)| [首先把一个建筑的左坐标，右坐标分别和高度pair后放进list，左坐标的高度设置为负，然后按照从小到大list排序，然后创建一个从大到小的heap并把0放进去，遍历list里的每个pair，如果当前坐标的高度为负，说明是左坐标，把正高度放进heap，反之把高度从heap删除，然后对比heap里的当前最高高度和之前的高度是否一样，不一样说明是个拐点，记录当前pair的横坐标和当前最高高度，然后更新之前的高度为当前高度](../java/218.the-skyline-problem.java) | Heap
|256. Paint House|[粉刷房子的最小花费，要求相邻房子不同颜色，只有三种颜色](https://leetcode.com/problems/paint-house/)| [遍历dp上一个位置找出上一个房子不选当前颜色的最小花费，加上当前颜色的花费](../java/256.paint-house.java) | dp
|265. Paint House II|[粉刷房子的最小花费，要求相邻房子不同颜色](https://leetcode.com/problems/paint-house-ii/)| [更新当前房子的所有颜色的最小花费，需要上一个房子的最小和第二小花费的颜色，如果当前颜色是上一个房子的最小花费颜色，那么之前颜色不能选最小花费颜色，则只能取上一个房子的第二小花费的颜色，不然就只需要取上一个房子的最小花费颜色，加上当前颜色的花费就好](../java/265.paint-house-ii.java) | dp
|269. Alien Dictionary|[外星人字典，根据单词排序找出新的字符表顺序](https://leetcode.com/problems/course-schedule-ii/)| [把每两个字符的先后顺序记录为有向图的边和方向，对于有向图中的每个结点（字符），计算其入度，然后从入度为0的结点开始 BFS 遍历这个有向图，然后将遍历路径保存下来返回即可](../java/269.alien-dictionary.java) | Topological sorting
|270. Closest Binary Search Tree Value|[最接近的二叉搜索树值](https://leetcode.com/problems/closest-binary-search-tree-value/)| [先看当前root值比搜索值大还是小，再根据大小选择左右遍历下去搜索并更新答案](../java/270.closest-binary-search-tree-value.java) | 二分法，二叉搜索树
|271. Encode and Decode Strings|[字符串的编码与解码](https://leetcode.com/problems/encode-and-decode-strings/)| [用257 258 char来做定界符，或者把每个字符串长度也encode进去，长度转换成4位的char](../java/271.encode-and-decode-strings.java) | Mask Bit操作（& 0xff)
|274. H-Index|[求H指数（高引用次数，总共有h篇论文分别被引用了至少h次）](https://leetcode.com/problems/h-index/description/)| [先从小到大排序，如果比当前论文被引用次数多的所有论文数量 大于等于 该论文被引次数，该数就是H指数](../java/274.h-index.java) | 排序，恶心
|275. H-Index II|[给一个排好序的数组，求H指数](https://leetcode.com/problems/h-index/description/)| [用找lower bound的二分法来找274的那个比当前论文被引用次数多的所有论文数量 大于等于 该论文被引次数的那个数](../java/275.h-index-ii.java) | 二分法
|276. Paint Fence|[栅栏涂色，不多于两个相同颜色的栅栏相邻](https://leetcode.com/problems/paint-fence/)| [前面和当前一种颜色，则表示更前一个栅栏颜色和右边两个不同, 当前颜色有k-1个颜色可选（排除更前的那个颜色），更前颜色有dp[i - 2]种方式涂，前面和当前不一样颜色，则当前颜色有k-1种选择，前一个颜色总共有dp[i - 1]种方式涂](../java/276.paint-fence.java) | dp，动态规划
|277. Find the Celebrity|[找到那个大家都认识但他不认识大家的名人](https://leetcode.com/problems/find-the-celebrity/)| [先遍历，对于遍历到的人i，若候选人认识i，则将候选人设为i，完成一遍遍历后，来检测候选人是否真正是名人](../java/277.find-the-celebrity.java) | Graph
|280. Wiggle Sort|[一大一小摆动排序](https://leetcode.com/problems/wiggle-sort/)| [一增一减，如果当前数不符合就和后面的数交换位置即可](../java/280.wiggle-sort.java) | 数组，排序
|281. Zigzag Iterator|[锯齿迭代器](https://leetcode.com/problems/zigzag-iterator/)| [主要是用queue，这样可以兼容不止两个list](../java/281.zigzag-iterator.java) | Queue，List
|285. Inorder Successor in BST|[二叉搜索树的中序后继node](https://leetcode.com/problems/inorder-successor-in-bst/)| [可以实现中序遍历来找，也可以利用bst的性质，如果当前根节点值大于要找的node，说明当前根节点可能是要找的后继node，记录当前节点并往左移，不然不记录往右移](../java/285.inorder-successor-in-bst.java) | Inorder递归和迭代，BST
|286. Walls and Gates|[求每个点到门的最近的曼哈顿距离](https://leetcode.com/problems/walls-and-gates/)| [首先把门的位置都排入queue中，然后开始循环，对于门位置的四个相邻点，判断其是否在矩阵范围内，并且位置值是否大于上一位置的值加1，如果满足这些条件，将当前位置赋为上一位置加1，并将次位置排入 queue 中，这样等 queue 中的元素遍历完了，所有位置的值就被正确地更新了](../java/286.walls-and-gates.java) | BFS
|288. Unique Word Abbreviation|[查看缩写是否只来自这个单词](https://leetcode.com/problems/unique-word-abbreviation/)| [如果在起始的字符串数组里至少有两个单词可以表示某一缩略词，把那个缩略词和空字符映射起来，否则缩略词和唯一代表的字符串映射](../java/288.unique-word-abbreviation.java) | Hash Table
|290. Word Pattern|[单词规律，找到每个字符和字符串的映射，字符串之间有空格](https://leetcode.com/problems/word-pattern/)| [因为有空格，所以直接按照空格把字符串分成list，然后再用map和字符一个一个配对尝试就好了](../java/290.word-pattern.java) | Hash Table
|291. Word Pattern II|[单词规律 II，找到每个字符和字符串的映射，字符串之间没有空格](https://leetcode.com/problems/word-pattern-ii/)| [没有空格就只能每个都试一遍，字符和字符串两个index，每匹配到一个就移动idx然后递归call检查是否能到终点，不然把之前记录的配对方式从map里删掉](../java/291.wordpattern-ii.java) | bakctracking
|296. Best Meeting Point|[所有人最佳的碰头地点，求最小的总移动距离](https://leetcode.com/problems/best-meeting-point/)| [先按从小到大顺序分别拿到所有人的横坐标和纵坐标，然后用最大坐标减去最小坐标，倒数第二个坐标减去第二个坐标，以此类推，再全部加起来](../java/296.best-meeting-point.java) | sorting，math
|302. Smallest Rectangle Enclosing Black Pixels|[包含黑像素的最小矩阵](https://leetcode.com/problems/smallest-rectangle-enclosing-black-pixels/)| [与其linear去找每个角的位置，利用题目给的一个黑色坐标，用二分法来寻找每个角的最开始黑色出现的坐标](../java/302.smallest-rectangle-enclosing-black-pixels.java) | 二分查找
|307. Range Sum Query - Mutable|[求一个范围内的数字总和，数组会被修改](https://leetcode.com/problems/range-sum-query-mutable/)| [利用segment tree，创建一个两倍长的数组，后半部分放原数组，前半部分nums[i] = nums[i * 2] + nums[i * 2 + 1]，然后更新时从i + n开始，找i j之间和也是从+ n后开始，一直往中间移直到i == j](../java/307.range-sum-query-mutable.java) | Segment Tree
|315. Count of Smaller Numbers After Self|[计算数组中每个元素右侧小于当前元素的个数](https://leetcode.com/problems/count-of-smaller-numbers-after-self/)| [重建一个有序的list，然后二分法找下界，每个元素都去找到那个第一个不小于当前数的数的位置，那么它前面的数就是都小的，再把当前元素放进list找到的那个位置，来确保list是有序的](../java/315.count-of-smaller-numbers-after-self.java) | Segment Tree/ Binary Search
|316. Remove Duplicate Letters|[删掉字符串里的所有重复字符，并且要确保返回的字符串是最小答案](https://leetcode.com/problems/remove-duplicate-letters/)| [创建一个所有字符的次数表，和一个visited表，遍历每个字符，次数减一并且mark visit，并且用stack保存当前字符作为答案，保存前从stack的尾部开始遍历，把比当前字符大的并且次数不为零的字符从stack里删掉，并且visit标为false确保之后会再加回到stack](../java/316.remove-duplicate-letters.java) | stack，贪心法
|319. Bulb Switcher|[第n次每n个更改灯泡的状态，n次后亮的灯泡数量](https://leetcode.com/problems/bulb-switcher/)| [只有平方数有一个相等的因数对，也就少了一次关灯，即所有也只有平方数的灯泡会是点亮的状态](../java/319.bulb-switcher.java) | Math
|324. Wiggle Sort II|[摆动排序 II 把数组一大一小排列好，相等的不能相邻](https://leetcode.com/problems/wiggle-sort-ii/)| [先用快排找到中位数，因为快排会把大于piovt和小于piovt的分别放在piovt的前后，这时只需要分别从中位数的前面和后面各拿一个数放进新数组就行，记得把和piovt相同的先摆在piovt后面](../java/324.wiggle-sort-ii.java) | Quick Select
|328. Odd Even Linked List|[奇偶链表，把偶数的node提出来接在所有奇数node后面](https://leetcode.com/problems/odd-even-linked-list/)| [奇偶两个指针，先把奇指针连到下一个奇指针，移动奇指针，再把偶指针连到下一个偶指针，移动偶指针，最后再把奇指针和偶指针的头尾相连](../java/328.odd-even-linked-list.java) | Two Pointer LinkedList
|334. Increasing Triplet Subsequence|[找到三个数的递增子序列](https://leetcode.com/problems/increasing-triplet-subsequence/)| [双指针分别代表当前最小的数和位于first之后，大于first并且距离first最近的元素，遍历每个数并且更新这两个指针，一旦发现一个数大于这两个数，则发现了答案](../java/334.increasing-triplet-subsequence.java) | 双指针，dp
|336. Palindrome Pairs|[寻找所有的不同的可以组成回文串的索引对](https://leetcode.com/problems/palindrome-pairs/)| [先反向构建Trie树，把所有字符串的坐标存在最后一个node，并把前缀也是回文串的index全部记录在那个字符的node下，然后对每个字符串进行正向匹配，如果遍历到能形成字符串的node，并且当前字符的后部分也是回文，保存，然后如果当前字符全部匹配上的话，则去遍历当前node下的保存了前缀也是回文串的字符串坐标list，当前字符串和这些也可以匹配](../java/336.palindrome-pairs.java) | Trie，字典树
|339. Nested List Weight Sum|[嵌套列表权重和](https://leetcode.com/problems/nested-list-weight-sum/)| [dfs写一个函数根据level来计算当前数的和，并把list的迭代再call这个函数，bfs的话可以用queue一层一层的计算总和](../java/339.nested-list-weight-sum.java) | BFS，DFS
|346. Moving Average from Data Stream|[数据流中的移动平均值](https://leetcode.com/problems/moving-average-from-data-stream/)| [保留一个当前总和，有新的就减去再除以size就是平均值](../java/346.moving-average-from-data-stream.java) | Queue
|353. Design Snake Game|[设计贪吃蛇🐍](https://leetcode.com/problems/design-snake-game/)| [用一个queue把snake身体的坐标都存起来，每次移动前，先检查新坐标有没有食物，没有的话就去掉老的尾，然后再看queue里有没有坐标和新的头坐标一样，没有的话再加上新的头，](../java/353.design-snake-game.java) | Queue
|356. Line Reflection|[确认所有点关于某条Y轴平行的直线有镜像](https://leetcode.com/problems/line-reflection/)| [先找到X的最大值和最小值，则Y轴的Y值应该是最大值加上最小值除以二，然后利用Y轴检查每个点有没有关于这条Y轴对称](../java/356.line-reflection.java) | Math
|359. Logger Rate Limiter|[日志速率限制器, 每条信息十秒内只能出现一次](https://leetcode.com/problems/logger-rate-limiter/)| [Hash Table把每条信息上一次发的时间记下来，送分题](../java/359.logger-rate-limiter.java) | Hash Table
|360. Sort Transformed Array|[有序转化数组, 把数组每个数字都apply一个公式并把结果有序地存到新数组](https://leetcode.com/problems/sort-transformed-array/)| [因为公式是一个抛物线方程式，所以根据a的正负，先处理两边的数，对比大小存进去，再处理中间的数](../java/360.sort-transformed-array.java) | Math, Heap
|362. Design Hit Counter|[敲击计数器，返回5分钟内的点击数](https://leetcode.com/problems/design-hit-counter/)| [queue里保存所有timestamp，取出时把前面离现在不止5分钟的poll出来](../java/362.design-hit-counter.java) | Queue
|364. Nested List Weight Sum II|[深度越深，权重越小，返回所有数乘以权重的和](https://leetcode.com/problems/nested-list-weight-sum-ii/)| [unweight把每一个深度的总和累积加进去，然后每遍历完一个深度，把unweight加到总和weight，这样深度越浅的就会被多加几次，返回总和weight](../java/364.nested-list-weight-sum-ii.java) | Queue
|370. Range Addition|[范围内的数都加上或者减去一个数，更新几次后的最终数组结果](https://leetcode.com/problems/range-addition/)| [创建一个记录每个坐标和之前坐标相差多少的数组，每次只需要把范围的开头的数加到那个位置上去，再在范围结束的后一位减去之前加上的数，最后根据这个相邻相差多少的数组生成答案](../java/370.range-addition.java) | 数组
|373. Find K Pairs with Smallest Sums|[找两个从小到大排列好的数组的总和最小的前k对](https://leetcode.com/problems/find-k-pairs-with-smallest-sums/)| [创建一个数组记录每个nums1的数当前配对到nums2的第几位，总共找k次，每次都从nums1的第一个数到末尾，每个数都把nums1的和它配对的nums2的数的总和更新，找到当前遍历的总和最小值，再把nums2配对的那个坐标往后移一位](../java/373.find-k-pairs-with-smallest-sums.java) | Heap，数组
|378. Kth Smallest Element in a Sorted Matrix|[排好序的二维数组里的第k大的数](https://leetcode.com/problems/kth-smallest-element-in-a-sorted-matrix/)| [用二分法，范围从二维数组的左上角和右下角开始，查看中间值是第几大的值，然后移动左右直到中间值为第k大，查看数字是第几大从左下角开始，如果要找的数字比当前坐标的数字等于或者大，则当前坐标i上面的数都比要找的数字少，加入count，再把坐标往右移，否则说明当前坐标的数太大了，当前坐标往上移](../java/378.kth-smallest-element-in-a-sorted-matrix.java) | 二分查找，找上界
|380. Insert Delete GetRandom O(1)|[O(1)时间插入、删除和获取随机元素](https://leetcode.com/problems/insert-delete-getrandom-o1/)| [arraylist把所有数字放进去，hashtable记录下位置，删除时把要删除的元素和arryalist最后一个交换位置再删除，确保O(1)](../java/380.insert-delete-get-random-o-1.java) | hashtable
|381. Insert Delete GetRandom O(1) - Duplicates allowed|[O(1)时间插入、删除和获取随机元素，可重复](https://leetcode.com/problems/insert-delete-getrandom-o1-duplicates-allowed/)| [arraylist把所有数字放进去，hashtable里用set记录下同一元素出现的所有位置，删除时把要删除的元素和arryalist最后一个交换位置再删除，确保O(1)](../java/381.insert-delete-get-random-o-1-duplicates-allowed.java) | hashtable，Set
|394. Decode String|[字符串解码 k[encoded_string]](https://leetcode.com/problems/decode-string/)| [先找到数字，再把括号里的字符串迭代call自己解码，再根据次数加到答案里](../java/394.decode-string.java) | 迭代，stack
|419. Battleships in a Board|[找到board里的所有战舰](https://leetcode.com/problems/battleships-in-a-board/)| [因为战舰只会是一条横着或者竖着，遍历整个board把左边和上边都没有X的X点数记录下来就是战舰数](../java/419.battleships-in-a-board.java) | dfs
|448. Find All Numbers Disappeared in an Array|[找到数组中所有消失的数字](https://leetcode.com/problems/find-all-numbers-disappeared-in-an-array/)| [first pass先把所有不正确的位置和正确位置的数进行交换，直到不能交换为止，然后second pass再把无法交换的数字](../java/394.decode-string.java) | 数组
|454. 4Sum II|[给定四个包含整数的数组列表 A , B , C , D ,计算有多少个元组 (i, j, k, l) ，使得 A[i] + B[j] + C[k] + D[l] = 0](https://leetcode.com/problems/4sum-ii/)| [先把C和D的所有总和放进hashmap，然后计算A和B的和，在hashmap里找负的那个情况](../java/454.4-sum-ii.java) | Hash Table
|490. The Maze|[迷宫是有一个滚动的小球，这样就不是每次只走一步了，而是朝某一个方向一直滚，直到遇到墙或者边缘才停下来，返回是否能到达终点](https://leetcode.com/problems/the-maze/)| [dfs尝试每一个方向，每次需要把位置一直移到不能再在那个方向移动后，再dfs下一个方向，用二维数组记录下已经visit过的位置](../java/490.the-maze.java) | dfs
|491. Increasing Subsequences|[找到所有递增的子序列数组](https://leetcode.com/problems/increasing-subsequences/)| [dfs尝试每一个数，每次用set来记录已经遍历过的相同数字，在循环跳到下一个数字时确认下个是否已经出现过](../java/491.increasing-subsequences.java) | dfs
|505. The Maze II|[迷宫是有一个滚动的小球，这样就不是每次只走一步了，而是朝某一个方向一直滚，直到遇到墙或者边缘才停下来，返回能到达终点的最短路径的长度](https://leetcode.com/problems/the-maze-ii/)| [Dijkstra 算法，把下一个方向到达的坐标和累积已经花的路径长度放进priority queue，每次取出累积路径最短的，标记为visited然后往四个方向滑，第一个走到终点的就是最短的路径](../java/505.the-maze-ii.java) | Dijkstra， bfs
|516. Longest Palindromic Subsequence|[最长回文子序列](https://leetcode.com/problems/longest-palindromic-subsequence/)| [dp[i][j]代表从i到j的最长回文子序列长度，如果i的字符等于j的字符，dp[i][j] = dp[i - 1][j + 1] + 2, 否则就等于去掉i或者j后的最大长度](../java/516.longest-palindromic-subsequence.java) | dp
|510. Inorder Successor in BST II|[二叉搜索树的中序后继node, 不给root](https://leetcode.com/problems/inorder-successor-in-bst-ii/)| [如果当前node有右节点，则后继节点一定是右节点下的最左节点，不然一直查看当前node的父结点，看父结点的左子节点是否等于当前节点，不等于的话一直移动当前节点为父结点，找到的话说明这个父结点就是下一个后继节点，因为确保了一定是从当前节点的左边在往上走找到的第一个大于当前节点的节点](../java/510.inorder-successor-in-bst-ii.java) | Inorder递归和迭代，BST
|545. Boundary of Binary Tree|[找到二叉树的所有边界](https://leetcode.com/problems/boundary-of-binary-tree/)| [先从root左边开始一直往左走把不是leave的node都存起来，再从root遍历所有node把所有叶子从左到右存起来，再从root右边开始一直往右走把不是leave的node存到stack里，再从stack里拿出来存到答案里](../java/545.boundary-of-binary-tree.java) | stack，Tree
|656. Coin Path|[给一个数组A，数组元素的值代表当前位置的cost，-1不可以走这个位置，一个整数B表示能走的最大步数。从1开始每次能走B步以内，到达最末尾位置，使得付出总cost值最小，输出字母顺序排列最小路径](https://leetcode.com/problems/coin-path/)| [dp[i]表示从开头到位置i的最小cost值，从后往前跳，字母大的会被小的覆盖掉，才能得到字母顺序的最小路径，用一个root数组表示下一个位置的坐标](../java/656.coin-path.java) | DP
|690. Employee Importance|[员工的重要性](https://leetcode.com/problems/employee-importance/)| [先创建一个id和employee的hashTable，再dfs从一开始id开始，一直遍历子员工把重要性加上](../java/690.employee-importance.java) | hash Table
|742. Closest Leaf in a Binary Tree|[找到离给定k值的node最近的叶节点](https://leetcode.com/problems/closest-leaf-in-a-binary-tree/)| [从dfs来用hashtable给所有node都分别创建一个它的list，list包含的是下一个可以到达的node，这样就能从子节点返回到父结点，然后再从k节点开始bfs找到第一个叶节点](../java/742.closest-leaf-in-a-binary-tree.java) | dfs，bfs，tree
|843. Guess the Word|[有10次机会来猜出这个单词](https://leetcode.com/problems/guess-the-word/)| [先shuffle一下数组，然后一直拿一个单词出来call guess返回的字符匹配数量，然后把list里和当前单词相同字符数不等于那个数字的都删掉，因为当前单词和正确答案的字符匹配数量就是那个，不等于的话说明不是正确答案](../java/843.guess-the-word.java) | Minmax
|937. Reorder Data in Log Files|[重新排列日志文件](https://leetcode.com/problems/reorder-data-in-log-files/)| [sort排列，把日志按空格分成两部份，再先对比后面那份，再对比前面的标识符](../java/937.reorder-data-in-log-files.java) | sort
|973. K Closest Points to Origin|[最接近原点的K个点](https://leetcode.com/problems/k-closest-points-to-origin/)| [用minHeap把所有点放进去，再取出来前k个](../java/973.k-closest-points-to-origin.java) | sort，heap
|997. Find the Town Judge|[找那个谁也不信任但所有人都相信的法官](https://leetcode.com/problems/find-the-town-judge/)| [被人信任加1，信任别人则减1，找到那个加到总人数的人就是法官](../java/997.find-the-town-judge.java) | Graph
|1041. Robot Bounded In Circle|[机器人是否会回到原地](https://leetcode.com/problems/robot-bounded-in-circle/)| [检查运行完一次命令后的坐标是否为原点坐标，或者朝向是否是北，如果当前方向和初始方向不一致，说明每次执行完一遍指令，机器人都会运动 长度一样，但方向和初始方向角度相差90或者180的向量，多运行几次，向量就会全部被抵消而归零](../java/1041.robot-bounded-in-circle.java) | Math
|1003. Check If Word Is Valid After Substitutions|[查看一个字符能否由abc插入abc这种组合形成](https://leetcode.com/problems/check-if-word-is-valid-after-substitutions/)| [反向思维，把字符串里的abc一个一个消掉，解法同1047很像，用一个stack来记录之前遍历过的字符，遍历到c的时候就去stack里拿出top两个消掉，如果这两个不是a和b，说明没办法反向形成这种组合](../java/1003.check-if-word-is-valid-after-substitutions.java) | Stack
|1047. Remove All Adjacent Duplicates In String|[删除字符串中的所有相邻重复项](https://leetcode.com/problems/remove-all-adjacent-duplicates-in-string/)| [把答案用一个stack保存，遍历字符串，每个当前字符确认是否和stack的top一样，如果一样就把top移除，否则把当前字符加入到stack的顶端，最后返回stack的结果](../java/1047.remove-all-adjacent-duplicates-in-string.java) | Stack
|1167. Minimum Cost to Connect Sticks|[每连接两个木棍，花费为两个木棍数字相加，把所有木棍连接起来的最低成本](https://leetcode.com/problems/minimum-cost-to-connect-sticks/)| [把所有木棍放入从小到大的heap，然后每次拿出最小的两个合并，再把合并的数放进heap](../java/1167.minimum-cost-to-connect-sticks.java) | Greedy，Heap
|1306. Jump Game III|[每个元素代表你在该位置可以跳跃的最大长度，判断你是否能够到达最后一个下标](https://leetcode.com/problems/jump-game-iii/)| [给定起始位置，可以跳到 i + arr[i] 或者 i - arr[i]，判断自己是否能够跳到对应元素值为0的地方](../java/1306.jump-game-iii.java) | dfs, bfs
|1423. Maximum Points You Can Obtain from Cards|[只能从头或者尾拿卡，一共拿k次，求能拿的最大值](https://leetcode.com/problems/maximum-points-you-can-obtain-from-cards/)| [建立两个数组fontSum[i]和backSum[i]分别代表从前面和后面拿i张的和，然后从0到k的组合都试一遍加起来找最大值](../java/1423.maximum-points-you-can-obtain-from-cards.java) | dp
|1631. Path With Minimum Effort|[耗费的体力值是相邻格子的高度差绝对值，从左上角走到右下角的最小体力消耗值](https://leetcode.com/problems/path-with-minimum-effort/)| [用回溯法暴力试，也可以用二分搜索，因为高度一定小于10^6, 所以我们先创建一个体力值为k是否能到达右下角，然后从0到10^6一直二分搜索来找到那个可以的最小值](../java/1631.path-with-minimum-effort.java) | backtracking, binary search, dfs
