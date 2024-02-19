package com.cogsofcarminite;

import com.cogsofcarminite.reg.CCItems;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber(modid = CogsOfCarminite.MODID)
public class EventListener {
    @SubscribeEvent
    public static void onBlockActivated(PlayerInteractEvent.RightClickBlock event) {
        if (true) return;

        if (CCItems.CARMINITE_GEAR_GADGET.isIn(event.getItemStack()))
            return;

        BlockState state = event.getLevel()
                .getBlockState(event.getPos());

        BlockHitResult blockTrace =
                new BlockHitResult(VecHelper.getCenterOf(event.getPos()), event.getFace(), event.getPos(), true);
        InteractionResult result = state.use(event.getLevel(), event.getEntity(), event.getHand(), blockTrace);

        if (!result.consumesAction())
            return;

        event.setCanceled(true);
        event.setCancellationResult(result);
    }
}
