package com.cogsofcarminite.blocks;

import com.cogsofcarminite.blocks.entities.CarminiteClockBlockEntity;
import com.cogsofcarminite.reg.CCBlockEntities;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CarminiteClockBlock extends CarminiteMagicLogBlock implements IBE<CarminiteClockBlockEntity> {
    public CarminiteClockBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<CarminiteClockBlockEntity> getBlockEntityClass() {
        return CarminiteClockBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CarminiteClockBlockEntity> getBlockEntityType() {
        return CCBlockEntities.CARMINITE_CLOCK.get();
    }
}
