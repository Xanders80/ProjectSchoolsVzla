#!/usr/bin/env bash
set -euo pipefail

# Batch dedup for OpenCode prompts across multiple directories
# Includes: prompts, prompts/task-prompts, prompts/analyze, prompts/prompt-templates, prompts/system-prompts, prompts/integrations

ROOTS=(
  "prompts"
  "prompts/task-prompts"
  "prompts/analyze"
  "prompts/prompt-templates"
  "prompts/system-prompts"
  "prompts/integrations"
)

KEEP=""
DRY_RUN=true
while [[ "$#" -gt 0 ]]; do
  key="$1"
  case "$key" in
    --no-dry-run) DRY_RUN=false; shift ;;
    --keep) KEEP="$2"; shift 2;;
    --dry-run) DRY_RUN=true; shift;;
    -h|--help) echo "Usage: $0 [--no-dry-run]"; exit 0;;
    *) break;;
  esac
done

declare -A seen
declare -a duplicates

for root in "${ROOTS[@]}"; do
  if [[ ! -d "$root" ]]; then
    continue
  fi
  while IFS= read -r -d '' file; do
    [[ -f "$file" ]] || continue
    hash=$(md5sum "$file" | awk '{print $1}')
    if [[ -n "${seen[$hash]}" ]]; then
      duplicates+=("$file|${seen[$hash]}")
    else
      seen[$hash]="$file"
    fi
  done < <(find "$root" -type f \( -name "*.md" -o -name "*.yaml" \) -print0)
done

if [ ${#duplicates[@]} -eq 0 ]; then
  echo "No duplicates found across batch prompts."
  exit 0
fi

echo "Found duplicates (new|existing):"
for d in "${duplicates[@]}"; do
  IFS='|' read -r new old <<< "$d"
  echo "$new -> $old"
done

if [ "$DRY_RUN" = true ]; then
  echo "Dry run: no files have been removed. Re-run with --no-dry-run to delete duplicates."
  exit 0
fi

# Actual deletion: keep first occurrence, remove others that match hash
for d in "${duplicates[@]}"; do
  IFS='|' read -r new old <<< "$d"
  if [[ -f "$new" && -f "$old" ]]; then
    rm -f "$new" || true
    echo "Deleted duplicate: $new"
  fi
done

echo "Batch dedup complete."
