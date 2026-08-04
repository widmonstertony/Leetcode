from pathlib import Path

import pytest

from leettutor.solutions import SolutionError, SolutionStore


def test_solution_round_trip(tmp_path: Path) -> None:
    store = SolutionStore(tmp_path)
    path = store.save("Python", "34.search-range.py", "print('ok')")
    assert path == tmp_path / "python" / "34.search-range.py"
    assert store.load("Python", "34.search-range.py") == "print('ok')\n"
    assert store.list_files("Python") == ["34.search-range.py"]


@pytest.mark.parametrize("filename", ["../bad.py", "bad.py", "1.has space.py"])
def test_solution_name_rejects_unsafe_paths(tmp_path: Path, filename: str) -> None:
    store = SolutionStore(tmp_path)
    with pytest.raises(SolutionError):
        store.save("Python", filename, "pass")


def test_existing_solution_requires_explicit_overwrite(tmp_path: Path) -> None:
    store = SolutionStore(tmp_path)
    store.save("Java", "1.two-sum.java", "class Solution {}")
    with pytest.raises(SolutionError):
        store.save("Java", "1.two-sum.java", "class Solution2 {}")
