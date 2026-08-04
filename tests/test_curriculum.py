from pathlib import Path

from leettutor.curriculum import (
    ProgressStore,
    choose_next_problem,
    get_problem,
    progress_summary,
)


def test_auto_curriculum_starts_with_binary_search() -> None:
    problem = choose_next_problem({})
    assert problem.id == 704
    assert problem.topic == "二分"


def test_review_problem_is_prioritized() -> None:
    progress = {"34": {"status": "review", "attempts": 1, "topic": "二分"}}
    problem = choose_next_problem(progress, track="二分")
    assert problem.id == 34


def test_progress_round_trip(tmp_path: Path) -> None:
    store = ProgressStore(tmp_path / "progress.json")
    problem = get_problem(35)
    assert problem is not None
    store.update(problem, "in_progress")
    progress = store.update(problem, "mastered")
    assert progress["35"]["attempts"] == 1
    assert progress_summary(progress)["mastered"] == 1
