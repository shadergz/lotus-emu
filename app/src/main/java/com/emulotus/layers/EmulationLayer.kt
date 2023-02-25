package com.emulotus.layers

import com.emulotus.UserMachine
import com.emulotus.emu.VMState

object EmuLayer {

    private var virtualMachine: UserMachine = UserMachine()
    private val machineStatus: VMState = virtualMachine.machineState

    private val vmStateStrings = mapOf(
        VMState.Waiting to "Emulator is waiting"
    )
    fun getRecentlyStatus(): String? {
        return vmStateStrings[machineStatus]
    }
    // This function will be called future inside the "Emulator Activity"
    @Suppress("unused") fun activate() {
        virtualMachine.activate()
    }
}
