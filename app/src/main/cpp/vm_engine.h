#pragma once

#include <thread>
#include <shared_mutex>
#include <mutex>

#include <common/core_space.h>
#include <spin_lock.h>

#include <scph_9000x.h>
#include <scph_xxxx.h>

namespace lotus::emulation {

    enum VMState {
        VM_THREAD_WAITING,
    };

    class VirtualMachine {

    public:
        boolean stop_signal();
        boolean exec_signal();

        VMState get_state() const {
            // Doesn't locks multiples thread execution, only blocks when an execution stage
            // is begin performed!
            std::shared_lock<SharedSpinLock> lock {m_vmLock};
            return m_intStatus;
        }

    private:
        mutable SharedSpinLock m_vmLock;
        VMState m_intStatus {VMState::VM_THREAD_WAITING};

        std::unique_ptr<console::SCPH9000Generic> m_console9000;
        std::unique_ptr<console::SCPHXXXXGeneric> m_consoleXXXX;
    };
}
