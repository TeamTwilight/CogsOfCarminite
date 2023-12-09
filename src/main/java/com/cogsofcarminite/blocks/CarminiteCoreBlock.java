package com.cogsofcarminite.blocks;

import com.cogsofcarminite.blocks.entities.CarminiteCoreBlockEntity;
import com.cogsofcarminite.reg.CCBlockEntities;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CarminiteCoreBlock extends CarminiteMagicLogBlock implements IBE<CarminiteCoreBlockEntity> {
    public CarminiteCoreBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<CarminiteCoreBlockEntity> getBlockEntityClass() {
        System.out.println();
        return CarminiteCoreBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CarminiteCoreBlockEntity> getBlockEntityType() {
        return CCBlockEntities.CARMINITE_CORE.get();
    }
}
