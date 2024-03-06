package com.cogsofcarminite.reg;

import com.cogsofcarminite.CogsOfCarminite;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CCCreativeModeTabs {
    private static final DeferredRegister<CreativeModeTab> TAB_REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CogsOfCarminite.MODID);

    public static RegistryObject<CreativeModeTab> MAIN_TAB = TAB_REGISTER.register("main", () ->
                CreativeModeTab.builder().title(Component.translatable("cogsofcarminite.item_group.main")).icon(() ->
                        new ItemStack(CCItems.FIERY_SHEET.get()))
                        .displayItems((itemDisplayParameters, output) -> {
                            output.accept(CCBlocks.DARK_TOWER_CASING);
                            output.accept(CCBlocks.MECHANICAL_TIMEWOOD_CLOCK);
                            output.accept(CCBlocks.MECHANICAL_HEART_OF_TRANSFORMATION);
                            output.accept(CCBlocks.MECHANICAL_MINEWOOD_CORE);
                            output.accept(CCBlocks.MECHANICAL_SORTINGWOOD_ENGINE);
                            output.accept(CCBlocks.HORNBLOWER);
                            output.accept(CCBlocks.MECHANICAL_ROOT_PULLER);
                            output.accept(CCItems.CARMINITE_GEAR_GADGET);
                            output.accept(CCItems.BLOCK_ATTRIBUTE_FILTER);
                            output.accept(CCItems.IRONWOOD_SHEET);
                            output.accept(CCItems.KNIGHTMETAL_SHEET);
                            output.accept(CCItems.FIERY_SHEET);
                            output.accept(CCItems.IRONWOOD_NUGGET);
                        })
                        .build());


    public static void register(IEventBus modEventBus) {
        TAB_REGISTER.register(modEventBus);
    }
}
