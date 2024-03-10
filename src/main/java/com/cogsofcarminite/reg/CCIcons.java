package com.cogsofcarminite.reg;

import com.cogsofcarminite.CogsOfCarminite;
import com.cogsofcarminite.mixin.AllIconsAccessor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.utility.Color;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CCIcons extends AllIcons {
    public static final ResourceLocation CC_ATLAS = CogsOfCarminite.prefix("textures/gui/icons.png");

    public static final CCIcons HEART_TRANSFORM = new CCIcons(0, 0);
    public static final CCIcons HEART_ADAPT = new CCIcons(1, 0);
    public static final CCIcons HEART_REVERT = new CCIcons(2, 0);

    public CCIcons(int x, int y) {
        super(x, y);
    }

    @OnlyIn(Dist.CLIENT)
    public void bind() {
        RenderSystem.setShaderTexture(0, CC_ATLAS);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(GuiGraphics graphics, int x, int y) {
        graphics.blit(CC_ATLAS, x, y, 0, ((AllIconsAccessor)this).getIconX(), ((AllIconsAccessor)this).getIconY(), 16, 16, 256, 256);
    }

    @OnlyIn(Dist.CLIENT)
    public void render(PoseStack ms, MultiBufferSource buffer, int color) {
        VertexConsumer builder = buffer.getBuffer(RenderType.text(CC_ATLAS));
        Matrix4f matrix = ms.last().pose();
        Color rgb = new Color(color);
        int light = LightTexture.FULL_BRIGHT;

        Vec3 vec1 = new Vec3(0, 0, 0);
        Vec3 vec2 = new Vec3(0, 1, 0);
        Vec3 vec3 = new Vec3(1, 1, 0);
        Vec3 vec4 = new Vec3(1, 0, 0);

        float u1 = ((AllIconsAccessor)this).getIconX() * 1f / ICON_ATLAS_SIZE;
        float u2 = (((AllIconsAccessor)this).getIconX() + 16) * 1f / ICON_ATLAS_SIZE;
        float v1 = ((AllIconsAccessor)this).getIconY() * 1f / ICON_ATLAS_SIZE;
        float v2 = (((AllIconsAccessor)this).getIconY() + 16) * 1f / ICON_ATLAS_SIZE;

        vertex(builder, matrix, vec1, rgb, u1, v1, light);
        vertex(builder, matrix, vec2, rgb, u1, v2, light);
        vertex(builder, matrix, vec3, rgb, u2, v2, light);
        vertex(builder, matrix, vec4, rgb, u2, v1, light);
    }

    @OnlyIn(Dist.CLIENT)
    private void vertex(VertexConsumer builder, Matrix4f matrix, Vec3 vec, Color rgb, float u, float v, int light) {
        builder.vertex(matrix, (float) vec.x, (float) vec.y, (float) vec.z)
                .color(rgb.getRed(), rgb.getGreen(), rgb.getBlue(), 255)
                .uv(u, v)
                .uv2(light)
                .endVertex();
    }
}
