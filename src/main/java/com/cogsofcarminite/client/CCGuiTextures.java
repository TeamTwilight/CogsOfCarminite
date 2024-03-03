package com.cogsofcarminite.client;

import com.cogsofcarminite.CogsOfCarminite;
import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.foundation.gui.UIRenderHelper;
import com.simibubi.create.foundation.gui.element.ScreenElement;
import com.simibubi.create.foundation.utility.Color;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public enum CCGuiTextures implements ScreenElement {
    ATTRIBUTE_FILTER("filters", 0, 0, 241, 85);

    public final ResourceLocation location;
    public final int width, height;
    public final int startX, startY;

    private CCGuiTextures(String location, int width, int height) {
        this(location, 0, 0, width, height);
    }

    private CCGuiTextures(int startX, int startY) {
        this("icons", startX * 16, startY * 16, 16, 16);
    }

    private CCGuiTextures(String location, int startX, int startY, int width, int height) {
        this.location = CogsOfCarminite.prefix("textures/gui/" + location + ".png");
        this.width = width;
        this.height = height;
        this.startX = startX;
        this.startY = startY;
    }

    @OnlyIn(Dist.CLIENT)
    public void bind() {
        RenderSystem.setShaderTexture(0, this.location);
    }

    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y) {
        graphics.blit(this.location, x, y, this.startX, this.startY, this.width, this.height);
    }

    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, Color c) {
        bind();
        UIRenderHelper.drawColoredTexture(graphics, c, x, y, this.startX, this.startY, this.width, this.height);
    }

}
