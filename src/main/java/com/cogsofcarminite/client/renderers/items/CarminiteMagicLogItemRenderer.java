package com.cogsofcarminite.client.renderers.items;

import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CarminiteMagicLogItemRenderer extends CustomRenderedItemModelRenderer {
    protected final PartialModel model;

    public CarminiteMagicLogItemRenderer(PartialModel model) {
        this.model = model;
    }

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        renderer.render(model.getOriginalModel(), light);

        float offset = 0.5f / 16.0F;
        float worldTime = AnimationTickHolder.getRenderTime() / 10.0F;
        float angle = worldTime * -5.0F;

        if (stack.getItem() instanceof SequencedAssemblyItem assemblyItem) {
            angle *= assemblyItem.getProgress(stack) * assemblyItem.getProgress(stack);
        }

        angle %= 360;

        ms.pushPose();
        ms.translate(0, offset, 0);
        ms.mulPose(Axis.XN.rotationDegrees(90.0F));
        ms.mulPose(Axis.YP.rotationDegrees(angle));
        ms.translate(0, -offset, 0);
        renderer.render(this.model.get(), light);
        ms.popPose();
    }
}