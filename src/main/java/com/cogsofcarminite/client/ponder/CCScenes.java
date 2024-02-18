package com.cogsofcarminite.client.ponder;

import com.cogsofcarminite.blocks.entities.HornblowerBlockEntity;
import com.simibubi.create.foundation.ponder.PonderPalette;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.Selection;
import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import com.simibubi.create.foundation.utility.Pointing;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
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
}
