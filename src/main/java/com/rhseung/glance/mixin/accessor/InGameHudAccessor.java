package com.rhseung.glance.mixin.accessor;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(InGameHud.class)
public interface InGameHudAccessor {
    @Accessor
    int getTicks();

    @Accessor
    Random getRandom();
}
