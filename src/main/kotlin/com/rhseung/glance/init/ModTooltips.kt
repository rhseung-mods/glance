package com.rhseung.glance.init

import com.rhseung.glance.tooltip.AttributeTooltip
import com.rhseung.glance.tooltip.FoodTooltip
import com.rhseung.glance.tooltip.FuelTooltip
import com.rhseung.glance.tooltip.base.CompoundTooltip

object ModTooltips {
    fun register() {
        CompoundTooltip.register();
        AttributeTooltip.register();
        FoodTooltip.register();
        FuelTooltip.register();
    }
}