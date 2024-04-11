package com.cogsofcarminite.blocks.generators;

import com.cogsofcarminite.blocks.DirectedDirectionalKineticBlock;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.ModelFile;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CarminiteMagicLogBlockGenerator extends SpecialBlockStateGen {
    @Override
    protected int getXRotation(BlockState state) {
        return switch (state.getValue(DirectedDirectionalKineticBlock.TARGET)) {
            case CEILING -> -90;
            case WALL -> 0;
            case FLOOR -> 90;
        };
    }

    @Override
    protected int getYRotation(BlockState state) {
        return horizontalAngle(state.getValue(DirectedDirectionalKineticBlock.HORIZONTAL_FACING)) + 180;
    }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
        return AssetLookup.partialBaseModel(ctx, prov);
    }
}
