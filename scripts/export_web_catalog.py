#!/usr/bin/env python3
"""Export the source curriculum used by the browser-hosted LeetTutor UI."""

from __future__ import annotations

from dataclasses import asdict
import json
from pathlib import Path
import sys


PROJECT_ROOT = Path(__file__).resolve().parents[1]
if str(PROJECT_ROOT) not in sys.path:
    sys.path.insert(0, str(PROJECT_ROOT))

from leettutor.curriculum import PROBLEMS  # noqa: E402
from leettutor.system_design_curriculum import SYSTEM_DESIGN_CASES  # noqa: E402


def build_catalog() -> dict[str, object]:
    return {
        "schema_version": 1,
        "problems": [
            {**asdict(problem), "url": f"https://leetcode.com/problems/{problem.slug}/"}
            for problem in PROBLEMS
        ],
        "system_design": [asdict(case) for case in SYSTEM_DESIGN_CASES],
    }


def main() -> None:
    destination = PROJECT_ROOT / "web-demo" / "catalog.json"
    destination.write_text(
        json.dumps(build_catalog(), ensure_ascii=False, indent=2) + "\n",
        encoding="utf-8",
    )
    print(f"Exported {destination.relative_to(PROJECT_ROOT)}")


if __name__ == "__main__":
    main()
