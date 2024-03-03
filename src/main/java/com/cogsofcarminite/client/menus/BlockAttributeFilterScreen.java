package com.cogsofcarminite.client.menus;

import com.cogsofcarminite.client.CCGuiTextures;
import com.cogsofcarminite.network.BlockFilterScreenPacket;
import com.cogsofcarminite.reg.CCPackets;
import com.cogsofcarminite.util.attributes.BlockAttribute;
import com.google.common.collect.ImmutableList;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.logistics.filter.AttributeFilterMenu;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.element.GuiGameElement;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Indicator;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.model.data.ModelData;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.simibubi.create.foundation.gui.AllGuiTextures.PLAYER_INVENTORY;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockAttributeFilterScreen extends AbstractSimiContainerScreen<BlockAttributeFilterMenu> {
    private static final String PREFIX = "gui.attribute_filter.";
    private final Component addDESC = Lang.translateDirect(PREFIX + "add_attribute");
    private final Component addInvertedDESC = Lang.translateDirect(PREFIX + "add_inverted_attribute");
    private final Component allowDisN = Lang.translateDirect(PREFIX + "allow_list_disjunctive");
    private final Component allowDisDESC = Lang.translateDirect(PREFIX + "allow_list_disjunctive.description");
    private final Component allowConN = Lang.translateDirect(PREFIX + "allow_list_conjunctive");
    private final Component allowConDESC = Lang.translateDirect(PREFIX + "allow_list_conjunctive.description");
    private final Component denyN = Lang.translateDirect(PREFIX + "deny_list");
    private final Component denyDESC = Lang.translateDirect(PREFIX + "deny_list.description");
    private final Component referenceH = Lang.translateDirect(PREFIX + "add_reference_item");
    private final Component noSelectedT = Lang.translateDirect(PREFIX + "no_selected_attributes");
    private final Component selectedT = Lang.translateDirect(PREFIX + "selected_attributes");

    private IconButton whitelistDis, whitelistCon, blacklist;
    private Indicator whitelistDisIndicator, whitelistConIndicator, blacklistIndicator;
    private IconButton add;
    private IconButton addInverted;

    private ItemStack lastItemScanned = ItemStack.EMPTY;
    private final List<BlockAttribute> attributesOfItem = new ArrayList<>();
    private final List<Component> selectedAttributes = new ArrayList<>();
    private SelectionScrollInput attributeSelector;
    private Label attributeSelectorLabel;

    protected CCGuiTextures background;
    private List<Rect2i> extraAreas = Collections.emptyList();

    public BlockAttributeFilterScreen(BlockAttributeFilterMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.background = CCGuiTextures.ATTRIBUTE_FILTER;
    }

    @Override
    protected void init() {
        this.setWindowOffset(-11, 7);
        this.setWindowSize(Math.max(this.background.width, PLAYER_INVENTORY.width), this.background.height + 4 + PLAYER_INVENTORY.height);
        super.init();

        int x = this.leftPos;
        int y = this.topPos;

        IconButton resetButton = new IconButton(x + this.background.width - 62, y + this.background.height - 24, AllIcons.I_TRASH);
        resetButton.withCallback(() -> {
            this.menu.clearContents();
            contentsCleared();
            this.menu.sendClearPacket();
        });
        IconButton confirmButton = new IconButton(x + this.background.width - 33, y + this.background.height - 24, AllIcons.I_CONFIRM);
        confirmButton.withCallback(() -> {
            if (this.minecraft != null && this.minecraft.player != null) this.minecraft.player.closeContainer();
        });

        this.addRenderableWidget(resetButton);
        this.addRenderableWidget(confirmButton);

        this.extraAreas = ImmutableList.of(new Rect2i(x + this.background.width, y + this.background.height - 40, 80, 48));

        this.whitelistDis = new IconButton(x + 47, y + 61, AllIcons.I_WHITELIST_OR);
        this.whitelistDis.withCallback(() -> {
            this.menu.whitelistMode = AttributeFilterMenu.WhitelistMode.WHITELIST_DISJ;
            this.sendOptionUpdate(BlockFilterScreenPacket.Option.WHITELIST);
        });
        this.whitelistDis.setToolTip(this.allowDisN);
        this.whitelistCon = new IconButton(x + 65, y + 61, AllIcons.I_WHITELIST_AND);
        this.whitelistCon.withCallback(() -> {
            this.menu.whitelistMode = AttributeFilterMenu.WhitelistMode.WHITELIST_CONJ;
            this.sendOptionUpdate(BlockFilterScreenPacket.Option.WHITELIST2);
        });
        this.whitelistCon.setToolTip(this.allowConN);
        this.blacklist = new IconButton(x + 83, y + 61, AllIcons.I_WHITELIST_NOT);
        this.blacklist.withCallback(() -> {
            this.menu.whitelistMode = AttributeFilterMenu.WhitelistMode.BLACKLIST;
            this.sendOptionUpdate(BlockFilterScreenPacket.Option.BLACKLIST);
        });
        this.blacklist.setToolTip(denyN);

        this.whitelistDisIndicator = new Indicator(x + 47, y + 55, Components.immutableEmpty());
        this.whitelistConIndicator = new Indicator(x + 65, y + 55, Components.immutableEmpty());
        this.blacklistIndicator = new Indicator(x + 83, y + 55, Components.immutableEmpty());

        this.addRenderableWidgets(this.blacklist, this.whitelistCon, this.whitelistDis, this.blacklistIndicator, this.whitelistConIndicator, this.whitelistDisIndicator);

        this.addRenderableWidget(this.add = new IconButton(x + 182, y + 23, AllIcons.I_ADD));
        this.addRenderableWidget(this.addInverted = new IconButton(x + 200, y + 23, AllIcons.I_ADD_INVERTED_ATTRIBUTE));
        this.add.withCallback(() -> this.handleAddedAttibute(false));
        this.add.setToolTip(this.addDESC);
        this.addInverted.withCallback(() -> this.handleAddedAttibute(true));
        this.addInverted.setToolTip(this.addInvertedDESC);

        this.handleIndicators();

        this.attributeSelectorLabel = new Label(x + 43, y + 28, Components.immutableEmpty()).colored(0xF3EBDE).withShadow();
        this.attributeSelector = new SelectionScrollInput(x + 39, y + 23, 137, 18);
        this.attributeSelector.forOptions(Collections.singletonList(Components.immutableEmpty()));
        this.attributeSelector.removeCallback();
        this.referenceItemChanged(this.menu.ghostInventory.getStackInSlot(0));

        this.addRenderableWidget(this.attributeSelector);
        this.addRenderableWidget(this.attributeSelectorLabel);

        this.selectedAttributes.clear();
        this.selectedAttributes.add((this.menu.selectedAttributes.isEmpty() ? this.noSelectedT : this.selectedT).plainCopy().withStyle(ChatFormatting.YELLOW));
        this.menu.selectedAttributes.forEach(at -> this.selectedAttributes.add(Components.literal("- ").append(at.getFirst().format(at.getSecond())).withStyle(ChatFormatting.GRAY)));
    }

    @Override
    @SuppressWarnings("DataFlowIssue")
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        if (this.menu.clickedState != null && this.menu.ghostInventory.getStackInSlot(0).isEmpty()) {
            if (this.minecraft != null) {
                Lighting.setupForEntityInInventory();
                PoseStack poseStack = graphics.pose();

                poseStack.pushPose();
                poseStack.translate(this.leftPos + 23.5D, this.topPos + 31.5D, 150);
                TransformStack.cast(poseStack)
                        .centre()
                        .scale(16.0F, 16.0F, 16.0F)
                        .rotateX(30.0F)
                        .rotateY(225.0F)
                        .scale(0.625F)
                        .unCentre();

                MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
                Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                        this.menu.clickedState,
                        poseStack,
                        buffer,
                        LightTexture.FULL_BRIGHT,
                        OverlayTexture.NO_OVERLAY,
                        ModelData.EMPTY,
                        null
                );

                buffer.endBatch();
                poseStack.popPose();
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        int invX = this.getLeftOfCentered(PLAYER_INVENTORY.width);
        int invY = this.topPos + this.background.height + 4;
        this.renderPlayerInventory(graphics, invX, invY);

        int x = this.leftPos;
        int y = this.topPos;

        this.background.render(graphics, x, y);
        graphics.drawString(this.font, this.title, x + (this.background.width - 8) / 2 - this.font.width(this.title) / 2, y + 4,
                AllItems.FILTER.isIn(this.menu.contentHolder) ? 0x303030 : 0x592424, false);//FIXME

        GuiGameElement.of(this.menu.contentHolder).<GuiGameElement
                        .GuiRenderBuilder>at(x + this.background.width + 8, y + this.background.height - 52, -200)
                .scale(4)
                .render(graphics);
    }

    @Override
    protected void containerTick() {
        if (!this.menu.player.getMainHandItem().equals(this.menu.contentHolder, false)) this.menu.player.closeContainer();

        super.containerTick();

        this.handleTooltips();
        this.handleIndicators();

        ItemStack stackInSlot = this.menu.ghostInventory.getStackInSlot(0);
        if (!stackInSlot.equals(this.lastItemScanned, false)) this.referenceItemChanged(stackInSlot);
    }

    protected void handleTooltips() {
        List<IconButton> tooltipButtons = this.getTooltipButtons();

        for (IconButton button : tooltipButtons) {
            if (!button.getToolTip()
                    .isEmpty()) {
                button.setToolTip(button.getToolTip()
                        .get(0));
                button.getToolTip()
                        .add(TooltipHelper.holdShift(TooltipHelper.Palette.YELLOW, hasShiftDown()));
            }
        }

        if (hasShiftDown()) {
            List<MutableComponent> tooltipDescriptions = this.getTooltipDescriptions();
            for (int i = 0; i < tooltipButtons.size(); i++)
                this.fillToolTip(tooltipButtons.get(i), tooltipDescriptions.get(i));
        }
    }

    public void handleIndicators() {
        for (IconButton button : this.getTooltipButtons()) button.active = this.isButtonEnabled(button);
        for (Indicator indicator : this.getIndicators()) indicator.state = this.isIndicatorOn(indicator) ? Indicator.State.ON : Indicator.State.OFF;
    }

    private void fillToolTip(IconButton button, Component tooltip) {
        if (!button.isHoveredOrFocused()) return;
        List<Component> tip = button.getToolTip();
        tip.addAll(TooltipHelper.cutTextComponent(tooltip, TooltipHelper.Palette.ALL_GRAY));
    }

    protected void sendOptionUpdate(BlockFilterScreenPacket.Option option) {
        CCPackets.getChannel().sendToServer(new BlockFilterScreenPacket(option));
    }

    @Override
    public List<Rect2i> getExtraAreas() {
        return this.extraAreas;
    }

    private void referenceItemChanged(ItemStack stack) {
        this.lastItemScanned = stack;

        if (stack.isEmpty() && this.menu.noBlock()) {
            this.attributeSelector.active = false;
            this.attributeSelector.visible = false;
            this.attributeSelectorLabel.text = this.referenceH.plainCopy()
                    .withStyle(ChatFormatting.ITALIC);
            this.add.active = false;
            this.addInverted.active = false;
            this.attributeSelector.calling(s -> {
            });
            return;
        }

        this.add.active = true;

        this.addInverted.active = true;
        this.attributeSelector.titled(this.menu.clickedBlock.getName()
                .plainCopy()
                .append("..."));
        this.attributesOfItem.clear();

        Level level = this.minecraft == null ? null : this.minecraft.level;

        if (this.menu.firstSet && this.menu.clickedPos != null) {
            for (BlockAttribute blockAttribute : BlockAttribute.types)
                this.attributesOfItem.addAll(blockAttribute.listAttributesOf(this.menu.clickedState, level, this.menu.clickedPos));
        } else {
            for (BlockAttribute blockAttribute : BlockAttribute.types)
                this.attributesOfItem.addAll(blockAttribute.listAttributesOf(this.menu.clickedState, level));
        }

        List<Component> options = attributesOfItem.stream()
                .map(a -> a.format(false))
                .collect(Collectors.toList());
        this.attributeSelector.forOptions(options);
        this.attributeSelector.active = true;
        this.attributeSelector.visible = true;
        this.attributeSelector.setState(0);
        this.attributeSelector.calling(i -> {
            this.attributeSelectorLabel.setTextAndTrim(options.get(i), true, 112);
            BlockAttribute selected = this.attributesOfItem.get(i);
            for (Pair<BlockAttribute, Boolean> existing : this.menu.selectedAttributes) {
                CompoundTag testTag = new CompoundTag();
                CompoundTag testTag2 = new CompoundTag();
                existing.getFirst()
                        .serializeNBT(testTag);
                selected.serializeNBT(testTag2);
                if (testTag.equals(testTag2)) {
                    this.add.active = false;
                    this.addInverted.active = false;
                    return;
                }
            }
            this.add.active = true;
            this.addInverted.active = true;
        });
        this.attributeSelector.onChanged();
    }

    @Override
    public void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        ItemStack stack = this.menu.ghostInventory.getStackInSlot(1);
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate(0, 0, 150);
        graphics.renderItemDecorations(this.font, stack, this.leftPos + 22, this.topPos + 59,
                String.valueOf(this.selectedAttributes.size() - 1));
        matrixStack.popPose();

        super.renderForeground(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null) {
            if (this.hoveredSlot.hasItem()) {
                if (this.hoveredSlot.index == 37) {
                    graphics.renderComponentTooltip(this.font, this.selectedAttributes, mouseX, mouseY);
                    return;
                }
                graphics.renderTooltip(this.font, this.hoveredSlot.getItem(), mouseX, mouseY);
            } else if (this.hoveredSlot.index == 36 && !this.menu.noBlock()) {
                graphics.renderComponentTooltip(this.font, List.of(this.menu.clickedBlock.getName()), mouseX, mouseY);
                return;
            }
        }
        super.renderTooltip(graphics, mouseX, mouseY);
    }

    protected List<IconButton> getTooltipButtons() {
        return Arrays.asList(this.blacklist, this.whitelistCon, this.whitelistDis);
    }

    protected List<MutableComponent> getTooltipDescriptions() {
        return Arrays.asList(this.denyDESC.plainCopy(), this.allowConDESC.plainCopy(), this.allowDisDESC.plainCopy());
    }

    protected List<Indicator> getIndicators() {
        return Arrays.asList(this.blacklistIndicator, this.whitelistConIndicator, this.whitelistDisIndicator);
    }

    protected boolean handleAddedAttibute(boolean inverted) {
        int index = this.attributeSelector.getState();
        if (index >= this.attributesOfItem.size())
            return false;
        this.add.active = false;
        this.addInverted.active = false;
        CompoundTag tag = new CompoundTag();
        BlockAttribute blockAttribute = this.attributesOfItem.get(index);
        blockAttribute.serializeNBT(tag);
        CCPackets.getChannel()
                .sendToServer(new BlockFilterScreenPacket(inverted ? BlockFilterScreenPacket.Option.ADD_INVERTED_TAG : BlockFilterScreenPacket.Option.ADD_TAG, tag));
        this.menu.appendSelectedAttribute(blockAttribute, inverted);
        if (this.menu.selectedAttributes.size() == 1)
            this.selectedAttributes.set(0, this.selectedT.plainCopy()
                    .withStyle(ChatFormatting.YELLOW));
        this.selectedAttributes.add(Components.literal("- ").append(blockAttribute.format(inverted))
                .withStyle(ChatFormatting.GRAY));
        return true;
    }

    protected void contentsCleared() {
        this.selectedAttributes.clear();
        this.selectedAttributes.add(this.noSelectedT.plainCopy()
                .withStyle(ChatFormatting.YELLOW));
        if (!this.lastItemScanned.isEmpty()) {
            this.add.active = true;
            this.addInverted.active = true;
        }
    }

    protected boolean isButtonEnabled(IconButton button) {
        if (button == this.blacklist)
            return this.menu.whitelistMode != AttributeFilterMenu.WhitelistMode.BLACKLIST;
        if (button == this.whitelistCon)
            return this.menu.whitelistMode != AttributeFilterMenu.WhitelistMode.WHITELIST_CONJ;
        if (button == this.whitelistDis)
            return this.menu.whitelistMode != AttributeFilterMenu.WhitelistMode.WHITELIST_DISJ;
        return true;
    }

    protected boolean isIndicatorOn(Indicator indicator) {
        if (indicator == this.blacklistIndicator)
            return this.menu.whitelistMode == AttributeFilterMenu.WhitelistMode.BLACKLIST;
        if (indicator == this.whitelistConIndicator)
            return this.menu.whitelistMode == AttributeFilterMenu.WhitelistMode.WHITELIST_CONJ;
        if (indicator == this.whitelistDisIndicator)
            return this.menu.whitelistMode == AttributeFilterMenu.WhitelistMode.WHITELIST_DISJ;
        return false;
    }
}
