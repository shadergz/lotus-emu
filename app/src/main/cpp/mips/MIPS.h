#pragma once

#include <mips/dynamic_recompiler_aarchv8a.h>
#include <mips/machine_interpreter.h>

#include <mips/chip_r3000a.h>

namespace lotus::mips {

    class MIPSXCore {

    public:
        MIPSXCore() {
            m_mips_r300a = std::make_unique<CoreR3000A>();
        }

    private:
        std::unique_ptr<CoreR3000A> m_mips_r300a;

        std::unique_ptr<JitRecompiler> m_mips_recompiler;
        std::unique_ptr<MachineInterpreter> m_mips_interpreter;

    };

}