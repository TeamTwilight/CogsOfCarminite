package com.cogsofcarminite.items;

import com.cogsofcarminite.client.renderers.items.CarminiteMagicLogItemRenderer;
import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class CarminiteMagicLogBlockItem extends BlockItem {
    public CarminiteMagicLogBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @OnlyIn(Dist.CLIENT)
    public abstract PartialModel getPartialModel();

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new CarminiteMagicLogItemRenderer(this.getPartialModel())));
    }
}
