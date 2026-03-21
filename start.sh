#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
RUN_DIR="$ROOT_DIR/.run"
LOG_DIR="$RUN_DIR/logs"
PID_DIR="$RUN_DIR/pids"

mkdir -p "$LOG_DIR" "$PID_DIR"

# Free listen port before start (Spring default 8080; override with SERVER_PORT)
FREE_PORT="${SERVER_PORT:-8080}"
if command -v lsof >/dev/null 2>&1; then
  PIDS="$(lsof -ti TCP:"$FREE_PORT" -sTCP:LISTEN 2>/dev/null || true)"
  if [ -n "$PIDS" ]; then
    echo "==> Port $FREE_PORT in use, stopping listener(s): $PIDS"
    # shellcheck disable=SC2086
    kill $PIDS 2>/dev/null || true
    sleep 1
    PIDS="$(lsof -ti TCP:"$FREE_PORT" -sTCP:LISTEN 2>/dev/null || true)"
    if [ -n "$PIDS" ]; then
      echo "==> Force kill on port $FREE_PORT: $PIDS"
      # shellcheck disable=SC2086
      kill -9 $PIDS 2>/dev/null || true
    fi
  fi
else
  echo "WARN: lsof not found; skip port cleanup for $FREE_PORT" >&2
fi

echo "==> Building backend (reactor package)..."

if ! command -v mvn >/dev/null 2>&1; then
  echo "ERROR: mvn not found. Please install Maven." >&2
  exit 1
fi

# Compile & package in a reactor so the starter module is available
rm -f "$LOG_DIR/backend-build.log"
mvn -DskipTests -pl backend -am package 2>&1 | tee "$LOG_DIR/backend-build.log"

# Pick the first jar produced by backend packaging
BACKEND_JAR=""
shopt -s nullglob
for f in "$ROOT_DIR/backend/target/"*.jar; do
  BACKEND_JAR="$f"
  break
done
shopt -u nullglob

if [ -z "$BACKEND_JAR" ]; then
  echo "ERROR: backend jar not found in backend/target" >&2
  exit 1
fi

# Run with backend/ as CWD so App.java's dotenv loader picks up backend/.env.
nohup bash -c "cd \"$ROOT_DIR/backend\" && java -jar \"$BACKEND_JAR\"" >"$LOG_DIR/backend.log" 2>&1 &
echo $! >"$PID_DIR/backend.pid"

echo "backend pid: $(cat "$PID_DIR/backend.pid")"
echo "backend log: $LOG_DIR/backend.log"

APP_PORT="${SERVER_PORT:-8080}"
BASE_URL="http://127.0.0.1:${APP_PORT}"
{
  echo "swagger ui: ${BASE_URL}/swagger-ui/index.html"
  echo "openapi all: ${BASE_URL}/v3/api-docs"
  echo "openapi lark: ${BASE_URL}/v3/api-docs/lark"
} | tee -a "$LOG_DIR/backend.log"

echo
echo "==> Following backend logs (Ctrl+C to stop following)..."
tail -n 200 -f "$LOG_DIR/backend.log"

