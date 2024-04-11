package com.cogsofcarminite.client.instances;

import com.cogsofcarminite.blocks.CarminiteMagicLogBlock;
import com.cogsofcarminite.blocks.entities.CarminiteMagicLogBlockEntity;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CarminiteMagicLogInstance extends KineticBlockEntityInstance<CarminiteMagicLogBlockEntity> implements DynamicInstance {
    protected final RotatingData shaft;

    public CarminiteMagicLogInstance(MaterialManager materialManager, CarminiteMagicLogBlockEntity blockEntity) {
        super(materialManager, blockEntity);
        Direction facing = this.blockState.getBlock() instanceof CarminiteMagicLogBlock logBlock ? logBlock.getDirection(this.blockState).getOpposite() : Direction.UP;
        this.shaft = setup(getRotatingMaterial().getModel(AllPartialModels.SHAFT_HALF, blockEntity.getBlockState(), facing).createInstance());
    }

    @Override
    public void beginFrame() {

    }

    @Override
    public void update() {
        this.updateRotation(this.shaft);
    }

    @Override
    public void updateLight() {
        this.relight(this.pos, this.shaft);
    }

    @Override
    public void remove() {
        this.shaft.delete();
    }
}
