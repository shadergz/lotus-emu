package com.emulotus

import com.emulotus.emu.VMState

class UserMachine {

    init {
        startsVirtualMachine()
    }

    external fun activate(): Boolean
    external fun stopWhenCheck(): Boolean

    var machineState: VMState = VMState.Waiting
        get() {
            var state = VMState.Waiting
            when (fetchMachineState()) {
                0 -> state = VMState.Waiting
            }
            return state
        }
        private set

    companion object {
        private external fun fetchMachineState(): Int

        private external fun startsVirtualMachine(): Int
        private external fun finalizeVirtualMachine(): Int
    }
}