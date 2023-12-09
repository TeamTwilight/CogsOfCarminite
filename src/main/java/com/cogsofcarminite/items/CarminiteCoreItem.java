package com.cogsofcarminite.items;

import com.cogsofcarminite.reg.CCPartialBlockModels;
import com.jozufozu.flywheel.core.PartialModel;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CarminiteCoreItem extends CarminiteMagicLogBlockItem {
    public CarminiteCoreItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public PartialModel getPartialModel() {
        return CCPartialBlockModels.CORE_FLYWHEEL_OFF;
    }
}
