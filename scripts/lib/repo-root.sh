#!/usr/bin/env bash
# Shared helpers for Git Bash (MSYS) + native Windows Python.
# Source after ROOT="$(cd ... && pwd)".

repo_root_for_python() {
  local p="${1:-${ROOT:-.}}"
  if command -v cygpath >/dev/null 2>&1; then
    cygpath -m "$p"
    return 0
  fi
  if [[ "$p" =~ ^/([a-zA-Z])/(.*)$ ]]; then
    local drive rest
    drive="$(echo "${BASH_REMATCH[1]}" | tr '[:lower:]' '[:upper:]')"
    rest="${BASH_REMATCH[2]}"
    echo "${drive}:/${rest}"
    return 0
  fi
  echo "$p"
}
