#!/usr/bin/env sh
#
# code-generation.sh
# Builds the ts-generator annotation processor, then compiles the main project
# so the processor emits Angular TypeScript services/models/enums into
# src/app/shared/api/.
#
# Usage:  ./ts-generator/src/main/java/com/graze/tsgen/code-generation.sh
#         (run from the project root, or invoke directly — path is auto-resolved)

set -eu

# ── Resolve project root (directory containing pom.xml) ─────────────
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# First, try to use the Git repository root if available.
if command -v git >/dev/null 2>&1; then
  GIT_ROOT="$(git -C "$SCRIPT_DIR" rev-parse --show-toplevel 2>/dev/null || true)"
  if [ -n "${GIT_ROOT:-}" ] && [ -f "$GIT_ROOT/pom.xml" ]; then
    PROJECT_ROOT="$GIT_ROOT"
  fi
fi

# If Git is unavailable or unsuitable, walk up from SCRIPT_DIR to find pom.xml.
if [ -z "${PROJECT_ROOT:-}" ]; then
  SEARCH_DIR="$SCRIPT_DIR"
  while [ "$SEARCH_DIR" != "/" ]; do
    if [ -f "$SEARCH_DIR/pom.xml" ]; then
      PROJECT_ROOT="$SEARCH_DIR"
      break
    fi
    SEARCH_DIR="$(dirname "$SEARCH_DIR")"
  done
fi

if [ -z "${PROJECT_ROOT:-}" ]; then
  echo "❌  Could not locate project root (pom.xml not found above $SCRIPT_DIR)"
  exit 1
fi

cd "$PROJECT_ROOT"
echo "📂  Project root: $PROJECT_ROOT"

# ── 1. Build & install the ts-generator annotation processor ────────
echo ""
echo "🔧  Building ts-generator annotation processor…"
./mvnw -f ts-generator/pom.xml clean install -q
echo "✅  ts-generator installed to local Maven repo"

# ── 2. Compile the main project (triggers TS code generation) ───────
echo ""
echo "⚙️   Compiling main project (TypeScript generation runs during compile)…"
./mvnw clean compile 2>&1
echo ""
echo "✅  Code generation complete — check src/app/shared/api/"
