package com.rhseung.glance.hud.crosshair

abstract class CrosshairData(val maxTicks: Int) {
    var tick: Int = 0;

    open fun onTick() {
        if (tick++ >= maxTicks)
            tick = maxTicks;
    }

    fun isFinished() = tick >= maxTicks;
}