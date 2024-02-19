package com.cogsofcarminite.items;

import com.cogsofcarminite.client.renderers.items.CarminiteMagicLogItemRenderer;
import com.cogsofcarminite.reg.CCPartialBlockModels;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CarminiteGearGadgetItem extends Item {
    public CarminiteGearGadgetItem(Properties properties) {
        super(properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new CarminiteMagicLogItemRenderer(CCPartialBlockModels.CARMINITE_FLYWHEEL)));
    }
}
