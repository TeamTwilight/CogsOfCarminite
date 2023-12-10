package com.cogsofcarminite;

import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber(modid = CogsOfCarminite.MODID)
public class EventListener {

    @SubscribeEvent
    public static void entityHurts(LivingEntityUseItemEvent.Start event) {
        
    }
}
