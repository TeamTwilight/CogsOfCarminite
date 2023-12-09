package com.cogsofcarminite;

import com.cogsofcarminite.reg.CCBlocks;
import com.simibubi.create.CreateClient;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber(modid = CogsOfCarminite.MODID)
public class EventListener {

    @SubscribeEvent
    public static void entityHurts(LivingEntityUseItemEvent.Start event) {
        CogsOfCarminite.LOGGER.warn("ITEM USED");
        if (event.getEntity().level().isClientSide) {
            CogsOfCarminite.LOGGER.warn("SHIT IS NULL = {}!", (CreateClient.CASING_CONNECTIVITY.get(CCBlocks.DARK_TOWER_CASING.getDefaultState()) == null));
        }
    }
}
