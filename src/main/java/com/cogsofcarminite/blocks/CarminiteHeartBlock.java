package com.cogsofcarminite.blocks;

import com.cogsofcarminite.blocks.entities.CarminiteHeartBlockEntity;
import com.cogsofcarminite.reg.CCBlockEntities;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CarminiteHeartBlock extends CarminiteMagicLogBlock implements IBE<CarminiteHeartBlockEntity> {
    public CarminiteHeartBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<CarminiteHeartBlockEntity> getBlockEntityClass() {
        return CarminiteHeartBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CarminiteHeartBlockEntity> getBlockEntityType() {
        return CCBlockEntities.CARMINITE_HEART.get();
    }
}
