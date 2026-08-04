import pytest

from leettutor.code_runner import (
    CodeValidationError,
    parse_test_cases,
    run_python_solution,
)


BINARY_SEARCH = """
class Solution:
    def search(self, nums, target):
        left, right = 0, len(nums)
        while left < right:
            mid = (left + right) // 2
            if nums[mid] < target:
                left = mid + 1
            else:
                right = mid
        return left if left < len(nums) and nums[left] == target else -1
"""


def test_runs_solution_against_multiple_cases() -> None:
    result = run_python_solution(
        source=BINARY_SEARCH,
        method_name="search",
        test_cases=(
            '[{"args":[[-1,0,3,5,9,12],9],"expected":4},'
            '{"args":[[-1,0,3,5,9,12],2],"expected":-1}]'
        ),
    )
    assert result.succeeded
    assert result.summary == "2/2 个测试通过"


def test_reports_failed_expectation() -> None:
    result = run_python_solution(
        source=BINARY_SEARCH,
        method_name="search",
        test_cases='{"args":[[1,2,3],2],"expected":0}',
    )
    assert result.status == "failed"
    assert result.cases[0]["actual"] == 1


def test_rejects_filesystem_imports() -> None:
    with pytest.raises(CodeValidationError, match="不允许导入"):
        run_python_solution(
            source="import os\nclass Solution: pass",
            method_name="x",
            test_cases='{"args":[]}',
        )


def test_test_case_shape_is_explained() -> None:
    with pytest.raises(CodeValidationError, match="缺少 `args`"):
        parse_test_cases('{"expected": 1}')
