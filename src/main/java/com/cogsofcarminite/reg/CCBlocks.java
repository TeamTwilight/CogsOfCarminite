package com.cogsofcarminite.reg;

import com.cogsofcarminite.blocks.CarminiteClockBlock;
import com.cogsofcarminite.blocks.CarminiteCoreBlock;
import com.cogsofcarminite.blocks.CarminiteEngineBlock;
import com.cogsofcarminite.blocks.CarminiteHeartBlock;
import com.cogsofcarminite.client.CCSpriteShifts;
import com.cogsofcarminite.items.*;
import com.simibubi.create.content.decoration.encasing.CasingBlock;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.BuilderTransformers;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

import static com.cogsofcarminite.CogsOfCarminite.TWILIGHT_REGISTRATE;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

@SuppressWarnings("removal")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CCBlocks {
    static {
        TWILIGHT_REGISTRATE.useCreativeTab(CCCreativeModeTabs.MAIN_TAB);
    }

    public static final BlockEntry<CasingBlock> DARK_TOWER_CASING =
            TWILIGHT_REGISTRATE.block("dark_tower_casing", CasingBlock::new)
                    .transform(BuilderTransformers.casing(() -> CCSpriteShifts.DARK_TOWER_CASING))
                    .properties(p -> p.mapColor(MapColor.PODZOL))
                    .register();

    public static final BlockEntry<CarminiteCoreBlock> CARMINITE_CORE =
            TWILIGHT_REGISTRATE.block("carminite_core", CarminiteCoreBlock::new)
                    .initialProperties(SharedProperties::copperMetal)
                    .properties(p -> p.mapColor(MapColor.STONE)
                            .noOcclusion())
                    .transform(pickaxeOnly())
                    .blockstate(directionalBlockProviderIgnoresWaterlogged())
                    .addLayer(() -> RenderType::cutoutMipped)
                    .transform(BlockStressDefaults.setImpact(4.0))
                    .item(CarminiteCoreItem::new)
                    .transform(customItemModel())
                    .register();

    public static final BlockEntry<CarminiteHeartBlock> CARMINITE_HEART =
            TWILIGHT_REGISTRATE.block("carminite_heart", CarminiteHeartBlock::new)
                    .initialProperties(SharedProperties::copperMetal)
                    .properties(p -> p.mapColor(MapColor.STONE)
                            .noOcclusion())
                    .transform(pickaxeOnly())
                    .blockstate(directionalBlockProviderIgnoresWaterlogged())
                    .addLayer(() -> RenderType::cutoutMipped)
                    .transform(BlockStressDefaults.setImpact(4.0))
                    .item(CarminiteHeartItem::new)
                    .transform(customItemModel())
                    .register();

    public static final BlockEntry<CarminiteEngineBlock> CARMINITE_ENGINE =
            TWILIGHT_REGISTRATE.block("carminite_engine", CarminiteEngineBlock::new)
                    .initialProperties(SharedProperties::copperMetal)
                    .properties(p -> p.mapColor(MapColor.STONE)
                            .noOcclusion())
                    .transform(pickaxeOnly())
                    .blockstate(directionalBlockProviderIgnoresWaterlogged())
                    .addLayer(() -> RenderType::cutoutMipped)
                    .transform(BlockStressDefaults.setImpact(4.0))
                    .item(CarminiteEngineItem::new)
                    .transform(customItemModel())
                    .register();

    public static final BlockEntry<CarminiteClockBlock> CARMINITE_CLOCK =
            TWILIGHT_REGISTRATE.block("carminite_clock", CarminiteClockBlock::new)
                    .initialProperties(SharedProperties::copperMetal)
                    .properties(p -> p.mapColor(MapColor.STONE)
                            .noOcclusion())
                    .transform(pickaxeOnly())
                    .blockstate(directionalBlockProviderIgnoresWaterlogged())
                    .addLayer(() -> RenderType::cutoutMipped)
                    .transform(BlockStressDefaults.setImpact(4.0))
                    .item(CarminiteClockItem::new)
                    .transform(customItemModel())
                    .register();

    public static void register() {}

    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> directionalBlockProviderIgnoresWaterlogged() {
        return (c, p) -> directionalBlockIgnoresWaterlogged(c, p, $ -> AssetLookup.partialBaseModel(c, p));
    }

    public static <T extends Block> void directionalBlockIgnoresWaterlogged(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, Function<BlockState, ModelFile> modelFunc) {
        prov.getVariantBuilder(ctx.getEntry())
                .forAllStatesExcept(state -> {
                    Direction.Axis axis = state.getValue(CarminiteEngineBlock.AXIS);
                    Direction.AxisDirection axisDirection = state.getValue(CarminiteEngineBlock.AXIS_POSITIVE) ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;
                    Direction dir = Direction.fromAxisAndDirection(axis, axisDirection);
                    return ConfiguredModel.builder()
                            .modelFile(modelFunc.apply(state))
                            .rotationX(dir == Direction.DOWN ? 180
                                    : dir.getAxis()
                                    .isHorizontal() ? 90 : 0)
                            .rotationY(dir.getAxis()
                                    .isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360)
                            .build();
                }, BlockStateProperties.WATERLOGGED);
    }
}
