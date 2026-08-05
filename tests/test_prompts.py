from leettutor.prompts import build_tutor_opening, build_workspace_help_request


def test_tutor_opening_is_instant_diagnostic_not_solution() -> None:
    opening = build_tutor_opening(
        problem_id=704,
        title_cn="二分查找",
        difficulty="Easy",
        topic="二分",
        focus="建立收敛不变量",
        invariant_prompt="循环结束时 left 代表什么？",
        attempt=1,
    )
    assert "提示：" in opening
    assert "轮到你：" in opening
    assert opening.count("？") == 1
    assert "暴力做法" not in opening
    assert "循环结束时 left 代表什么" not in opening
    assert "完整代码" not in opening


def test_workspace_help_contains_current_runtime_context() -> None:
    request = build_workspace_help_request(
        problem="704. Binary Search",
        statement="Find target",
        language="Python",
        code="class Solution: pass",
        method_name="search",
        test_cases='{"args": [[1], 1]}',
        run_result="1/1 个测试通过",
        question="下一步呢？",
        trigger="test",
    )
    assert "class Solution: pass" in request
    assert "1/1 个测试通过" in request
    assert "下一步呢" in request
    assert "不超过120个中文字符" in request
    assert "恰好1个问题" in request


def test_explicit_full_solution_request_relaxes_short_turn_contract() -> None:
    request = build_workspace_help_request(
        problem="704. Binary Search",
        statement="Find target",
        language="Python",
        code="class Solution: pass",
        method_name="search",
        test_cases="[]",
        run_result="尚未运行",
        question="求最优解代码",
    )

    assert "用户明确要求完整代码" in request
    assert "不超过120个中文字符" not in request


def test_visual_request_allows_one_small_mermaid_map() -> None:
    request = build_workspace_help_request(
        problem="153. Find Minimum",
        statement="Rotated sorted array",
        language="Python",
        code="class Solution: pass",
        method_name="findMin",
        test_cases="[]",
        run_result="尚未运行",
        question="请用 Mermaid 视觉解释当前搜索区间",
    )

    assert "3～8节点" in request
    assert "fenced Mermaid" in request
    assert "不要给完整解法" in request
