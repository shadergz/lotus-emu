#include <jni.h>

#include <vm_engine.h>

#define FUN_JNI_INT extern "C" JNIEXPORT lotus::ji32 JNICALL
#define FUN_JNI_BOOL extern "C" JNIEXPORT lotus::boolean JNICALL

static std::unique_ptr<lotus::emulation::VirtualMachine> g_vmcore;

FUN_JNI_INT Java_com_emulotus_UserMachine_00024Companion_startsVirtualMachine(
        [[maybe_unused]] JNIEnv* env, [[maybe_unused]] jobject thiz) {
    if (g_vmcore != nullptr) {
        return -1;
    }
    g_vmcore = std::make_unique<lotus::emulation::VirtualMachine>();
    return 0;
}

FUN_JNI_INT Java_com_emulotus_UserMachine_00024Companion_finalizeVirtualMachine(
        [[maybe_unused]] JNIEnv* env, [[maybe_unused]] jobject thiz) {
    if (!g_vmcore->stop_signal()) {
        return -1;
    }

    return 0;
}
FUN_JNI_BOOL Java_com_emulotus_UserMachine_stopWhenCheck(
        [[maybe_unused]] JNIEnv* env, [[maybe_unused]] jobject thiz) {
    return g_vmcore->stop_signal();
}

FUN_JNI_BOOL Java_com_emulotus_UserMachine_activate(
        [[maybe_unused]] JNIEnv* env, [[maybe_unused]] jobject thiz) {
    return g_vmcore->exec_signal();
}

FUN_JNI_INT Java_com_emulotus_UserMachine_00024Companion_fetchMachineState(
        JNIEnv* env, [[maybe_unused]] jobject thiz) {
    // We need to return a VMState as an integer, for this we must acquire the actual machine
    // status at runtime!
    env->ExceptionClear();

    lotus::ji32 stateValue = {static_cast<lotus::ji32>(g_vmcore->get_state())};
    return stateValue;
}
