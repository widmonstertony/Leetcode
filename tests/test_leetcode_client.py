import json

import pytest

from leettutor.leetcode_client import (
    LeetCodeImportError,
    fetch_problem,
    html_to_markdown,
    parse_problem_reference,
)


class FakeResponse:
    def raise_for_status(self) -> None:
        return None

    def json(self):
        return {
            "data": {
                "question": {
                    "questionFrontendId": "704",
                    "title": "Binary Search",
                    "titleSlug": "binary-search",
                    "content": "<p>Find <strong>target</strong>.</p><pre>nums = [1]</pre>",
                    "difficulty": "Easy",
                    "isPaidOnly": False,
                    "exampleTestcases": "[-1,0,3,5,9,12]\n9\n[-1,0,3]\n2",
                    "metaData": json.dumps(
                        {
                            "name": "search",
                            "params": [{"name": "nums"}, {"name": "target"}],
                        }
                    ),
                    "hints": ["Keep an invariant"],
                    "topicTags": [{"name": "Binary Search", "slug": "binary-search"}],
                    "codeSnippets": [
                        {
                            "lang": "Python3",
                            "langSlug": "python3",
                            "code": "class Solution:\n    def search(self, nums, target):",
                        }
                    ],
                }
            }
        }


class FakeSession:
    def __init__(self) -> None:
        self.url = ""
        self.body = {}

    def post(self, url, *, json, headers, timeout):
        self.url = url
        self.body = json
        return FakeResponse()


def test_parse_problem_url_and_reject_other_hosts() -> None:
    assert parse_problem_reference(
        "https://leetcode.com/problems/binary-search/?envId=x"
    ) == ("binary-search", "leetcode.com")
    assert parse_problem_reference("two-sum") == ("two-sum", "leetcode.com")
    with pytest.raises(LeetCodeImportError):
        parse_problem_reference("https://example.com/problems/two-sum/")


def test_fetch_problem_extracts_template_method_and_sample_arguments() -> None:
    session = FakeSession()
    problem = fetch_problem("binary-search", session=session)
    assert session.url == "https://leetcode.com/graphql/"
    assert session.body["variables"] == {"titleSlug": "binary-search"}
    assert problem.method_name == "search"
    assert "class Solution" in problem.starter_code
    assert problem.starter_code.rstrip().endswith("pass")
    assert json.loads(problem.sample_cases)[0]["args"] == [[-1, 0, 3, 5, 9, 12], 9]
    assert "**target**" in problem.statement


def test_html_conversion_preserves_code_blocks() -> None:
    assert "```text" in html_to_markdown("<pre>x = 1</pre>")
