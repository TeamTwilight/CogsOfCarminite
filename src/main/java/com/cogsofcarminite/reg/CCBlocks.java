package com.cogsofcarminite.reg;

import com.cogsofcarminite.behaviour.CarminiteLogMovementBehaviour;
import com.cogsofcarminite.behaviour.HornblowerMovementBehaviour;
import com.cogsofcarminite.behaviour.MechanicalRootPullerMovementBehaviour;
import com.cogsofcarminite.blocks.*;
import com.cogsofcarminite.client.CCSpriteShifts;
import com.cogsofcarminite.items.CarminiteMagicLogBlockItem;
import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.AllMovementBehaviours;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.decoration.encasing.CasingBlock;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.BlockStateGen;
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
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;

@SuppressWarnings("removal")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CCBlocks {

    public static final BlockEntry<CasingBlock> DARK_TOWER_CASING =
            TWILIGHT_REGISTRATE.block("dark_tower_casing", CasingBlock::new)
                    .transform(BuilderTransformers.casing(() -> CCSpriteShifts.DARK_TOWER_CASING))
                    .properties(p -> p.mapColor(MapColor.PODZOL))
                    .register();


    public static final BlockEntry<MechanicalTimewoodClock> MECHANICAL_TIMEWOOD_CLOCK =
            TWILIGHT_REGISTRATE.block("mechanical_timewood_clock", MechanicalTimewoodClock::new)
                    .initialProperties(SharedProperties::copperMetal)
                    .properties(p -> p.mapColor(MapColor.STONE)
                            .noOcclusion())
                    .transform(axeOrPickaxe())
                    .blockstate(directionalBlockProviderIgnoresWaterlogged())
                    .addLayer(() -> RenderType::cutoutMipped)
                    .transform(BlockStressDefaults.setImpact(4.0))
                    .onRegister(AllMovementBehaviours.movementBehaviour(new CarminiteLogMovementBehaviour()))
                    .item((mechanicalTimewoodClock, properties) -> new CarminiteMagicLogBlockItem(mechanicalTimewoodClock, properties) {
                        @Override
                        public PartialModel getPartialModel() {
                            return CCPartialBlockModels.CLOCK_FLYWHEEL_OFF;
                        }
                    })
                    .tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
                    .transform(customItemModel())
                    .register();
    
    public static final BlockEntry<MechanicalHeartOfTransformation> MECHANICAL_HEART_OF_TRANSFORMATION =
            TWILIGHT_REGISTRATE.block("mechanical_heart_of_transformation", MechanicalHeartOfTransformation::new)
                    .initialProperties(SharedProperties::copperMetal)
                    .properties(p -> p.mapColor(MapColor.STONE)
                            .noOcclusion())
                    .transform(axeOrPickaxe())
                    .blockstate(directionalBlockProviderIgnoresWaterlogged())
                    .addLayer(() -> RenderType::cutoutMipped)
                    .transform(BlockStressDefaults.setImpact(4.0))
                    .onRegister(AllMovementBehaviours.movementBehaviour(new CarminiteLogMovementBehaviour()))
                    .item((mechanicalHeartOfTransformation, properties) -> new CarminiteMagicLogBlockItem(mechanicalHeartOfTransformation, properties) {
                        @Override
                        public PartialModel getPartialModel() {
                            return CCPartialBlockModels.HEART_FLYWHEEL_OFF;
                        }
                    })
                    .tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
                    .transform(customItemModel())
                    .register();


    public static final BlockEntry<MechanicalMinewoodCoreBlock> MECHANICAL_MINEWOOD_CORE =
            TWILIGHT_REGISTRATE.block("mechanical_minewood_core", MechanicalMinewoodCoreBlock::new)
                    .initialProperties(SharedProperties::copperMetal)
                    .properties(p -> p.mapColor(MapColor.STONE)
                            .noOcclusion())
                    .transform(axeOrPickaxe())
                    .blockstate(directionalBlockProviderIgnoresWaterlogged())
                    .addLayer(() -> RenderType::cutoutMipped)
                    .transform(BlockStressDefaults.setImpact(4.0))
                    .onRegister(AllMovementBehaviours.movementBehaviour(new CarminiteLogMovementBehaviour()))
                    .item((mechanicalMinewoodCoreBlock, properties) -> new CarminiteMagicLogBlockItem(mechanicalMinewoodCoreBlock, properties) {
                        @Override
                        public PartialModel getPartialModel() {
                            return CCPartialBlockModels.CORE_FLYWHEEL_OFF;
                        }
                    })
                    .tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
                    .transform(customItemModel())
                    .register();
    
    public static final BlockEntry<MechanicalSortingwoodEngine> MECHANICAL_SORTINGWOOD_ENGINE =
            TWILIGHT_REGISTRATE.block("mechanical_sortingwood_engine", MechanicalSortingwoodEngine::new)
                    .initialProperties(SharedProperties::copperMetal)
                    .properties(p -> p.mapColor(MapColor.STONE)
                            .noOcclusion())
                    .transform(axeOrPickaxe())
                    .blockstate(directionalBlockProviderIgnoresWaterlogged())
                    .addLayer(() -> RenderType::cutoutMipped)
                    .transform(BlockStressDefaults.setImpact(4.0))
                    .onRegister(AllMovementBehaviours.movementBehaviour(new CarminiteLogMovementBehaviour()))
                    .item((mechanicalSortingwoodEngine, properties) -> new CarminiteMagicLogBlockItem(mechanicalSortingwoodEngine, properties) {
                        @Override
                        public PartialModel getPartialModel() {
                            return CCPartialBlockModels.ENGINE_FLYWHEEL_OFF;
                        }
                    })
                    .tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
                    .transform(customItemModel())
                    .register();

    public static final BlockEntry<HornblowerBlock> HORNBLOWER =
            TWILIGHT_REGISTRATE.block("hornblower", HornblowerBlock::new)
                    .initialProperties(SharedProperties::copperMetal)
                    .properties(p -> p.mapColor(MapColor.PODZOL).noOcclusion())
                    .transform(axeOrPickaxe())
                    .blockstate(BlockStateGen.horizontalBlockProvider(true))
                    .addLayer(() -> RenderType::cutoutMipped)
                    .transform(BlockStressDefaults.setImpact(1.0))
                    .onRegister(AllMovementBehaviours.movementBehaviour(new HornblowerMovementBehaviour()))
                    .item()
                    .tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
                    .transform(customItemModel())
                    .register();

    public static final BlockEntry<MechanicalRootPullerBlock> MECHANICAL_ROOT_PULLER =
            TWILIGHT_REGISTRATE.block("mechanical_root_puller", MechanicalRootPullerBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.mapColor(MapColor.PODZOL).noOcclusion())
                    .transform(axeOrPickaxe())
                    .blockstate(BlockStateGen.horizontalBlockProvider(true))
                    .addLayer(() -> RenderType::cutoutMipped)
                    .transform(BlockStressDefaults.setImpact(4.0))
                    .onRegister(AllMovementBehaviours.movementBehaviour(new MechanicalRootPullerMovementBehaviour()))
                    .item()
                    .transform(customItemModel())
                    .register();

    public static void register() {}

    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> directionalBlockProviderIgnoresWaterlogged() {
        return (c, p) -> directionalBlockIgnoresWaterlogged(c, p, $ -> AssetLookup.partialBaseModel(c, p));
    }

    public static <T extends Block> void directionalBlockIgnoresWaterlogged(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, Function<BlockState, ModelFile> modelFunc) {
        prov.getVariantBuilder(ctx.getEntry())
                .forAllStatesExcept(state -> {
                    Direction.Axis axis = state.getValue(MechanicalSortingwoodEngine.AXIS);
                    Direction.AxisDirection axisDirection = state.getValue(MechanicalSortingwoodEngine.AXIS_POSITIVE) ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;
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
