"""Curated interview curriculum and locally persisted learning progress."""

from __future__ import annotations

import json
from dataclasses import dataclass
from datetime import UTC, datetime
from pathlib import Path
from typing import Any, Mapping


@dataclass(frozen=True)
class Problem:
    id: int
    slug: str
    title: str
    title_cn: str
    topic: str
    difficulty: str
    focus: str
    invariant_prompt: str

    @property
    def label(self) -> str:
        return f"{self.id}. {self.title}"

    @property
    def url(self) -> str:
        return f"https://leetcode.com/problems/{self.slug}/"


TOPIC_ORDER = ("二分", "栈", "优先队列", "DP")


PROBLEMS: tuple[Problem, ...] = (
    Problem(704, "binary-search", "Binary Search", "二分查找", "二分", "Easy", "建立闭区间与收敛不变量", "循环结束时，left/right 分别代表什么？"),
    Problem(35, "search-insert-position", "Search Insert Position", "搜索插入位置", "二分", "Easy", "把“找到值”改写为“找第一个可行位置”", "什么条件能说明 mid 仍可能是答案？"),
    Problem(34, "find-first-and-last-position-of-element-in-sorted-array", "Find First and Last Position of Element in Sorted Array", "在排序数组中查找元素的第一个和最后一个位置", "二分", "Medium", "统一 lower bound / upper bound，消灭等号混乱", "若使用 while left < right，第一次出现 target 的可行条件是什么？"),
    Problem(153, "find-minimum-in-rotated-sorted-array", "Find Minimum in Rotated Sorted Array", "寻找旋转排序数组中的最小值", "二分", "Medium", "用 right 作为可靠参照物保留候选", "nums[mid] 与 nums[right] 的比较能排除哪一半？"),
    Problem(33, "search-in-rotated-sorted-array", "Search in Rotated Sorted Array", "搜索旋转排序数组", "二分", "Medium", "识别有序半边并判断 target 是否在其中", "每轮至少有哪一半一定有序？"),
    Problem(875, "koko-eating-bananas", "Koko Eating Bananas", "爱吃香蕉的珂珂", "二分", "Medium", "从二分下标进阶到二分答案", "速度越大，完成时间如何单调变化？"),
    Problem(410, "split-array-largest-sum", "Split Array Largest Sum", "分割数组的最大值", "二分", "Hard", "把最优化问题改写为可行性判定", "给定最大段和 limit，如何判断它是否可行？"),
    Problem(20, "valid-parentheses", "Valid Parentheses", "有效的括号", "栈", "Easy", "明确栈中保存尚未匹配的对象", "遇到右括号时，谁应该被结算？"),
    Problem(155, "min-stack", "Min Stack", "最小栈", "栈", "Medium", "维护与主栈同步的辅助状态", "弹出元素后，最小值如何恢复？"),
    Problem(496, "next-greater-element-i", "Next Greater Element I", "下一个更大元素 I", "栈", "Easy", "理解单调栈中谁等待被结算", "栈顶与当前元素满足什么关系时应该 pop？"),
    Problem(739, "daily-temperatures", "Daily Temperatures", "每日温度", "栈", "Medium", "栈中保存下标以计算距离", "为什么栈里不能只存温度？"),
    Problem(394, "decode-string", "Decode String", "字符串解码", "栈", "Medium", "用栈保存嵌套层级的现场", "进入新的方括号前需要保存哪些状态？"),
    Problem(84, "largest-rectangle-in-histogram", "Largest Rectangle in Histogram", "柱状图中最大的矩形", "栈", "Hard", "用单调栈确定左右第一个更矮位置", "柱子在什么时候才能确定自己的完整宽度？"),
    Problem(42, "trapping-rain-water", "Trapping Rain Water", "接雨水", "栈", "Hard", "理解凹槽被结算时的高与宽", "pop 出来的柱子在几何上代表什么？"),
    Problem(215, "kth-largest-element-in-an-array", "Kth Largest Element in an Array", "数组中的第 K 个最大元素", "优先队列", "Medium", "固定大小的小顶堆", "堆顶应该代表当前保留元素中的什么？"),
    Problem(347, "top-k-frequent-elements", "Top K Frequent Elements", "前 K 个高频元素", "优先队列", "Medium", "频率表与固定大小堆组合", "为什么大小为 k 的小顶堆更容易淘汰元素？"),
    Problem(973, "k-closest-points-to-origin", "K Closest Points to Origin", "最接近原点的 K 个点", "优先队列", "Medium", "说清堆顶和保留集合的含义", "使用大小为 k 的堆时，堆顶应该是最近还是最远？"),
    Problem(23, "merge-k-sorted-lists", "Merge k Sorted Lists", "合并 K 个升序链表", "优先队列", "Hard", "堆只保存每条链的当前候选", "为什么堆大小最多只需要 k？"),
    Problem(295, "find-median-from-data-stream", "Find Median from Data Stream", "数据流的中位数", "优先队列", "Hard", "双堆维护分区与大小不变量", "两个堆分别保存哪一半，大小最多能差多少？"),
    Problem(70, "climbing-stairs", "Climbing Stairs", "爬楼梯", "DP", "Easy", "从递归问题定义一维状态", "dp[i] 究竟表示到达 i 的方法数还是剩余方法数？"),
    Problem(198, "house-robber", "House Robber", "打家劫舍", "DP", "Medium", "写清选择当前与跳过当前", "dp[i] 的两个来源分别对应什么选择？"),
    Problem(213, "house-robber-ii", "House Robber II", "打家劫舍 II", "DP", "Medium", "把环拆成两个互斥的线性问题", "第一间和最后一间为什么不能同时出现？"),
    Problem(322, "coin-change", "Coin Change", "零钱兑换", "DP", "Medium", "最少数量 DP 的不可达初值", "为什么初值不能全部设为 0？"),
    Problem(518, "coin-change-ii", "Coin Change II", "零钱兑换 II", "DP", "Medium", "遍历顺序区分组合与排列", "先遍历硬币为什么不会重复计算顺序？"),
    Problem(300, "longest-increasing-subsequence", "Longest Increasing Subsequence", "最长递增子序列", "DP", "Medium", "区分以 i 结尾与前 i 个元素", "状态若定义为以 i 结尾，答案一定是 dp[-1] 吗？"),
    Problem(1143, "longest-common-subsequence", "Longest Common Subsequence", "最长公共子序列", "DP", "Medium", "二维前缀状态与字符匹配选择", "两个末尾字符不同时，要比较哪两个子问题？"),
    Problem(72, "edit-distance", "Edit Distance", "编辑距离", "DP", "Medium", "让插入、删除、替换对应到坐标移动", "一次操作后，i/j 中哪一个会减少？"),
    Problem(312, "burst-balloons", "Burst Balloons", "戳气球", "DP", "Hard", "逆向选择最后一个被戳的气球", "为什么选择最后一个比选择第一个更容易定义边界？"),
)


class ProgressError(RuntimeError):
    """Raised when study progress cannot be persisted."""


class ProgressStore:
    def __init__(self, path: Path) -> None:
        self.path = path

    def load(self) -> dict[str, dict[str, Any]]:
        if not self.path.exists():
            return {}
        try:
            raw = json.loads(self.path.read_text(encoding="utf-8"))
        except (OSError, json.JSONDecodeError) as exc:
            raise ProgressError(f"无法读取学习进度：{exc}") from exc
        if not isinstance(raw, Mapping):
            raise ProgressError("学习进度文件格式不正确。")
        return {
            str(key): dict(value)
            for key, value in raw.items()
            if isinstance(value, Mapping)
        }

    def update(self, problem: Problem, status: str) -> dict[str, dict[str, Any]]:
        progress = self.load()
        key = str(problem.id)
        entry = progress.get(key, {})
        attempts = int(entry.get("attempts", 0))
        if status == "in_progress":
            attempts += 1
        progress[key] = {
            "status": status,
            "attempts": attempts,
            "topic": problem.topic,
            "updated_at": datetime.now(UTC).isoformat(),
        }
        self._save(progress)
        return progress

    def _save(self, progress: Mapping[str, Any]) -> None:
        temporary = self.path.with_suffix(self.path.suffix + ".tmp")
        try:
            temporary.write_text(
                json.dumps(progress, ensure_ascii=False, indent=2) + "\n",
                encoding="utf-8",
            )
            temporary.replace(self.path)
        except OSError as exc:
            raise ProgressError(f"无法保存学习进度：{exc}") from exc


def get_problem(problem_id: int | str) -> Problem | None:
    wanted = int(problem_id)
    return next((problem for problem in PROBLEMS if problem.id == wanted), None)


def choose_next_problem(
    progress: Mapping[str, Mapping[str, Any]],
    *,
    track: str = "自动补弱",
    difficulty: str = "循序渐进",
    exclude_id: int | None = None,
) -> Problem:
    """Choose deterministically so the learning plan remains understandable."""

    candidates = [problem for problem in PROBLEMS if problem.id != exclude_id]
    if track in TOPIC_ORDER:
        candidates = [problem for problem in candidates if problem.topic == track]
    if difficulty in {"Easy", "Medium", "Hard"}:
        candidates = [problem for problem in candidates if problem.difficulty == difficulty]
    if not candidates:
        raise ValueError("当前筛选条件下没有可选题目。")

    topic_scores: dict[str, float] = {}
    for topic in TOPIC_ORDER:
        topic_problems = [problem for problem in PROBLEMS if problem.topic == topic]
        mastered = sum(
            progress.get(str(problem.id), {}).get("status") == "mastered"
            for problem in topic_problems
        )
        topic_scores[topic] = mastered / len(topic_problems)

    def score(problem: Problem) -> tuple[float, int, int, int]:
        entry = progress.get(str(problem.id), {})
        status = entry.get("status", "new")
        status_rank = {
            "review": 0,
            "new": 1,
            "in_progress": 2,
            "mastered": 3,
        }.get(str(status), 1)
        weakness = topic_scores[problem.topic] if track == "自动补弱" else 0.0
        attempts = int(entry.get("attempts", 0))
        return (weakness, status_rank, attempts, PROBLEMS.index(problem))

    return min(candidates, key=score)


def progress_summary(progress: Mapping[str, Mapping[str, Any]]) -> dict[str, int]:
    return {
        "mastered": sum(item.get("status") == "mastered" for item in progress.values()),
        "review": sum(item.get("status") == "review" for item in progress.values()),
        "attempted": sum(int(item.get("attempts", 0)) > 0 for item in progress.values()),
        "total": len(PROBLEMS),
    }
