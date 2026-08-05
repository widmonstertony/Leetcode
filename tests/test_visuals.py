from leettutor.visuals import algorithm_pattern_mermaid, system_design_pattern_mermaid


def test_algorithm_visuals_show_the_invariant_without_solution_code() -> None:
    binary = algorithm_pattern_mermaid("二分")
    dp = algorithm_pattern_mermaid("DP")

    assert binary.startswith("flowchart LR")
    assert "left < right" in binary
    assert "答案候选" in binary
    assert "定义状态" in dp and "遍历顺序" in dp
    assert "class Solution" not in binary + dp


def test_system_design_visuals_follow_the_selected_track_and_language() -> None:
    realtime = system_design_pattern_mermaid("realtime")
    transactions = system_design_pattern_mermaid("transactions", language="en")

    assert "连接网关" in realtime and "消息日志" in realtime
    assert "Idempotency" in transactions and "Ledger" in transactions
    assert realtime.count("-->") == 5
