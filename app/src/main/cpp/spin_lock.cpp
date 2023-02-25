#include <spin_lock.h>

namespace lotus {

    constexpr u64 yield_attempt_freq = 64;
    constexpr u64 sleep_attempt_freq = 256;
    constexpr u64 sleep_by_in_attempt = 2000; // 2ns sleep time

    template<typename T>
    void ensure_lock_stage(T &&normal_lock) {
        if (normal_lock()) return;
        const auto threadHandler = pthread_self();
        pthread_setname_np(threadHandler, "Trying to acquire the lock, please"
                                          " be patient like C-3PO!");

        // While a normal lock can't be performed, we will perform some wait operations!
        for (u64 attempt{}; !normal_lock(); attempt++) {
            if (attempt % yield_attempt_freq) {
                std::this_thread::yield(); continue;
            }
            // Now is time to sleep for some while
            if (attempt % sleep_attempt_freq) {
                std::this_thread::sleep_for(std::chrono::nanoseconds(sleep_by_in_attempt));
                continue;
            }
        }
    }

    void spin_lock::dirty_spin_lock() {
        ensure_lock_stage([this] {
            return this->try_lock();
        });
    }

    void SharedSpinLock::dirty_spin_lock() {
        ensure_lock_stage([this] {
            return this->try_lock();
        });
    }

    void SharedSpinLock::dirty_shared_spin_lock() {
        ensure_lock_stage([this] {
            return this->try_lock_shared();
        });
    }

}