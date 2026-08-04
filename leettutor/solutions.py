"""Safe access to the repository's Python and Java solution folders."""

from __future__ import annotations

import re
from pathlib import Path


_SAFE_SOLUTION_NAME = re.compile(
    r"^[0-9]+\.[A-Za-z0-9][A-Za-z0-9._-]*\.(?:py|java)$"
)


class SolutionError(RuntimeError):
    """Raised for invalid names or failed solution-file operations."""


class SolutionStore:
    """Read and save solutions without allowing path traversal."""

    LANGUAGE_DIRECTORIES = {"Python": "python", "Java": "java"}
    LANGUAGE_SUFFIXES = {"Python": ".py", "Java": ".java"}

    def __init__(self, project_root: Path) -> None:
        self.project_root = project_root.resolve()

    def list_files(self, language: str) -> list[str]:
        directory = self._language_directory(language)
        suffix = self.LANGUAGE_SUFFIXES[language]
        if not directory.exists():
            return []
        return sorted(
            (path.name for path in directory.glob(f"*{suffix}") if path.is_file()),
            key=self._sort_key,
        )

    def load(self, language: str, filename: str) -> str:
        path = self._safe_path(language, filename)
        try:
            return path.read_text(encoding="utf-8")
        except FileNotFoundError as exc:
            raise SolutionError(f"找不到题解：{filename}") from exc
        except OSError as exc:
            raise SolutionError(f"无法读取 {filename}：{exc}") from exc

    def save(
        self, language: str, filename: str, content: str, *, overwrite: bool = False
    ) -> Path:
        path = self._safe_path(language, filename)
        if path.exists() and not overwrite:
            raise SolutionError("文件已存在；确认内容后勾选“允许覆盖”。")
        path.parent.mkdir(parents=True, exist_ok=True)
        try:
            path.write_text(content.rstrip() + "\n", encoding="utf-8")
        except OSError as exc:
            raise SolutionError(f"无法保存 {filename}：{exc}") from exc
        return path

    def _language_directory(self, language: str) -> Path:
        try:
            name = self.LANGUAGE_DIRECTORIES[language]
        except KeyError as exc:
            raise SolutionError(f"不支持的语言：{language}") from exc
        return self.project_root / name

    def _safe_path(self, language: str, filename: str) -> Path:
        suffix = self.LANGUAGE_SUFFIXES.get(language)
        if suffix is None:
            raise SolutionError(f"不支持的语言：{language}")
        if (
            Path(filename).name != filename
            or not _SAFE_SOLUTION_NAME.fullmatch(filename)
            or not filename.endswith(suffix)
        ):
            raise SolutionError(
                f"文件名需符合 <题号>.<题名>{suffix}，且不能包含空格或路径。"
            )
        return self._language_directory(language) / filename

    @staticmethod
    def _sort_key(filename: str) -> tuple[int, str]:
        number, _, _ = filename.partition(".")
        return (int(number), filename.casefold())
