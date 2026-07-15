/*
 * Copyright 2026 Gerui Lv and Qingyue Tan
 * Licensed under the Apache License, Version 2.0.
 */
#include <jni.h>
#include <pthread.h>

#include "chorus_teki_backend.h"

static JavaVM *g_vm;
static jobject g_callback_target;
static pthread_mutex_t g_callback_mutex = PTHREAD_MUTEX_INITIALIZER;

static void publish_server_qoe(
        int64_t sequence_number,
        int32_t fast_path_index,
        int32_t slow_path_index,
        double fast_path_receive_mbps,
        double slow_path_receive_mbps,
        double fast_path_rtt_ms,
        double slow_path_rtt_ms,
        double fast_path_ratio,
        int64_t received_at_elapsed_ms,
        void *user_data) {
    (void) user_data;
    if (g_vm == NULL) return;

    JNIEnv *env = NULL;
    int detach = 0;
    jint state = (*g_vm)->GetEnv(g_vm, (void **) &env, JNI_VERSION_1_6);
    if (state == JNI_EDETACHED) {
        if ((*g_vm)->AttachCurrentThread(g_vm, &env, NULL) != JNI_OK) return;
        detach = 1;
    } else if (state != JNI_OK) {
        return;
    }

    pthread_mutex_lock(&g_callback_mutex);
    jobject target = g_callback_target == NULL
            ? NULL
            : (*env)->NewLocalRef(env, g_callback_target);
    pthread_mutex_unlock(&g_callback_mutex);

    if (target != NULL) {
        jclass target_class = (*env)->GetObjectClass(env, target);
        jmethodID method = target_class == NULL
                ? NULL
                : (*env)->GetMethodID(
                        env,
                        target_class,
                        "onServerPathStats",
                        "(JIIDDDDDJ)V");
        if (target_class != NULL && method != NULL) {
            (*env)->CallVoidMethod(
                    env,
                    target,
                    method,
                    (jlong) sequence_number,
                    (jint) fast_path_index,
                    (jint) slow_path_index,
                    (jdouble) fast_path_receive_mbps,
                    (jdouble) slow_path_receive_mbps,
                    (jdouble) fast_path_rtt_ms,
                    (jdouble) slow_path_rtt_ms,
                    (jdouble) fast_path_ratio,
                    (jlong) received_at_elapsed_ms);
        }
        if ((*env)->ExceptionCheck(env)) (*env)->ExceptionClear(env);
        if (target_class != NULL) (*env)->DeleteLocalRef(env, target_class);
        (*env)->DeleteLocalRef(env, target);
    }

    if (detach) (*g_vm)->DetachCurrentThread(g_vm);
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    (void) reserved;
    g_vm = vm;
    return JNI_VERSION_1_6;
}

JNIEXPORT jint JNICALL
Java_org_chorusresearch_player_transport_teki_NativeTekiBridge_nativeBackendApiVersion(
        JNIEnv *env,
        jclass clazz) {
    (void) env;
    (void) clazz;
    return (jint) chorus_teki_backend_api_version();
}

JNIEXPORT jint JNICALL
Java_org_chorusresearch_player_transport_teki_NativeTekiBridge_nativeCapabilityFlags(
        JNIEnv *env,
        jclass clazz) {
    (void) env;
    (void) clazz;
    return (jint) chorus_teki_backend_capabilities();
}

JNIEXPORT jint JNICALL
Java_org_chorusresearch_player_transport_teki_NativeTekiBridge_nativeSendClientQoe(
        JNIEnv *env,
        jclass clazz,
        jint chunk_index,
        jlong expected_delivery_time_ms) {
    (void) env;
    (void) clazz;
    return (jint) chorus_teki_send_client_qoe_info(
            (int32_t) chunk_index,
            (int64_t) expected_delivery_time_ms);
}

JNIEXPORT void JNICALL
Java_org_chorusresearch_player_transport_teki_NativeTekiBridge_nativeSetCallbackTarget(
        JNIEnv *env,
        jclass clazz,
        jobject target) {
    (void) clazz;
    pthread_mutex_lock(&g_callback_mutex);
    if (g_callback_target != NULL) (*env)->DeleteGlobalRef(env, g_callback_target);
    g_callback_target = target == NULL ? NULL : (*env)->NewGlobalRef(env, target);
    pthread_mutex_unlock(&g_callback_mutex);
    chorus_teki_set_server_qoe_callback(publish_server_qoe, NULL);
}

JNIEXPORT void JNICALL
Java_org_chorusresearch_player_transport_teki_NativeTekiBridge_nativeClose(
        JNIEnv *env,
        jclass clazz) {
    (void) clazz;
    chorus_teki_set_server_qoe_callback(NULL, NULL);
    chorus_teki_backend_close();
    pthread_mutex_lock(&g_callback_mutex);
    if (g_callback_target != NULL) (*env)->DeleteGlobalRef(env, g_callback_target);
    g_callback_target = NULL;
    pthread_mutex_unlock(&g_callback_mutex);
}
