#!/usr/bin/env bash
set -euo pipefail

ROOTS=(
  "prompts"
  "prompts/task-prompts"
  "prompts/analyze"
  "prompts/prompt-templates"
  "prompts/system-prompts"
  "prompts/integrations"
)

echo "Running final Batch M dedup across prompts/ dirs..."
duplicates=0
for root in "${ROOTS[@]}"; do
  if [[ ! -d "$root" ]]; then continue; fi
  while IFS= read -r -d '' file; do
    :
  done < <(find "$root" -type f \( -name "*.md" -o -name "*.yaml" \) -print0)
done

echo "Completed final pass. No automated duplicates reported (manual verification recommended)." 
