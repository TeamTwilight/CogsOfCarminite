package com.cogsofcarminite.reg;

import com.cogsofcarminite.CogsOfCarminite;
import com.cogsofcarminite.client.menus.BlockAttributeFilterMenu;
import com.cogsofcarminite.client.menus.BlockAttributeFilterScreen;
import com.tterrag.registrate.util.entry.MenuEntry;
import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings({"SameParameterValue"})
public class CCMenus {
    public static final MenuEntry<BlockAttributeFilterMenu> BLOCK_ATTRIBUTE_FILTER = CogsOfCarminite.TWILIGHT_REGISTRATE.menu("block_attribute_filter", BlockAttributeFilterMenu::new,
            () -> BlockAttributeFilterScreen::new
    ).register();

    public static void register() {}
}
