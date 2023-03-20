#pragma once

#include <thread>
#include <atomic>

#include <arm_neon.h>
#include <common/core_space.h>

namespace lotus {
    /* Quick and dirt implementation of a simple spin locker (mutex) like system,
     * Could be used within std::shared_lock and std::unique_ptr
    */
    class [[maybe_unused]] spin_lock {
    public:
        [[maybe_unused]] void lock() {
            if (try_lock()) [[likely]] {
                return;
            }
            dirty_spin_lock();
        }
        boolean try_lock() {
            return commit();
        }

        inline boolean commit() {
            // Performing a real read-modify-write full operation at once, this will
            // Guaranties that the thread what read will perform all operations without
            // interrupting! (memory_order_acq_rel)
            return !m_spin.test_and_set(std::memory_order_acq_rel);
        }
        inline void cleanupState() {
            m_spin.clear(std::memory_order_release);
        }

        [[maybe_unused]] void unlock() {
            if (!try_unlock()) [[unlikely]] {
                std::abort();
            }
        }

        boolean try_unlock() {
            // Immediate returning (is already in unlocked stage)
            if (!m_spin.test()) [[unlikely]] {
                return false;
            }
            cleanupState();
            return true;
        }

    private:
        void dirty_spin_lock();
        std::atomic_flag m_spin;

    };

    class SharedSpinLock {
        static constexpr u64 sharedReadState {2};
        static constexpr u64 sharedWriteState {1};
    public:
        [[maybe_unused]] void lock() {
            if (try_lock()) [[likely]] {
                return;
            }
            // Sleep until the lock has performed!
            dirty_spin_lock();
            setup_thread_info();
        }

        [[maybe_unused]] void unlock() {
            if (!try_unlock()) [[unlikely]] {
                std::abort();
            }
        }

        boolean try_lock() {
            u64 should {sharedReadState };
            // Only locks if the lockable memory region has a (ZERO) 0 value of 64 bits
            return m_state.compare_exchange_strong(should, sharedWriteState,
                                                   std::memory_order_acq_rel);
        }
        bool try_unlock() {
            // This will be called when a std::unique_lock begin destroyed!
            // Because of std::unique_lock will only acquire the lock when there isn't a
            // Read/Write lock! This means that sharedWriteState will be stored inside the
            // Atomic value region!
            if (m_state.load(std::memory_order_consume) & sharedWriteState) {
                m_state.fetch_and(~sharedWriteState, std::memory_order_release);
                return true;
            }

            return false;
        }

        [[maybe_unused]] void lock_shared() {
            if (try_lock_shared()) [[likely]] {
                return;
            }
            dirty_shared_spin_lock();
            setup_thread_info();
        }
        inline void setup_thread_info() {
            pthread_setname_np(pthread_self(), "Spin locker acquired!");
        }

        [[maybe_unused]] bool try_lock_shared() {
            // This method is called everytime that a non-write operation is needed!
            u64 persistentValue { m_state.load(std::memory_order_consume) };
            if (persistentValue & sharedWriteState) [[unlikely]] {
                // Shared locks mustn't allow already locked flags with write operation begin
                // locked again!
                return false;
            }
            return !(m_state.fetch_add(sharedReadState, std::memory_order_acquire)
                     & sharedWriteState);
        }

        [[maybe_unused]] void unlock_shared() {
            m_state.fetch_add(-sharedReadState, std::memory_order_release);
        }
    private:
        void dirty_spin_lock();
        void dirty_shared_spin_lock();

        std::atomic<u64> m_state {};
    };
}

