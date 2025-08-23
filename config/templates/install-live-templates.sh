#!/usr/bin/env bash
set -euo pipefail

SRC_DIR="${1:-$(cd "$(dirname "$0")/liveTemplates" && pwd)}"
AS_CONFIG_DIR_FROM_ARG="${2:-}"

if [ ! -d "$SRC_DIR" ]; then
  echo "Templates dir not found: $SRC_DIR"
  exit 1
fi

if [ -n "${AS_CONFIG_DIR:-}" ] || [ -n "$AS_CONFIG_DIR_FROM_ARG" ]; then
  TARGET_IDE_DIR="${AS_CONFIG_DIR:-$AS_CONFIG_DIR_FROM_ARG}"
  DEST_DIR="$TARGET_IDE_DIR/templates"
  mkdir -p "$DEST_DIR"
  copied=0
  shopt -s nullglob
  for f in "$SRC_DIR"/*.xml; do
    cp -f "$f" "$DEST_DIR/"
    echo "Installed: $(basename "$f") → $DEST_DIR"
    copied=$((copied+1))
  done
  [ "$copied" -gt 0 ] || { echo "No .xml files in $SRC_DIR"; exit 1; }
  echo; echo "✅ Live Templates installed to: $DEST_DIR"
  echo "↻ Please restart Android Studio (or Invalidate Caches) to see them."
  exit 0
fi

unameOut="$(uname -s)"
case "${unameOut}" in
  Darwin)
    BASE="$HOME/Library/Application Support/Google"
    CANDIDATE_PATTERNS=(
      "$BASE/AndroidStudio"*
      "$BASE/AndroidStudioPreview"*
      "$BASE/AndroidStudioBeta"*
      "$BASE/AndroidStudioCanary"*
    )
    ;;
  Linux)
    BASE="$HOME/.config/Google"
    CANDIDATE_PATTERNS=("$BASE/AndroidStudio"*)
    ;;
  MINGW*|MSYS*|CYGWIN*)
    USERNAME_WIN="${USERNAME:-${USER:-}}"
    BASE="/c/Users/${USERNAME_WIN}/AppData/Roaming/Google"
    CANDIDATE_PATTERNS=("$BASE/AndroidStudio"*)
    ;;
  *)
    echo "Unsupported OS: ${unameOut}"
    exit 1
    ;;
esac

shopt -s nullglob
CANDIDATES=()
for pat in "${CANDIDATE_PATTERNS[@]}"; do
  while IFS= read -r match; do
    [ -d "$match" ] && CANDIDATES+=("$match")
  done < <(compgen -G "$pat" || true)
done

if [ ${#CANDIDATES[@]} -eq 0 ]; then
  echo "Android Studio config dir not found under $BASE"
  echo "Hint: run Android Studio once so it creates the config directory,"
  echo "or pass it explicitly via env AS_CONFIG_DIR=\"/path/...\""
  exit 1
fi

TARGET_IDE_DIR="$(printf '%s\n' "${CANDIDATES[@]}" | sort -V | tail -n 1)"
DEST_DIR="$TARGET_IDE_DIR/templates"
mkdir -p "$DEST_DIR"

echo "Using Android Studio config: $TARGET_IDE_DIR"

copied=0
for f in "$SRC_DIR"/*.xml; do
  cp -f "$f" "$DEST_DIR/"
  echo "Installed: $(basename "$f") → $DEST_DIR"
  copied=$((copied+1))
done

[ "$copied" -gt 0 ] || { echo "No .xml files in $SRC_DIR"; exit 1; }

echo
echo "✅ Live Templates installed to: $DEST_DIR"
echo "↻ Please restart Android Studio (or Invalidate Caches) to see them."
