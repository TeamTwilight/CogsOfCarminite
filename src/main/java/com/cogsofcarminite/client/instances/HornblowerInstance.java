package com.cogsofcarminite.client.instances;

import com.cogsofcarminite.reg.CCPartialBlockModels;
import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.SingleRotatingInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class HornblowerInstance<T extends KineticBlockEntity> extends SingleRotatingInstance<T> {
    public HornblowerInstance(MaterialManager materialManager, T blockEntity) {
        super(materialManager, blockEntity);
    }

    @Override
    protected Instancer<RotatingData> getModel() {
        return getRotatingMaterial().getModel(CCPartialBlockModels.SHAFT_QUARTER, blockState, Direction.DOWN);
    }
}
