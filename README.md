# LeetCode — Python Reboot

这个仓库现在以 **Python 重刷**为主，同时继续保留和维护 Java 版本。不同语言各自放在独立目录，不再让根目录被数百个题解文件占满。

## 从这里开始

| 入口 | 用途 |
| --- | --- |
| [python/](python/) | 当前和之后的 Python 解答 |
| [java/](java/) | Java 解答 |
| [java/oa/](java/oa/) | Amazon、Google OA |
| [sql/](sql/) | SQL 解答 |
| [Python 重刷手册](docs/PYTHON_PLAYBOOK.md) | 题型判断、背诵模板、重刷路线 |
| [BST 旧笔记](docs/BST.md) | 原 BST 笔记 |

## 目录结构

```text
Leetcode/
├── README.md
├── python/                    # 当前重刷重点
│   └── <题号>.<题名>.py
├── java/
│   ├── <题号>.<题名>.java
│   └── oa/                    # Amazon / Google OA
├── sql/
│   └── <题号>.<题名>.sql
└── docs/
│   ├── PYTHON_PLAYBOOK.md     # 完整模板和旧题笔记
│   └── BST.md
```

## 当前 Python 进度

| 题目 | 主题 | 状态 |
| --- | --- | --- |
| [1. Two Sum](python/1.two-sum.py) | Hash Map | 已完成 |
| [49. Group Anagrams](python/49.group-anagrams.py) | Hash / Counting | 已完成 |
| [128. Longest Consecutive Sequence](python/128.longest-consecutive-sequence.py) | Set | 已完成 |
| [347. Top K Frequent Elements](python/347.top-k-frequent-elements.py) | Hash + Heap | 已完成 |
| [29. Divide Two Integers](python/29.divide-two-integers.py) | Bit / Math | 待完成 |

以后新增 Python 解答放入 `python/`，命名继续使用：

```text
<题号>.<题名>.py
```

## 近期重刷优先级

过去最容易混淆的是二分边界、Stack、Priority Queue 和 DP，因此先恢复这四类。

| 顺序 | 专题 | 建议题目 | 过关标准 |
| ---: | --- | --- | --- |
| 1 | Binary Search | 35、34、275、378、410 | 固定 `while left < right`，循环后只看 `left` |
| 2 | Stack | 20、496、503、84、42 | 写清栈里放谁、何时 pop、谁被结算 |
| 3 | Priority Queue | 215、347、373、1167、295 | 写代码前说清堆顶含义和堆的大小 |
| 4 | DP | 70、198、322、518、300、312、309 | 先写 State、Choice、Transition、Base、Order |

完整路线见[重刷手册的 Python 重刷路线](docs/PYTHON_PLAYBOOK.md#python-重刷路线)。

## 二分只背这一套

个人约定：数组和整数二分尽量全部写成“收敛到唯一候选”。

```python
left, right = ...

while left < right:
    mid = left + (right - left) // 2

    if mid_may_be_answer(mid):
        right = mid
    else:
        left = mid + 1

# 固定：left == right
candidate = left
```

记忆：

```text
while 永远用 <
MID 可能是答案：保留 mid
MID 不可能是答案：扔掉 mid
循环结束：看 left，不看 mid
```

- 要下标：返回或检查 `left`。
- 要数组中的值：返回 `nums[left]`。
- 答案可能不存在：先检查 `left` 是否越界、是否满足题意。
- Lower bound：`mid_may_be_answer = nums[mid] >= target`。
- Upper bound：`mid_may_be_answer = nums[mid] > target`。

更完整的边界、Stack、Heap 和 DP 记忆卡见[四大重点背诵卡](docs/PYTHON_PLAYBOOK.md#四大重点背诵卡)。

## 每道 Python 题只记录三件事

```python
# Pattern:
# Invariant:
# Mistake:
```

- `Pattern`：这题属于哪个模板。
- `Invariant`：循环或递归过程中始终成立的事实。
- `Mistake`：这次真正写错的一点。

当天做完，第二天不看答案重写，第七天只默写骨架。
