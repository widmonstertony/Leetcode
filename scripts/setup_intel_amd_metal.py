"""Diagnose or install LeetTutor's Intel Mac AMD Metal runtime."""

from __future__ import annotations

import argparse
import sys
from pathlib import Path


PROJECT_ROOT = Path(__file__).resolve().parents[1]
if str(PROJECT_ROOT) not in sys.path:
    sys.path.insert(0, str(PROJECT_ROOT))

from leettutor.hardware import detect_hardware
from leettutor.metal_runtime import (
    AMD_METAL_ENDPOINT,
    MetalRuntimeError,
    ensure_metal_runtime,
    inspect_metal_setup,
    install_metal_runtime,
)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Check or install the experimental Intel Mac AMD Metal backend."
    )
    parser.add_argument(
        "--install", action="store_true", help="Clone, patch, and build llama-server."
    )
    parser.add_argument(
        "--start", action="store_true", help="Start the endpoint after the checks."
    )
    return parser.parse_args()


def main() -> int:
    args = parse_args()
    profile = detect_hardware()
    status = inspect_metal_setup(
        project_root=PROJECT_ROOT,
        gpu_name=profile.gpu,
        vram_gb=profile.vram_gb,
    )
    print("LeetTutor Intel + AMD Metal diagnostics")
    for line in status.report_lines():
        print(f"  {line}")

    if args.install:
        try:
            for update in install_metal_runtime(
                project_root=PROJECT_ROOT,
                gpu_name=profile.gpu,
                vram_gb=profile.vram_gb,
            ):
                suffix = f": {update.detail}" if update.detail else ""
                print(f"[{update.progress:>4.0%}] {update.phase}{suffix}")
        except MetalRuntimeError as exc:
            print(f"Install failed: {exc}", file=sys.stderr)
            return 1

    if args.start:
        try:
            handle = ensure_metal_runtime(project_root=PROJECT_ROOT)
        except MetalRuntimeError as exc:
            print(f"Start failed: {exc}", file=sys.stderr)
            return 1
        owner = "started" if handle.managed else "already running"
        print(f"AMD Metal endpoint {owner}: {AMD_METAL_ENDPOINT}")
        if handle.managed:
            print("Press Ctrl+C to stop it.")
            try:
                handle.process.wait() if handle.process is not None else None
            except KeyboardInterrupt:
                handle.stop()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
