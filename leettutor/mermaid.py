"""Parse Markdown into normal and Mermaid-renderable segments."""

from __future__ import annotations

import re
from dataclasses import dataclass
from typing import Literal


_MERMAID_BLOCK = re.compile(
    r"```mermaid[ \t]*\r?\n(?P<body>.*?)```", re.IGNORECASE | re.DOTALL
)


@dataclass(frozen=True)
class Segment:
    kind: Literal["markdown", "mermaid"]
    content: str


def split_mermaid_blocks(text: str) -> list[Segment]:
    """Extract fenced Mermaid blocks while preserving surrounding Markdown."""

    segments: list[Segment] = []
    cursor = 0
    for match in _MERMAID_BLOCK.finditer(text):
        markdown = text[cursor : match.start()].strip()
        if markdown:
            segments.append(Segment("markdown", markdown))
        mermaid = match.group("body").strip()
        if mermaid:
            segments.append(Segment("mermaid", mermaid))
        cursor = match.end()

    tail = text[cursor:].strip()
    if tail:
        segments.append(Segment("markdown", tail))
    return segments
