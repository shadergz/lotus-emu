#pragma once

#include <mips/dynamic_recompiler_aarchv8a.h>
#include <mips/machine_interpreter.h>

#include <mips/chip_r3000a.h>

namespace lotus::mips {

    class MIPSXCore {

    public:
        MIPSXCore() {
            m_mipsR300A = std::make_unique<CoreR3000A>();
        }

    private:
        std::unique_ptr<CoreR3000A> m_mipsR300A;

        std::unique_ptr<JitCompiler> m_mipsCompiler;
        std::unique_ptr<MachineInterpreter> m_mipsInterpreter;

    };

}