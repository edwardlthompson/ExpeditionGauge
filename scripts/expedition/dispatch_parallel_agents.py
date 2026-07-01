#!/usr/bin/env python3
"""Launch parallel Cursor SDK agents (one per manifest row) when CURSOR_API_KEY is set."""
from __future__ import annotations

import argparse
import concurrent.futures
import json
import os
import subprocess
import sys
from datetime import datetime, timezone
from pathlib import Path


def repo_root() -> Path:
    return Path(__file__).resolve().parents[2]


def load_manifest(sprint: str) -> dict:
    path = repo_root() / "scripts" / "expedition" / "parallel-manifests" / f"sprint-{sprint}.json"
    if not path.is_file():
        raise SystemExit(f"Missing manifest: {path}")
    return json.loads(path.read_text(encoding="utf-8"))


def build_prompt(agent: dict, manifest: dict) -> str:
    scopes = "\n".join(f"- `{s}`" for s in agent.get("scopes", []))
    also = agent.get("alsoEdit") or []
    also_block = ""
    if also:
        also_block = "\n## Also edit (same agent)\n" + "\n".join(f"- `{p}`" for p in also) + "\n"
    forbidden = "\n".join(f"- `{p}`" for p in manifest.get("sequentialOnly", []))
    spec = agent.get("spec", "")
    spec_line = f"Read `{spec}` first if it exists.\n\n" if spec else ""
    return f"""# Parallel agent task — Sprint {manifest['sprint']} / {agent['id']}

{spec_line}## Task
{agent['task']}

## Allowed files (ONLY edit these)
{scopes}
{also_block}
## Forbidden (sequential owner only)
{forbidden}

## Rules
- Do NOT edit BUILD_PLAN.md or files outside allowed scope.
- Package namespace: dev.foss.expeditiongauge
- After edits run: bash scripts/watch-agent-gates.sh --once --autofix
- Commit on branch `{agent['branch']}` with a Conventional Commit message.

Implement now. Apply file changes with --force semantics.
"""


def ensure_worktree(root: Path, branch: str, worktree: Path) -> None:
    worktree.parent.mkdir(parents=True, exist_ok=True)
    if worktree.is_dir() and (worktree / ".git").exists():
        return
    if worktree.exists():
        raise SystemExit(f"Worktree path exists but is not a worktree: {worktree}")
    subprocess.run(
        ["git", "worktree", "add", "-B", branch, str(worktree), "HEAD"],
        cwd=root,
        check=True,
    )


def run_agent_sdk(agent: dict, manifest: dict, root: Path, worktrees_dir: Path) -> dict:
    try:
        from cursor_sdk import Agent, AgentOptions, LocalAgentOptions
    except ImportError as exc:
        raise SystemExit(
            "cursor-sdk not installed. Run: pip install cursor-sdk"
        ) from exc

    api_key = os.environ.get("CURSOR_API_KEY")
    if not api_key:
        raise SystemExit("CURSOR_API_KEY is required for SDK dispatch")

    slug = agent["slug"]
    worktree = worktrees_dir / slug
    ensure_worktree(root, agent["branch"], worktree)

    prompt_path = worktree / ".cursor" / "PARALLEL_AGENT_TASK.md"
    prompt_path.parent.mkdir(parents=True, exist_ok=True)
    prompt_path.write_text(build_prompt(agent, manifest), encoding="utf-8")

    prompt = f"Execute the parallel agent task in @.cursor/PARALLEL_AGENT_TASK.md"
    started = datetime.now(timezone.utc).isoformat()
    result = Agent.prompt(
        prompt,
        AgentOptions(
            api_key=api_key,
            model="composer-2.5",
            local=LocalAgentOptions(cwd=str(worktree)),
        ),
    )
    return {
        "id": agent["id"],
        "slug": slug,
        "branch": agent["branch"],
        "worktree": str(worktree.relative_to(root)).replace("\\", "/"),
        "runtime": "sdk",
        "started_at": started,
        "finished_at": datetime.now(timezone.utc).isoformat(),
        "status": result.status,
        "agent_id": getattr(result, "agent_id", None) or getattr(result, "agentId", None),
    }


def main() -> None:
    parser = argparse.ArgumentParser(description="Dispatch parallel agents via Cursor SDK")
    parser.add_argument("--sprint", required=True, help="Sprint id, e.g. 19b")
    parser.add_argument("--max-workers", type=int, default=5)
    args = parser.parse_args()

    root = repo_root()
    manifest = load_manifest(args.sprint)
    agents = [a for a in manifest["agents"] if a.get("owner") == "AGENT"]
    worktrees_dir = root / ".cursor" / "worktrees" / f"sprint-{args.sprint}"

    results: list[dict] = []
    with concurrent.futures.ThreadPoolExecutor(max_workers=min(args.max_workers, len(agents))) as pool:
        futures = [
            pool.submit(run_agent_sdk, agent, manifest, root, worktrees_dir) for agent in agents
        ]
        for fut in concurrent.futures.as_completed(futures):
            results.append(fut.result())

    state_path = root / ".cursor" / "parallel-dispatch" / f"sprint-{args.sprint}.json"
    state_path.parent.mkdir(parents=True, exist_ok=True)
    payload = {
        "sprint": args.sprint,
        "dispatched_at": datetime.now(timezone.utc).isoformat(),
        "runtime": "sdk",
        "agents": results,
    }
    state_path.write_text(json.dumps(payload, indent=2), encoding="utf-8")
    print(json.dumps(payload, indent=2))

    failed = [r for r in results if r.get("status") != "finished"]
    if failed:
        raise SystemExit(2)


if __name__ == "__main__":
    main()
