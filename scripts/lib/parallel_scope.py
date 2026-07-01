"""Parse BUILD_PLAN Parallel tables, detect scope overlaps, build dispatch manifests."""
from __future__ import annotations

import json
import re
import string
from dataclasses import dataclass
from datetime import datetime, timezone
from pathlib import Path

MAX_AGENTS = 8

# ExpeditionGauge composition roots — parallel agents must not edit these.
FORBIDDEN_PATHS = [
    "BUILD_PLAN.md",
    "COMPLETED_TASKS.md",
    "AGENT_MEMORY.md",
    "DECISION_LOG.md",
    "examples/android/app/src/main/java/dev/foss/expeditiongauge/MainActivity.kt",
    "examples/android/app/src/main/java/dev/foss/expeditiongauge/ui/navigation/ExpeditionGaugeApp.kt",
    "examples/android/app/src/main/java/dev/foss/expeditiongauge/ui/navigation/AppScreenRouter.kt",
    "examples/android/app/src/main/java/dev/foss/expeditiongauge/ExpeditionGaugeServices.kt",
    "examples/android/app/src/main/java/dev/foss/expeditiongauge/ui/layout/InsetAwareScaffold.kt",
    "examples/android/settings.gradle.kts",
    "examples/android/app/build.gradle.kts",
    ".github/workflows/release.yml",
]

SPRINT_HEADER = re.compile(r"^#{2,3}\s+Sprint\s+", re.I)
PARALLEL_HEADER = re.compile(r"^#{3,4}\s+.*Parallel", re.I)
SEQUENTIAL_HEADER = re.compile(r"^#{3,4}\s+Sequential", re.I)
TABLE_ROW = re.compile(r"^\|([^|]+)\|([^|]+)\|([^|]+)\|")
OPEN_AGENT_SEQ = re.compile(
    r"^(?:\d+[a-z]?\.)\s+(?:🔲|⬜|\[ \])\s+\[AGENT\]\s+",
)
AGENT_COUNT_TARGET = re.compile(
    r"<!--\s*agent_count_target:\s*(\d+)", re.I
)
PARALLEL_EXCEPTION = re.compile(
    r"<!--\s*parallel_exception:\s*(.+?)\s*-->", re.I
)
PLAYBOOK_MARKERS = ("## Child Repo Playbook", "## Active board")


@dataclass
class ParallelRow:
    task: str
    owner: str
    scope: str
    sprint_title: str = ""


@dataclass
class SprintBlock:
    title: str
    lines: list[str]
    start: int
    agent_count_target: int | None
    parallel_exception: str | None


def normalize_scope(scope: str) -> str:
    return scope.strip().rstrip("/")


def scopes_overlap(a: str, b: str) -> bool:
    pa, pb = normalize_scope(a), normalize_scope(b)
    if not pa or not pb:
        return False
    return pa == pb or pa.startswith(pb + "/") or pb.startswith(pa + "/")


def find_overlaps(scopes: list[str]) -> list[str]:
    errors: list[str] = []
    for i, a in enumerate(scopes):
        for j, b in enumerate(scopes):
            if j <= i:
                continue
            if scopes_overlap(a, b):
                errors.append(f"overlap: {a!r} vs {b!r}")
    return errors


def slugify(text: str) -> str:
    out: list[str] = []
    for ch in text.lower().strip().replace("/", "-").replace("_", "-"):
        if ch.isalnum():
            out.append(ch)
        elif ch in " -":
            if out and out[-1] != "-":
                out.append("-")
    slug = "".join(out).strip("-")
    return slug[:48] or "task"


def load_json(path: Path) -> dict | None:
    if not path.exists():
        return None
    try:
        return json.loads(path.read_text(encoding="utf-8"))
    except json.JSONDecodeError:
        return None


def resolve_context(
    root: Path,
    *,
    stack: str | None = None,
    feature: str | None = None,
) -> dict[str, str | list[str]]:
    ctx: dict[str, str | list[str]] = {
        "stack": stack or "android",
        "feature": feature or "",
        "active_modules": [],
    }
    progress = load_json(root / ".cursor/agent-progress.json")
    if progress and not feature and progress.get("current_feature"):
        ctx["feature"] = str(progress["current_feature"])
    selection = load_json(root / ".cursor/stack-selection.json")
    if selection:
        if not stack and selection.get("stack"):
            ctx["stack"] = str(selection["stack"])
        modules = selection.get("active_modules")
        if isinstance(modules, list):
            ctx["active_modules"] = [str(m) for m in modules]
    if not ctx["active_modules"]:
        ctx["active_modules"] = [str(ctx["stack"])]
    return ctx


def substitute_placeholders(scope: str, ctx: dict[str, str | list[str]]) -> str:
    result = scope
    result = result.replace("{stack}", str(ctx.get("stack", "android")))
    feature = str(ctx.get("feature", ""))
    if "{feature}" in result and not feature:
        raise ValueError(
            "Unresolved {feature} in scope "
            f"{scope!r}. Set current_feature via agent-progress.sh or pass --feature."
        )
    result = result.replace("{feature}", feature)
    return result


def _playbook_start(lines: list[str]) -> int:
    for i, line in enumerate(lines):
        stripped = line.strip()
        for marker in PLAYBOOK_MARKERS:
            if stripped.startswith(marker):
                return i
    return 0


def _in_playbook(line: str) -> bool:
    stripped = line.strip()
    return any(stripped.startswith(m) for m in PLAYBOOK_MARKERS)


def parse_parallel_rows(text: str) -> list[ParallelRow]:
    rows: list[ParallelRow] = []
    current_sprint = ""
    in_parallel = False
    for line in text.splitlines():
        if SPRINT_HEADER.match(line):
            current_sprint = line.strip().lstrip("#").strip()
            in_parallel = False
            continue
        if PARALLEL_HEADER.match(line):
            in_parallel = True
            continue
        if in_parallel and line.startswith("#"):
            in_parallel = False
            continue
        if not in_parallel:
            continue
        m = TABLE_ROW.match(line)
        if not m or m.group(1).strip().lower() in ("task", "---"):
            continue
        task, owner, scope_cell = m.group(1).strip(), m.group(2).strip(), m.group(3).strip()
        scope_m = re.search(r"`([^`]+)`", scope_cell)
        if not scope_m:
            continue
        rows.append(
            ParallelRow(
                task=task,
                owner=owner.upper(),
                scope=scope_m.group(1).strip(),
                sprint_title=current_sprint,
            )
        )
    return rows


def parse_sprint_blocks(text: str) -> list[SprintBlock]:
    """Return sprint sections from Child Repo Playbook or Active board onward."""
    lines = text.splitlines()
    start_idx = _playbook_start(lines)
    blocks: list[SprintBlock] = []
    i = start_idx
    while i < len(lines):
        line = lines[i]
        if SPRINT_HEADER.match(line):
            title = line.strip().lstrip("#").strip()
            block_lines = [line]
            i += 1
            while i < len(lines) and not (
                SPRINT_HEADER.match(lines[i])
                or (
                    lines[i].startswith("## ")
                    and not lines[i].startswith("### ")
                )
            ):
                block_lines.append(lines[i])
                i += 1
            body = "\n".join(block_lines)
            target_m = AGENT_COUNT_TARGET.search(body)
            exc_m = PARALLEL_EXCEPTION.search(body)
            blocks.append(
                SprintBlock(
                    title=title,
                    lines=block_lines,
                    start=i,
                    agent_count_target=int(target_m.group(1)) if target_m else None,
                    parallel_exception=exc_m.group(1).strip() if exc_m else None,
                )
            )
            continue
        i += 1
    return blocks


def agent_rows(rows: list[ParallelRow]) -> list[ParallelRow]:
    return [r for r in rows if r.owner == "AGENT"]


def _matches_feature(row: ParallelRow, feature: str) -> bool:
    if not feature:
        return True
    title = row.sprint_title
    return (
        feature in title
        or f"Sprint {feature}" in title
        or feature in row.scope
    )


def suggest_rows(ctx: dict[str, str | list[str]]) -> list[ParallelRow]:
    stack = str(ctx.get("stack", "android"))
    feature = str(ctx.get("feature", ""))
    modules = ctx.get("active_modules") or [stack]
    suggestions: list[ParallelRow] = []

    if stack == "android" and feature:
        suggestions.extend(
            [
                ParallelRow(
                    task="Feature logic + unit tests",
                    owner="AGENT",
                    scope="examples/android/app/src/test/java/dev/foss/expeditiongauge/",
                ),
                ParallelRow(
                    task="View / Compose UI slice",
                    owner="AGENT",
                    scope="examples/android/app/src/main/java/dev/foss/expeditiongauge/ui/",
                ),
            ]
        )
    elif len(modules) > 1:
        for mod in modules:
            suggestions.append(
                ParallelRow(
                    task=f"{mod} stack slice",
                    owner="AGENT",
                    scope=f"examples/{mod}/**",
                )
            )
    elif feature:
        suggestions.extend(
            [
                ParallelRow(
                    task="Logic + unit tests",
                    owner="AGENT",
                    scope=f"examples/{stack}/src/{feature}/",
                ),
                ParallelRow(
                    task="View + i18n",
                    owner="AGENT",
                    scope=f"examples/{stack}/src/components/",
                ),
            ]
        )
    else:
        suggestions.extend(
            [
                ParallelRow(
                    task="App code",
                    owner="AGENT",
                    scope=f"examples/{stack}/**",
                ),
                ParallelRow(
                    task="Docs + module guides",
                    owner="AGENT",
                    scope="docs/**",
                ),
            ]
        )
    return suggestions[:MAX_AGENTS]


def sequential_agent_open(text: str, before_parallel: bool = True) -> list[str]:
    lines = text.splitlines()
    in_child = False
    seen_parallel = False
    open_items: list[str] = []
    for line in lines:
        if _in_playbook(line):
            in_child = True
            continue
        if not in_child:
            continue
        if line.startswith("## Ongoing Maintenance"):
            break
        if PARALLEL_HEADER.match(line):
            seen_parallel = True
            if before_parallel:
                break
            continue
        if seen_parallel and before_parallel:
            break
        if OPEN_AGENT_SEQ.match(line):
            open_items.append(line.strip())
    return open_items


def write_lock_file(root: Path, manifest: dict) -> Path:
    lock = root / ".cursor" / "parallel-scope-lock.json"
    lock.parent.mkdir(parents=True, exist_ok=True)
    payload = dict(manifest)
    payload.setdefault("created_at", datetime.now(timezone.utc).replace(microsecond=0).isoformat())
    lock.write_text(json.dumps(payload, indent=2) + "\n", encoding="utf-8")
    return lock


def build_manifest(
    root: Path,
    build_plan_path: Path,
    *,
    stack: str | None = None,
    feature: str | None = None,
    require_sequential_clear: bool = False,
    suggest: bool = False,
    write_lock: bool = False,
) -> dict:
    text = build_plan_path.read_text(encoding="utf-8")
    ctx = resolve_context(root, stack=stack, feature=feature)
    feat = feature or str(ctx.get("feature", ""))
    all_rows = parse_parallel_rows(text)
    agents_raw = agent_rows(all_rows)
    if feat:
        agents_raw = [r for r in agents_raw if _matches_feature(r, feat)]

    blockers: list[str] = []
    if require_sequential_clear:
        open_seq = sequential_agent_open(text)
        if open_seq:
            blockers.append(
                f"{len(open_seq)} open [AGENT] Sequential item(s) before Parallel lane"
            )

    agents: list[dict] = []
    labels = "ABCDEFGH"
    unresolved = False
    for idx, row in enumerate(agents_raw):
        if idx >= MAX_AGENTS:
            break
        try:
            scope = substitute_placeholders(row.scope, ctx)
        except ValueError as exc:
            blockers.append(str(exc))
            unresolved = True
            scope = row.scope
        agent_id = labels[idx] if idx < len(labels) else str(idx + 1)
        branch = f"feature/agent-{slugify(row.task)}"
        agents.append(
            {
                "id": agent_id,
                "task": row.task,
                "owner": row.owner,
                "scope": scope,
                "branch": branch,
                "sprint": row.sprint_title,
                "forbidden_paths": FORBIDDEN_PATHS,
            }
        )

    suggestions: list[dict] = []
    if suggest or len(agents) < 2:
        for row in suggest_rows(ctx):
            try:
                scope = substitute_placeholders(row.scope, ctx)
            except ValueError:
                scope = row.scope.replace("{feature}", "*")
            suggestions.append(
                {"task": row.task, "owner": row.owner, "scope": scope}
            )

    scopes = [a["scope"] for a in agents]
    overlaps = find_overlaps(scopes)
    if overlaps:
        blockers.extend(overlaps)

    agent_count = len(agents)
    split_hint: str | None = None
    if agent_count > MAX_AGENTS:
        split_hint = (
            f"Split sprint into sub-sprints; table implies {agent_count} agents "
            f"(max {MAX_AGENTS})"
        )
        blockers.append(split_hint)
        agents = agents[:MAX_AGENTS]
        agent_count = len(agents)

    ready = not blockers and agent_count > 0
    manifest = {
        "agent_count": agent_count,
        "ready": ready,
        "blockers": blockers,
        "agents": agents,
        "suggestions": suggestions,
        "split_hint": split_hint,
        "context": {"stack": ctx["stack"], "feature": ctx.get("feature", "")},
        "unresolved_placeholders": unresolved,
    }
    if write_lock and agents:
        write_lock_file(root, manifest)
    return manifest


def check_build_plan_parallel(
    build_plan_path: Path,
    root: Path | None = None,
    *,
    min_agents: int = 2,
) -> tuple[bool, list[str]]:
    text = build_plan_path.read_text(encoding="utf-8")
    repo_root = root or build_plan_path.parent
    ctx = resolve_context(repo_root)
    errors: list[str] = []
    blocks = parse_sprint_blocks(text)
    if not blocks:
        return True, []

    for block in blocks:
        body = "\n".join(block.lines)
        if "archived" in body.lower() and "🔲" not in body:
            continue
        if not PARALLEL_HEADER.search(body):
            if block.parallel_exception:
                continue
            if not re.search(r"🔲", body):
                continue
            errors.append(f"{block.title}: missing ### Parallel table")
            continue

        sprint_rows = parse_parallel_rows(body)
        agent_list = agent_rows(sprint_rows)
        scopes = []
        for row in agent_list:
            try:
                scopes.append(substitute_placeholders(row.scope, ctx))
            except ValueError:
                scopes.append(row.scope)
        overlaps = find_overlaps(scopes)
        errors.extend(f"{block.title}: {e}" for e in overlaps)

        exc = block.parallel_exception
        if exc and exc.lower() not in ("none", ""):
            continue
        if len(agent_list) < min_agents:
            errors.append(
                f"{block.title}: {len(agent_list)} AGENT Parallel row(s) "
                f"(need >= {min_agents} or <!-- parallel_exception: reason -->)"
            )
        if block.agent_count_target and block.agent_count_target > MAX_AGENTS:
            errors.append(
                f"{block.title}: agent_count_target {block.agent_count_target} exceeds max {MAX_AGENTS}"
            )

    return len(errors) == 0, errors
