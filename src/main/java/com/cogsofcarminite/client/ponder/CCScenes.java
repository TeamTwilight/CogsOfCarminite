package com.cogsofcarminite.client.ponder;

import com.cogsofcarminite.blocks.MechanicalRootPullerBlock;
import com.cogsofcarminite.blocks.entities.HornblowerBlockEntity;
import com.cogsofcarminite.blocks.entities.MechanicalRootPullerBlockEntity;
import com.cogsofcarminite.reg.CCBlocks;
import com.simibubi.create.foundation.ponder.*;
import com.simibubi.create.foundation.ponder.element.EntityElement;
import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import com.simibubi.create.foundation.ponder.element.WorldSectionElement;
import com.simibubi.create.foundation.utility.Pointing;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import twilightforest.init.TFBlocks;
import twilightforest.init.TFItems;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CCScenes {
    public static void hornblower(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("hornblower", "Using the Hornblower");
        scene.configureBasePlate(0, 0, 5);

        BlockPos hornblowerPos = new BlockPos(3, 2, 2);
        Selection hornblowerSelect = util.select.position(hornblowerPos);
        Selection bigCog = util.select.position(4, 1, 3);
        Selection encasedCog = util.select.position(3, 1, 2);

        scene.world.showSection(util.select.layer(0), Direction.UP);
        scene.world.setKineticSpeed(encasedCog, 0);
        scene.world.setKineticSpeed(hornblowerSelect, 0);
        scene.idle(5);

        scene.world.showSection(encasedCog, Direction.DOWN);
        scene.idle(10);

        scene.world.showSection(hornblowerSelect, Direction.DOWN);
        scene.idle(10);
        Vec3 hornblowerTop = util.vector.topOf(hornblowerPos);
        scene.overlay.showText(60)
                .attachKeyFrame()
                .text("Hornblowers use kinetic energy to blow on horn items")
                .pointAt(hornblowerTop)
                .placeNearTarget();
        scene.idle(70);

        scene.world.showSection(bigCog, Direction.DOWN);
        scene.world.setKineticSpeed(encasedCog, 32);
        scene.world.setKineticSpeed(hornblowerSelect, 32);
        scene.effects.indicateSuccess(hornblowerPos);
        scene.idle(10);

        scene.overlay.showText(60)
                .attachKeyFrame()
                .colored(PonderPalette.GREEN)
                .text("They can be powered from below using shafts or cogwheels")
                .pointAt(util.vector.topOf(hornblowerPos.below()))
                .placeNearTarget();
        scene.idle(70);

        ItemStack blowHorn = Items.GOAT_HORN.getDefaultInstance();
        scene.overlay.showControls(new InputWindowElement(hornblowerTop, Pointing.DOWN).withItem(blowHorn).rightClick(), 30);
        scene.idle(7);

        scene.world.modifyBlockEntity(hornblowerPos, HornblowerBlockEntity.class, be -> be.horn = blowHorn.copy());
        scene.overlay.showText(40)
                .attachKeyFrame()
                .text("Use Right-click while holding a horn to insert it")
                .pointAt(hornblowerTop)
                .placeNearTarget();
        scene.idle(60);

        scene.overlay.showText(40)
                .attachKeyFrame()
                .text("After a short delay, the Hornblower will sound the horn")
                .pointAt(hornblowerTop)
                .placeNearTarget();
        scene.idle(130);

        scene.overlay.showControls(new InputWindowElement(hornblowerTop, Pointing.DOWN).rightClick(), 30);
        scene.idle(7);

        scene.world.modifyBlockEntity(hornblowerPos, HornblowerBlockEntity.class, be -> be.horn = ItemStack.EMPTY);
        scene.overlay.showText(40)
                .attachKeyFrame()
                .text("Right-click with an empty hand to retrieve the horn")
                .pointAt(hornblowerTop)
                .placeNearTarget();
        scene.idle(60);

        Selection rock = util.select.fromTo(0, 1, 1, 2, 3, 2);
        scene.world.showSection(rock, Direction.DOWN);
        scene.idle(10);

        ItemStack crumbleHorn = TFItems.CRUMBLE_HORN.get().getDefaultInstance();
        scene.overlay.showControls(new InputWindowElement(hornblowerTop, Pointing.DOWN).withItem(crumbleHorn).rightClick(), 30);
        scene.idle(7);

        scene.world.modifyBlockEntity(hornblowerPos, HornblowerBlockEntity.class, be -> be.horn = crumbleHorn.copy());
        scene.overlay.showText(40)
                .attachKeyFrame()
                .text("The Hornblower may also use the Crumble Horn")
                .pointAt(hornblowerTop)
                .placeNearTarget();
        scene.idle(30);

        BlockState cobble = Blocks.COBBLESTONE.defaultBlockState();
        scene.world.setBlock(new BlockPos(0, 1, 1), cobble, true);
        scene.world.setBlock(new BlockPos(1, 2, 2), cobble, true);
        scene.idle(30);

        scene.overlay.showText(80)
                .attachKeyFrame()
                .text("Each time the Crumble Horn is used, it's effect will be applied to random blocks in the 5x5x5 area in front of the Hornblower")
                .pointAt(hornblowerTop)
                .placeNearTarget();
        scene.idle(30);

        scene.world.setBlock(new BlockPos(0, 1, 2), cobble, true);
        scene.world.setBlock(new BlockPos(1, 1, 2), cobble, true);
        scene.idle(60);

        scene.world.setBlock(new BlockPos(1, 3, 2), cobble, true);
        scene.world.destroyBlock(new BlockPos(0, 1, 1));
        scene.idle(60);

        scene.world.setBlock(new BlockPos(1, 1, 1), cobble, true);
        scene.world.destroyBlock(new BlockPos(1, 3, 2));
        scene.idle(56);

        scene.overlay.showControls(new InputWindowElement(hornblowerTop, Pointing.DOWN).rightClick(), 30);
        scene.world.modifyBlockEntity(hornblowerPos, HornblowerBlockEntity.class, be -> be.horn = ItemStack.EMPTY);
    }

    public static void rootPuller(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("root_puller", "Using the Root Puller");
        scene.configureBasePlate(0, 0, 5);

        scene.world.setKineticSpeed(util.select.layer(0), -8);
        scene.world.setKineticSpeed(util.select.layer(1), 16);
        scene.world.setKineticSpeed(util.select.layer(2), -16);

        scene.world.showSection(util.select.layer(0), Direction.UP);
        scene.idle(5);
        scene.world.showSection(util.select.position(3, 1, 2), Direction.DOWN);
        scene.world.showSection(util.select.position(3, 1, 5), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.fromTo(3, 2, 3, 3, 2, 5), Direction.NORTH);
        scene.idle(10);
        scene.world.showSection(util.select.position(3, 2, 2), Direction.SOUTH);
        scene.idle(20);

        Selection worldToRoot = util.select.fromTo(1, 0, 1, 2, 0, 2);

        BlockPos breakingPos = util.grid.at(2, 2, 2);
        BlockPos uprootPos = util.grid.at(1, 0, 1);
        BlockPos uprootPos2 = util.grid.at(1, 0, 2);
        BlockPos uprootPos3 = util.grid.at(2, 0, 2);
        BlockPos uprootPos4 = util.grid.at(2, 0, 1);

        scene.world.hideSection(worldToRoot, Direction.DOWN);
        scene.idle(30);
        scene.world.setBlocks(worldToRoot, TFBlocks.ROOT_BLOCK.get().defaultBlockState(), false);
        scene.world.setBlock(uprootPos2, TFBlocks.LIVEROOT_BLOCK.get().defaultBlockState(), false);
        scene.world.showSection(util.select.fromTo(1, 0, 1, 2, 2, 2), Direction.DOWN);
        scene.idle(5);

        for (int i = 0; i < 10; i++) {
            scene.idle(10);
            scene.addInstruction(ponderScene -> {
                PonderWorld world = ponderScene.getWorld();
                int progress = world.getBlockBreakingProgressions().getOrDefault(breakingPos, -1) + 1;
                if (progress == 9) {
                    world.addBlockDestroyEffects(uprootPos, world.getBlockState(uprootPos));
                    world.destroyBlock(uprootPos, false);
                    world.setBlockBreakingProgress(breakingPos, 0);
                    ponderScene.forEach(WorldSectionElement.class, WorldSectionElement::queueRedraw);
                } else
                    world.setBlockBreakingProgress(breakingPos, progress + 1);
            });

            if (i == 1) {
                scene.overlay.showText(80)
                        .attachKeyFrame()
                        .placeNearTarget()
                        .pointAt(util.vector.topOf(breakingPos))
                        .text("When given Rotational Force, a Mechanical Root Puller will pull out root blocks in front of it");
            }
        }

        scene.idle(1);
        scene.world.hideSection(util.select.position(uprootPos), Direction.DOWN);
        ElementLink<EntityElement> stickEntity = scene.world.createItemEntity(util.vector.topOf(breakingPos), util.vector.of(0, .15f, 0), new ItemStack(Items.STICK));

        scene.world.modifyKineticSpeed(util.select.everywhere(), f -> 4 * f);
        scene.effects.rotationSpeedIndicator(new BlockPos(3, 2, 5));
        scene.idle(7);

        for (int i = 0; i < 10; i++) {
            scene.idle(3);
            scene.addInstruction(ponderScene -> {
                PonderWorld world = ponderScene.getWorld();
                int progress = world.getBlockBreakingProgressions().getOrDefault(breakingPos, -1) + 1;
                if (progress == 9) {
                    world.addBlockDestroyEffects(uprootPos2, world.getBlockState(uprootPos2));
                    world.destroyBlock(uprootPos2, false);
                    world.setBlockBreakingProgress(breakingPos, 0);
                    ponderScene.forEach(WorldSectionElement.class, WorldSectionElement::queueRedraw);
                } else
                    world.setBlockBreakingProgress(breakingPos, progress + 1);
            });

            if (i == 2) {
                scene.overlay.showText(60)
                        .attachKeyFrame()
                        .placeNearTarget()
                        .pointAt(util.vector.topOf(breakingPos.east()))
                        .text("Its mining speed depends on the Rotational Input");
            } else if (i == 8) scene.world.modifyEntity(stickEntity, Entity::discard);
        }

        scene.idle(1);
        ElementLink<EntityElement> rootEntity = scene.world.createItemEntity(util.vector.topOf(breakingPos), util.vector.of(0, .15f, 0), new ItemStack(TFItems.LIVEROOT.get()));
        scene.idle(7);

        for (int i = 0; i < 10; i++) {
            scene.idle(3);
            scene.addInstruction(ponderScene -> {
                PonderWorld world = ponderScene.getWorld();
                int progress = world.getBlockBreakingProgressions().getOrDefault(breakingPos, -1) + 1;
                if (progress == 9) {
                    world.addBlockDestroyEffects(uprootPos3, world.getBlockState(uprootPos3));
                    world.destroyBlock(uprootPos3, false);
                    world.setBlockBreakingProgress(breakingPos, 0);
                    ponderScene.forEach(WorldSectionElement.class, WorldSectionElement::queueRedraw);
                } else
                    world.setBlockBreakingProgress(breakingPos, progress + 1);
            });

            if (i == 8) scene.world.modifyEntity(rootEntity, Entity::discard);
        }

        BlockPos hopper = breakingPos.east().east();
        scene.world.showSection(util.select.position(hopper), Direction.DOWN);
        ItemStack itemStack = new ItemStack(Items.DIRT);
        Vec3 entitySpawn = util.vector.topOf(hopper.above(3));
        ElementLink<EntityElement> entity1 = scene.world.createItemEntity(entitySpawn, util.vector.of(0, 0.2, 0), itemStack);
        scene.idle(1);
        stickEntity = scene.world.createItemEntity(util.vector.topOf(breakingPos), util.vector.of(0, .15f, 0), new ItemStack(Items.STICK));
        scene.idle(7);

        for (int i = 0; i < 10; i++) {
            scene.idle(3);
            scene.addInstruction(ponderScene -> {
                PonderWorld world = ponderScene.getWorld();
                int progress = world.getBlockBreakingProgressions().getOrDefault(breakingPos, -1) + 1;
                if (progress == 9) {
                    world.setBlockBreakingProgress(breakingPos, 0);
                    ponderScene.forEach(WorldSectionElement.class, WorldSectionElement::queueRedraw);
                } else
                    world.setBlockBreakingProgress(breakingPos, progress + 1);
            });

            if (i == 2) {
                scene.overlay.showText(80)
                        .attachKeyFrame()
                        .text("Blocks may be inserted by things like Hoppers and Belts. Inserted blocks will be used to replace Roots as they are destroyed")
                        .pointAt(util.vector.topOf(breakingPos.east()))
                        .placeNearTarget();
            } else if (i == 4) scene.world.modifyEntity(entity1, Entity::discard);
            else if (i == 8) scene.world.modifyEntity(stickEntity, Entity::discard);
            else if (i == 9) scene.world.setBlock(uprootPos4, Blocks.DIRT.defaultBlockState(), true);
        }

        scene.idle(1);
        stickEntity = scene.world.createItemEntity(util.vector.topOf(breakingPos), util.vector.of(0, .15f, 0), new ItemStack(Items.STICK));
        scene.idle(7);

        for (int i = 0; i < 10; i++) {
            scene.idle(3);
            scene.addInstruction(ponderScene -> {
                PonderWorld world = ponderScene.getWorld();
                int progress = world.getBlockBreakingProgressions().getOrDefault(breakingPos, -1) + 1;
                if (progress == 9) {
                    world.setBlockBreakingProgress(breakingPos, 0);
                    ponderScene.forEach(WorldSectionElement.class, WorldSectionElement::queueRedraw);
                } else
                    world.setBlockBreakingProgress(breakingPos, progress + 1);
            });
            if (i == 8) scene.world.modifyEntity(stickEntity, Entity::discard);
            else if (i == 9) scene.world.setBlock(uprootPos4.above(), Blocks.DIRT.defaultBlockState(), true);
        }

        scene.idle(1);
        stickEntity = scene.world.createItemEntity(util.vector.topOf(breakingPos), util.vector.of(0, .15f, 0), new ItemStack(TFItems.LIVEROOT.get()));
        scene.idle(7);

        for (int i = 0; i < 10; i++) {
            scene.idle(3);
            scene.addInstruction(ponderScene -> {
                PonderWorld world = ponderScene.getWorld();
                int progress = world.getBlockBreakingProgressions().getOrDefault(breakingPos, -1) + 1;
                if (progress == 9) {
                    world.setBlockBreakingProgress(breakingPos, 0);
                    ponderScene.forEach(WorldSectionElement.class, WorldSectionElement::queueRedraw);
                } else
                    world.setBlockBreakingProgress(breakingPos, progress + 1);
            });
            if (i == 8) scene.world.modifyEntity(stickEntity, Entity::discard);
            else if (i == 9) scene.world.setBlock(uprootPos3.above(), Blocks.DIRT.defaultBlockState(), true);
        }

        scene.idle(1);
        stickEntity = scene.world.createItemEntity(util.vector.topOf(breakingPos), util.vector.of(0, .15f, 0), new ItemStack(Items.STICK));
        scene.idle(7);

        for (int i = 0; i < 10; i++) {
            scene.idle(3);
            scene.addInstruction(ponderScene -> {
                PonderWorld world = ponderScene.getWorld();
                int progress = world.getBlockBreakingProgressions().getOrDefault(breakingPos, -1) + 1;
                if (progress == 9) {
                    world.setBlockBreakingProgress(breakingPos, 0);
                    ponderScene.forEach(WorldSectionElement.class, WorldSectionElement::queueRedraw);
                } else
                    world.setBlockBreakingProgress(breakingPos, progress + 1);
            });
            if (i == 2) {
                Vec3 filterPos = MechanicalRootPullerBlockEntity.MechanicalRootPullerFilterSlot
                        .getOffset(CCBlocks.MECHANICAL_ROOT_PULLER.getDefaultState().setValue(MechanicalRootPullerBlock.FACING, Direction.WEST))
                        .add(Vec3.atLowerCornerOf(breakingPos.east()))
                        .add(0.0D, 0.025D, 0.0D);
                scene.overlay.showFilterSlotInput(filterPos, Direction.UP, 100);
                scene.overlay.showText(100)
                        .pointAt(filterPos)
                        .placeNearTarget()
                        .attachKeyFrame()
                        .text("The filter slot can be used to specify which blocks the Root Puller accepts.");
            } else if (i == 8) scene.world.modifyEntity(stickEntity, Entity::discard);
            else if (i == 9) scene.world.setBlock(uprootPos3.above().above(), Blocks.DIRT.defaultBlockState(), true);
        }

        scene.idle(1);
        scene.world.createItemEntity(util.vector.topOf(breakingPos), util.vector.of(0, .15f, 0), new ItemStack(Items.STICK));
        scene.idle(60);
    }
}
