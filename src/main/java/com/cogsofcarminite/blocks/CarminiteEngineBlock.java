package com.cogsofcarminite.blocks;

import com.cogsofcarminite.blocks.entities.CarminiteEngineBlockEntity;
import com.cogsofcarminite.reg.CCBlockEntities;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CarminiteEngineBlock extends CarminiteMagicLogBlock implements IBE<CarminiteEngineBlockEntity> {
    public CarminiteEngineBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<CarminiteEngineBlockEntity> getBlockEntityClass() {
        return CarminiteEngineBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CarminiteEngineBlockEntity> getBlockEntityType() {
        return CCBlockEntities.CARMINITE_ENGINE.get();
    }
}
