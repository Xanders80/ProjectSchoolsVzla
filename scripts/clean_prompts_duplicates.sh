#!/usr/bin/env bash
set -euo pipefail

# Simple dedup script for prompts files based on content hash
# Robust version for various Bash versions
ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
if [[ $# -ge 1 && -d "$1" ]]; then
  ROOT_DIR="$1"
fi
echo "Root: $ROOT_DIR"

declare -A hash_map
duplicates=()

while IFS= read -r -d '' file; do
  if [[ ! -f "$file" ]]; then
    continue
  fi
  hash=$(md5sum "$file" | awk '{print $1}')
  if [[ -n ${hash_map[$hash]:-} ]]; then
    duplicates+=("$file -> ${hash_map[$hash]}")
  else
    hash_map[$hash]=$file
  fi
done < <(find "$ROOT_DIR" -type f \( -name "*.md" -o -name "*.yaml" \) -print0)

if [ ${#duplicates[@]} -eq 0 ]; then
  echo "No duplicates found."
  exit 0
fi

echo "Found duplicates (new -> existing):"
for d in "${duplicates[@]}"; do
  echo "$d"
done

echo "Optionally, delete duplicates manually or keep the most recent."
