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
from leettutor.prompts import (  # noqa: E402
    ALGORITHM_SYSTEM_PROMPT,
    SYSTEM_DESIGN_SYSTEM_PROMPT,
)
from leettutor.system_design_curriculum import SYSTEM_DESIGN_CASES  # noqa: E402


PROBLEM_ENGLISH_COPY: dict[int, tuple[str, str]] = {
    704: ("Establish a closed search interval and its convergence invariant", "At loop exit, what do left and right represent?"),
    35: ("Turn exact lookup into finding the first feasible position", "What condition means mid can still be the answer?"),
    34: ("Unify lower and upper bounds without ambiguous equality cases", "With while left < right, what makes an index feasible for the first target?"),
    153: ("Use right as a reliable reference while preserving the minimum candidate", "What half can nums[mid] versus nums[right] safely eliminate?"),
    33: ("Identify the sorted half and decide whether target lies inside it", "Which half must be sorted on every iteration?"),
    875: ("Advance from searching indices to searching the answer space", "As speed increases, how does completion time change monotonically?"),
    410: ("Rewrite an optimization problem as a feasibility decision", "Given a maximum segment sum limit, how can you decide whether it is feasible?"),
    20: ("Define the stack as the set of unmatched opening tokens", "When a closing bracket arrives, what should be resolved?"),
    155: ("Maintain auxiliary state in lockstep with the main stack", "After a pop, how is the previous minimum restored?"),
    496: ("Understand which elements are waiting to be resolved in a monotonic stack", "What relation between the stack top and current value should trigger a pop?"),
    739: ("Store indices in the stack so distance remains available", "Why is storing temperatures alone insufficient?"),
    394: ("Use a stack to preserve the context of each nesting level", "Which pieces of state must be saved before entering a new bracket?"),
    84: ("Use a monotonic stack to find the first shorter bar on each side", "When does a bar's complete usable width become known?"),
    42: ("Resolve each basin with an explicit height and width", "Geometrically, what does a popped bar represent?"),
    215: ("Keep a fixed-size min-heap", "What should the heap top mean among the retained elements?"),
    347: ("Combine a frequency table with a fixed-size heap", "Why does a size-k min-heap make eviction straightforward?"),
    973: ("State exactly what the heap top and retained set mean", "For a size-k heap, should the top be the nearest or farthest retained point?"),
    23: ("Keep only the current candidate from each sorted list", "Why does the heap never need more than k entries?"),
    295: ("Maintain partition and size invariants with two heaps", "Which half belongs in each heap, and how far may their sizes differ?"),
    70: ("Define a one-dimensional state from the recursive question", "Does dp[i] count ways to reach i or ways remaining after i?"),
    198: ("Write the choices to take or skip the current house", "Which two choices produce dp[i]?"),
    213: ("Split the cycle into two mutually exclusive linear cases", "Why can the first and last houses never appear together?"),
    322: ("Choose an unreachable initial value for minimum-count DP", "Why can the initial array not be all zeroes?"),
    518: ("Use iteration order to distinguish combinations from permutations", "Why does iterating coins first avoid recounting orderings?"),
    300: ("Distinguish a subsequence ending at i from one using the first i items", "If dp[i] means ending at i, must the answer be dp[-1]?"),
    1143: ("Define a two-dimensional prefix state and the character-match choice", "When the last characters differ, which two subproblems should be compared?"),
    72: ("Map insert, delete, and replace to coordinate movement", "After one operation, which of i and j decreases?"),
    312: ("Reverse the choice by selecting the last balloon to burst", "Why is choosing the last balloon easier for defining boundaries than choosing the first?"),
}


def build_catalog() -> dict[str, object]:
    return {
        "schema_version": 2,
        "problems": [
            {
                **asdict(problem),
                "focus_en": PROBLEM_ENGLISH_COPY[problem.id][0],
                "invariant_prompt_en": PROBLEM_ENGLISH_COPY[problem.id][1],
                "url": f"https://leetcode.com/problems/{problem.slug}/",
            }
            for problem in PROBLEMS
        ],
        "system_design": [asdict(case) for case in SYSTEM_DESIGN_CASES],
        "prompts": {
            "algorithm": ALGORITHM_SYSTEM_PROMPT,
            "system_design": SYSTEM_DESIGN_SYSTEM_PROMPT,
        },
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
