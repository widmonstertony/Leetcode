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
    assert "暴力做法" in opening
    assert "循环结束时 left 代表什么" in opening
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
