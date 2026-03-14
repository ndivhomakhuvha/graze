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
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../../../../../../.." && pwd)"

if [ ! -f "$PROJECT_ROOT/pom.xml" ]; then
  echo "❌  Could not locate project root (expected pom.xml in $PROJECT_ROOT)"
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
