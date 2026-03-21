#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PID_DIR="$ROOT_DIR/.run/pids"

kill_pid_file() {
  local name="$1"
  local file="$PID_DIR/$name.pid"
  if [ -f "$file" ]; then
    local pid
    pid="$(cat "$file" || true)"
    if [ -n "${pid:-}" ] && kill -0 "$pid" >/dev/null 2>&1; then
      echo "==> Stopping $name (pid=$pid)"
      kill "$pid" || true
    else
      echo "==> $name already stopped (pid=${pid:-?})"
    fi
    rm -f "$file"
  else
    echo "==> No pid file for $name"
  fi
}

kill_pid_file backend
kill_pid_file frontend

echo "==> Done."

