package com.cogsofcarminite.reg;

import com.cogsofcarminite.CogsOfCarminite;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CCCreativeModeTabs {
    private static final DeferredRegister<CreativeModeTab> TAB_REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CogsOfCarminite.MODID);

    public static RegistryObject<CreativeModeTab> MAIN_TAB;

    static {
        MAIN_TAB = TAB_REGISTER.register("main", () ->
                CreativeModeTab.builder().title(Component.translatable("cogsofcarminite.item_group.main")).icon(() ->
                        new ItemStack(CCItems.FIERY_SHEET.get())).displayItems((parameters, output) -> {
                            for (RegistryEntry<Item> entry : CogsOfCarminite.TWILIGHT_REGISTRATE.getAll(Registries.ITEM)) {
                                if (CreateRegistrate.isInCreativeTab(entry, MAIN_TAB)) output.accept(entry.get());
                            }
                        }).build());
    }

    public static void register(IEventBus modEventBus) {
        TAB_REGISTER.register(modEventBus);
    }
}
