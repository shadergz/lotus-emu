
#include <vm_engine.h>

namespace lotus::emulation {

    bool VirtualMachine::stop_signal() {
        std::lock_guard<SharedSpinLock> auto_lock(m_vmLock);
        return true;
    }
    bool VirtualMachine::exec_signal() {
        std::lock_guard<SharedSpinLock> auto_lock(m_vmLock);
        return true;
    }

}
