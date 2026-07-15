/*
 * Copyright 2026 Gerui Lv and Qingyue Tan
 * Licensed under the Apache License, Version 2.0.
 */
#ifndef CHORUS_TEKI_BACKEND_H
#define CHORUS_TEKI_BACKEND_H

#include <stdint.h>

#define CHORUS_TEKI_API_VERSION 1

#define CHORUS_TEKI_CAPABILITY_HTTP3 (1u << 0)
#define CHORUS_TEKI_CAPABILITY_MULTIPATH (1u << 1)
#define CHORUS_TEKI_CAPABILITY_CLIENT_QOE (1u << 2)
#define CHORUS_TEKI_CAPABILITY_SERVER_QOE (1u << 3)

typedef void (*chorus_teki_server_qoe_callback)(
        int64_t sequence_number,
        int32_t fast_path_index,
        int32_t slow_path_index,
        double fast_path_receive_mbps,
        double slow_path_receive_mbps,
        double fast_path_rtt_ms,
        double slow_path_rtt_ms,
        double fast_path_ratio,
        int64_t received_at_elapsed_ms,
        void *user_data);

/*
 * These functions are implemented by the local stub. An authorized external backend may replace
 * them with strong definitions without changing the public Java/JNI boundary.
 */
int32_t chorus_teki_backend_api_version(void);
uint32_t chorus_teki_backend_capabilities(void);
int32_t chorus_teki_send_client_qoe_info(
        int32_t chunk_index,
        int64_t expected_delivery_time_ms);
void chorus_teki_set_server_qoe_callback(
        chorus_teki_server_qoe_callback callback,
        void *user_data);
void chorus_teki_backend_close(void);

#endif
