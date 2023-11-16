package com.example.examplemod.instances;

import com.example.examplemod.blocks.entities.CarminiteClockBlockEntity;
import com.jozufozu.flywheel.api.MaterialManager;
import com.simibubi.create.content.kinetics.base.SingleRotatingInstance;

public class CarminiteClockInstance extends SingleRotatingInstance<CarminiteClockBlockEntity> {

    public CarminiteClockInstance(MaterialManager materialManager, CarminiteClockBlockEntity blockEntity) {
        super(materialManager, blockEntity);
    }

}
