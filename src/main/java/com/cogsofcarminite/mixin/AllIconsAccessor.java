package com.cogsofcarminite.mixin;

import com.simibubi.create.foundation.gui.AllIcons;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AllIcons.class)
public interface AllIconsAccessor {
    @Accessor("iconX")
    int getIconX();

    @Accessor("iconY")
    int getIconY();
}
