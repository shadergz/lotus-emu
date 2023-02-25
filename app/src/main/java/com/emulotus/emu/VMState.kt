package com.emulotus.emu

@Suppress("unused")
sealed class VMState(val vmState: Int) {
    object Waiting: VMState(0)
}

