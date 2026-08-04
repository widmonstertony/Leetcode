"""Public LeetCode problem importer (no account cookies or submissions)."""

from __future__ import annotations

import html
import json
import re
from dataclasses import asdict, dataclass
from html.parser import HTMLParser
from typing import Any
from urllib.parse import urlparse

import requests


QUESTION_QUERY = """
query questionData($titleSlug: String!) {
  question(titleSlug: $titleSlug) {
    questionFrontendId
    title
    titleSlug
    content
    difficulty
    isPaidOnly
    exampleTestcases
    metaData
    hints
    topicTags { name slug }
    codeSnippets { lang langSlug code }
  }
}
"""


class LeetCodeImportError(RuntimeError):
    """Raised when a public problem cannot be imported."""


class _MarkdownishParser(HTMLParser):
    def __init__(self) -> None:
        super().__init__()
        self.parts: list[str] = []
        self.in_pre = False
        self.list_depth = 0

    def handle_starttag(self, tag: str, attrs: list[tuple[str, str | None]]) -> None:
        if tag in {"p", "div", "br", "h1", "h2", "h3", "h4"}:
            self.parts.append("\n")
        elif tag == "pre":
            self.in_pre = True
            self.parts.append("\n```text\n")
        elif tag == "code" and not self.in_pre:
            self.parts.append("`")
        elif tag == "li":
            self.parts.append("\n- ")
        elif tag in {"strong", "b"}:
            self.parts.append("**")
        elif tag in {"em", "i"}:
            self.parts.append("*")

    def handle_endtag(self, tag: str) -> None:
        if tag == "pre":
            self.in_pre = False
            self.parts.append("\n```\n")
        elif tag == "code" and not self.in_pre:
            self.parts.append("`")
        elif tag in {"strong", "b"}:
            self.parts.append("**")
        elif tag in {"em", "i"}:
            self.parts.append("*")
        elif tag in {"p", "div", "h1", "h2", "h3", "h4"}:
            self.parts.append("\n")

    def handle_data(self, data: str) -> None:
        self.parts.append(data)

    def markdown(self) -> str:
        text = html.unescape("".join(self.parts)).replace("\xa0", " ")
        text = re.sub(r"[ \t]+\n", "\n", text)
        text = re.sub(r"\n{3,}", "\n\n", text)
        return text.strip()


def html_to_markdown(value: str) -> str:
    parser = _MarkdownishParser()
    parser.feed(value)
    return parser.markdown()


def parse_problem_reference(reference: str) -> tuple[str, str]:
    """Return ``(slug, host)`` from a slug or LeetCode URL."""

    value = reference.strip()
    if not value:
        raise LeetCodeImportError("请粘贴 LeetCode 题目链接或题目 slug。")
    host = "leetcode.com"
    if "://" in value:
        parsed = urlparse(value)
        host = parsed.netloc.casefold().removeprefix("www.")
        if host not in {"leetcode.com", "leetcode.cn"}:
            raise LeetCodeImportError("目前只接受 leetcode.com 或 leetcode.cn 题目链接。")
        match = re.search(r"/problems/([a-z0-9-]+)", parsed.path.casefold())
        if not match:
            raise LeetCodeImportError("链接中没有找到 `/problems/题目-slug/`。")
        slug = match.group(1)
    else:
        slug = value.casefold().strip("/")
    if not re.fullmatch(r"[a-z0-9-]+", slug):
        raise LeetCodeImportError("题目 slug 只能包含小写字母、数字和连字符。")
    return slug, host


@dataclass(frozen=True)
class ImportedProblem:
    frontend_id: str
    title: str
    slug: str
    difficulty: str
    statement: str
    starter_code: str
    method_name: str
    example_testcases: str
    sample_cases: str
    topics: tuple[str, ...]
    hints: tuple[str, ...]
    paid_only: bool
    host: str

    @property
    def url(self) -> str:
        return f"https://{self.host}/problems/{self.slug}/"

    def to_dict(self) -> dict[str, Any]:
        value = asdict(self)
        value["url"] = self.url
        return value


def _method_name(raw_metadata: str) -> str:
    try:
        metadata = json.loads(raw_metadata or "{}")
    except json.JSONDecodeError:
        return ""
    return str(metadata.get("name", ""))


def _sample_cases(raw_examples: str, raw_metadata: str) -> str:
    """Turn LeetCode's newline-separated arguments into runnable JSON cases."""

    try:
        metadata = json.loads(raw_metadata or "{}")
    except json.JSONDecodeError:
        return ""
    parameter_count = len(metadata.get("params") or [])
    lines = [line.strip() for line in raw_examples.splitlines() if line.strip()]
    if not parameter_count or not lines or len(lines) % parameter_count:
        return ""
    values: list[Any] = []
    try:
        for line in lines:
            values.append(json.loads(line))
    except json.JSONDecodeError:
        return ""
    cases = [
        {"args": values[index : index + parameter_count]}
        for index in range(0, len(values), parameter_count)
    ]
    return json.dumps(cases, ensure_ascii=False, indent=2)


def _python_snippet(snippets: list[dict[str, Any]] | None) -> str:
    for snippet in snippets or []:
        if str(snippet.get("langSlug", "")).casefold() in {"python3", "python"}:
            code = str(snippet.get("code", "")).rstrip()
            # LeetCode starter snippets commonly end at ``def ...:`` with an
            # empty body, which is not valid Python until the user types.  A
            # local editor should be runnable immediately, so add a placeholder.
            lines = code.splitlines()
            if lines and lines[-1].rstrip().endswith(":"):
                indentation = len(lines[-1]) - len(lines[-1].lstrip()) + 4
                code += "\n" + " " * indentation + "pass"
            return code + "\n"
    return "class Solution:\n    pass\n"


def fetch_problem(
    reference: str,
    *,
    timeout_seconds: float = 12.0,
    session: requests.Session | None = None,
) -> ImportedProblem:
    slug, host = parse_problem_reference(reference)
    endpoint = f"https://{host}/graphql/"
    http = session or requests.Session()
    try:
        response = http.post(
            endpoint,
            json={"query": QUESTION_QUERY, "variables": {"titleSlug": slug}},
            headers={
                "Origin": f"https://{host}",
                "Referer": f"https://{host}/problems/{slug}/",
                "User-Agent": "LeetTutor-Local/1.0",
            },
            timeout=max(3.0, min(float(timeout_seconds), 30.0)),
        )
        response.raise_for_status()
        payload = response.json()
    except (requests.RequestException, ValueError) as exc:
        raise LeetCodeImportError(
            f"无法连接 LeetCode 导入题目：{exc}。仍可打开原题并手动粘贴题面。"
        ) from exc
    if payload.get("errors"):
        message = payload["errors"][0].get("message", "LeetCode 返回查询错误")
        raise LeetCodeImportError(str(message))
    question = payload.get("data", {}).get("question")
    if not isinstance(question, dict):
        raise LeetCodeImportError(f"没有找到题目 `{slug}`。")
    topics = tuple(
        str(topic.get("name"))
        for topic in question.get("topicTags") or []
        if topic.get("name")
    )
    raw_metadata = str(question.get("metaData", ""))
    raw_examples = str(question.get("exampleTestcases", ""))
    return ImportedProblem(
        frontend_id=str(question.get("questionFrontendId", "")),
        title=str(question.get("title", slug)),
        slug=str(question.get("titleSlug", slug)),
        difficulty=str(question.get("difficulty", "")),
        statement=html_to_markdown(str(question.get("content", ""))),
        starter_code=_python_snippet(question.get("codeSnippets")),
        method_name=_method_name(raw_metadata),
        example_testcases=raw_examples,
        sample_cases=_sample_cases(raw_examples, raw_metadata),
        topics=topics,
        hints=tuple(str(hint) for hint in question.get("hints") or []),
        paid_only=bool(question.get("isPaidOnly")),
        host=host,
    )
