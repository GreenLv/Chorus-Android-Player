#!/usr/bin/env bash
# Copyright 2026 Gerui Lv and Qingyue Tan. Licensed under Apache-2.0.
set -euo pipefail

root=${1:-}
abi=${2:-arm64-v8a}

if [[ -z "$root" ]]; then
    printf 'usage: %s PROVIDER_ROOT [ABI]\n' "$0" >&2
    exit 2
fi

library="$root/jniLibs/$abi/libxquic.so"
include="$root/include"

[[ -d "$include" ]] || { printf 'missing provider include directory: %s\n' "$include" >&2; exit 1; }
[[ -f "$library" ]] || { printf 'missing provider library for ABI %s: %s\n' "$abi" "$library" >&2; exit 1; }

case "$abi" in
    arm64-v8a) expected='arm64|aarch64' ;;
    armeabi-v7a) expected='arm|32-bit' ;;
    *) printf 'unsupported audited ABI: %s\n' "$abi" >&2; exit 2 ;;
esac

description=$(file "$library")
printf '%s\n' "$description"
printf '%s\n' "$description" | grep -Eiq "$expected" || {
    printf 'library architecture does not match %s\n' "$abi" >&2
    exit 1
}

symbol_tool=''
command -v llvm-nm >/dev/null && symbol_tool='llvm-nm -D'
[[ -n "$symbol_tool" ]] || { command -v nm >/dev/null && symbol_tool='nm -g'; }
[[ -n "$symbol_tool" ]] || { printf 'nm/llvm-nm is required for symbol validation\n' >&2; exit 1; }

symbols=$($symbol_tool "$library" 2>/dev/null || true)
for required in chorus_transport_api_version chorus_transport_send_client_qoe chorus_transport_set_server_qoe_callback; do
    printf '%s\n' "$symbols" | grep -q "$required" || {
        printf 'missing required provider symbol: %s\n' "$required" >&2
        exit 1
    }
done

printf 'provider layout, ABI, and public contract symbols verified\n'
