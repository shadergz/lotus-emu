#pragma once

#include <mips/MIPS.h>

namespace lotus::console {

    class SCPH9000Generic {

        SCPH9000Generic() {
            m_dynamicCPU = std::make_unique<mips::MIPSXCore>();

        }

    private:
        std::unique_ptr<mips::MIPSXCore> m_dynamicCPU;
    };
}

