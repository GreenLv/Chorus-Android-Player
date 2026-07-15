#!/usr/bin/env bash
# Copyright 2026 Gerui Lv and Qingyue Tan. Licensed under Apache-2.0.
set -euo pipefail

cd "$(dirname "$0")/.."
failed=0

report() {
    printf 'release guard: %s\n' "$1" >&2
    failed=1
}

while IFS= read -r file; do
    case "$file" in
        */build/*|*/.gradle/*|*/.git/*|*/.cxx/*) continue ;;
        *.so|*.a|*.o|*.apk|*.aab|*.aar|*.pdf|*.docx|*.pptx|*.pcap|*.pcapng|*.keystore|*.jks|*.p12|*.pem|*.key|*.log|*.csv)
            report "forbidden file type: $file" ;;
    esac
done < <(find . -type f -print)

while IFS= read -r large_file; do
    report "unexpected file larger than 1 MiB: $large_file"
done < <(find . -type f -size +1M \
    ! -path './.git/*' ! -path '*/build/*' ! -path '*/.gradle/*' ! -path '*/.cxx/*' -print)

content_files=(
    --hidden
    --glob '!**/.git/**'
    --glob '!**/.gradle/**'
    --glob '!**/build/**'
    --glob '!**/.cxx/**'
    --glob '!scripts/verify-no-private-artifacts.sh'
)

if rg -n "${content_files[@]}" \
    '([0-9]{1,3}\.){3}[0-9]{1,3}|/Users/|/home/|/workspace/|[A-Za-z]:\\Users\\' .; then
    report 'infrastructure address or absolute path found'
fi

if rg -n \
    --glob '*.java' --glob '*.c' --glob '*.h' --glob '*.gradle' --glob '*.properties' \
    --glob '!**/src/test/**' --glob '!**/build/**' --glob '!**/.cxx/**' \
    "['\"]https?://|([0-9]{1,3}\\.){3}[0-9]{1,3}" .; then
    report 'hard-coded endpoint found in source or build configuration'
fi

if rg -n -i "${content_files[@]}" \
    '(password|passwd|access[_-]?token|secret[_-]?key)[[:space:]]*[:=][[:space:]]*[^[:space:]]+' .; then
    report 'possible credential assignment found'
fi

if rg -n "${content_files[@]}" 'BEGIN [A-Z ]*PRIVATE KEY' .; then
    report 'private-key material found'
fi

if [[ -d .git ]]; then
    while IFS= read -r tracked; do
        case "$tracked" in
            */.cxx/*|xquic/*|*/xquic/*|*/include/xquic/*|*/share_libs/*|*xquic*.h|*xquic*.c|*libxquic*)
                report "forbidden tracked native path: $tracked" ;;
            *.so|*.a|*.o|*.apk|*.aab|*.aar|*.pdf|*.docx|*.pptx|*.pcap|*.pcapng|*.log|*.csv)
                report "forbidden tracked file: $tracked" ;;
        esac
    done < <(git ls-files)
fi

if (( failed )); then
    exit 1
fi
printf 'release guard: no prohibited artifact or high-risk pattern found\n'
