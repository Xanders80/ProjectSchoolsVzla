#!/usr/bin/env bash
set -euo pipefail

# Simple text-based dedup across prompts-related directories using md5 hash

ROOTS=(
  "prompts"
  "prompts/task-prompts"
  "prompts/analyze"
  "prompts/prompt-templates"
  "prompts/system-prompts"
  "prompts/integrations"
)

HASH_FILE="prompts_hashes.txt"
rm -f "$HASH_FILE"
touch "$HASH_FILE"

duplicates=()

for root in "${ROOTS[@]}"; do
  if [[ ! -d "$root" ]]; then
    continue
  fi
  while IFS= read -r -d '' file; do
    if [[ ! -f "$file" ]]; then
      continue
    fi
    hash=$(md5sum "$file" | awk '{print $1}')
    if grep -q -F "$hash\t" "$HASH_FILE"; then
      # Retrieve the first occurrence path for the hash
      first=$(grep -F "$hash\t" "$HASH_FILE" | head -n1 | cut -f2 -d'\t')
      duplicates+=("$file -> $first")
    else
      echo -e "$hash\t$file" >> "$HASH_FILE"
    fi
  done < <(find "$root" -type f \( -name "*.md" -o -name "*.yaml" \) -print0)
done

if [ ${#duplicates[@]} -eq 0 ]; then
  echo "No duplicates found across batch prompts (2)."
  exit 0
fi

echo "Found duplicates (new -> existing):"
for d in "${duplicates[@]}"; do
  echo "$d"
done

echo "End of batch 2 dedup. To remove, run with a flag to delete duplicates."
