/*
 * Copyright 2026 Gerui Lv and Qingyue Tan
 * Licensed under the Apache License, Version 2.0.
 */
#include "chorus_teki_backend.h"

#if defined(__GNUC__)
#define CHORUS_WEAK __attribute__((weak))
#else
#define CHORUS_WEAK
#endif

CHORUS_WEAK int32_t chorus_teki_backend_api_version(void) {
    return 0;
}

CHORUS_WEAK uint32_t chorus_teki_backend_capabilities(void) {
    return 0;
}

CHORUS_WEAK int32_t chorus_teki_send_client_qoe_info(
        int32_t chunk_index,
        int64_t expected_delivery_time_ms) {
    (void) chunk_index;
    (void) expected_delivery_time_ms;
    return -1;
}

CHORUS_WEAK void chorus_teki_set_server_qoe_callback(
        chorus_teki_server_qoe_callback callback,
        void *user_data) {
    (void) callback;
    (void) user_data;
}

CHORUS_WEAK void chorus_teki_backend_close(void) {}
