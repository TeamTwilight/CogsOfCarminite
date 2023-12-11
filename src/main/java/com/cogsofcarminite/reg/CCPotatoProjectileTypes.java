package com.cogsofcarminite.reg;

import com.cogsofcarminite.CogsOfCarminite;
import com.simibubi.create.content.equipment.potatoCannon.PotatoCannonProjectileType;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import twilightforest.block.CritterBlock;
import twilightforest.init.TFBlocks;
import twilightforest.init.TFItems;
import twilightforest.init.TFSounds;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Predicate;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CCPotatoProjectileTypes {
    public static final PotatoCannonProjectileType EXPERIMENT_115 = create("experiment_115").damage(2)
			.reloadTicks(15)
			.knockback(0.1f)
			.velocity(1.1f)
			.renderTumbling()
			.sticky()
			.soundPitch(1.0f)
			.registerAndAssign(TFItems.EXPERIMENT_115.get());

    public static final PotatoCannonProjectileType TORCHBERRIES = create("experiment_115").damage(2)
            .reloadTicks(10)
            .knockback(0.05f)
            .velocity(1.05f)
            .renderTumbling()
            .splitInto(3)
            .soundPitch(1.2f)
            .onEntityHit(potion())
            .registerAndAssign(TFItems.TORCHBERRIES.get());

    public static final PotatoCannonProjectileType MAGIC_BEANS = create("magic_beans").damage(3)
            .reloadTicks(20)
            .knockback(0.1f)
            .velocity(1.05f)
            .renderTumbling()
            .soundPitch(1.25f)
            .onBlockHit((level, hitResult) -> {
                if (level.isClientSide()) return true;
                BlockPos pos = hitResult.getBlockPos();
                if (level instanceof Level l && !l.isLoaded(pos)) return true;
                Direction face = hitResult.getDirection();
                if (face != Direction.UP) return false;

                int maxY = Math.max(pos.getY() + 100, 175);
                if (level instanceof Level lvl && pos.getY() < maxY && level.getBlockState(pos).is(TFBlocks.UBEROUS_SOIL.get()) && level.getBlockState(pos.above()).isAir()) {
                    if (!level.isClientSide()) {
                        lvl.setBlockAndUpdate(pos.above(), TFBlocks.BEANSTALK_GROWER.get().defaultBlockState());
                        level.playSound(null, pos, TFSounds.BEANSTALK_GROWTH.get(), SoundSource.BLOCKS, 4.0F, 1.0F);
                    }
                    return true;
                } else return false;
            })
            .registerAndAssign(TFItems.MAGIC_BEANS.get());

    public static final PotatoCannonProjectileType FIREFLY = create("firefly").damage(0)
            .reloadTicks(15)
            .knockback(0.1f)
            .velocity(1.15f)
            .renderTumbling()
            .soundPitch(1.1f)
            .onBlockHit((level, hitResult) -> {
                if (level.isClientSide()) return true;
                BlockPos pos = hitResult.getBlockPos();
                if (level instanceof Level l && !l.isLoaded(pos)) return true;
                Direction face = hitResult.getDirection();
                if (!level.getBlockState(pos.relative(face)).canBeReplaced()) return false;

                level.setBlock(pos.relative(face), TFBlocks.FIREFLY.get().defaultBlockState().setValue(CritterBlock.FACING, face), 3);
                return true;
            })
            .registerAndAssign(TFItems.FIREFLY.get());

    public static final PotatoCannonProjectileType CICADA = create("cicada").damage(0)
            .reloadTicks(15)
            .knockback(0.1f)
            .velocity(1.15f)
            .renderTumbling()
            .soundPitch(1.1f)
            .onBlockHit((level, hitResult) -> {
                if (level.isClientSide()) return true;
                BlockPos pos = hitResult.getBlockPos();
                if (level instanceof Level l && !l.isLoaded(pos)) return true;
                Direction face = hitResult.getDirection();
                if (!level.getBlockState(pos.relative(face)).canBeReplaced()) return false;

                level.setBlock(pos.relative(face), TFBlocks.CICADA.get().defaultBlockState().setValue(CritterBlock.FACING, face), 3);
                return true;
            })
            .registerAndAssign(TFItems.CICADA.get());

    public static final PotatoCannonProjectileType MOONWORM = create("moonworm").damage(0)
            .reloadTicks(15)
            .knockback(0.1f)
            .velocity(1.15f)
            .renderTumbling()
            .soundPitch(1.1f)
            .onBlockHit((level, hitResult) -> {
                if (level.isClientSide()) return true;
                BlockPos pos = hitResult.getBlockPos();
                if (level instanceof Level l && !l.isLoaded(pos)) return true;
                Direction face = hitResult.getDirection();
                if (!level.getBlockState(pos.relative(face)).canBeReplaced()) return false;

                level.setBlock(pos.relative(face), TFBlocks.MOONWORM.get().defaultBlockState().setValue(CritterBlock.FACING, face), 3);
                return true;
            })
            .registerAndAssign(TFItems.MOONWORM.get());

    private static PotatoCannonProjectileType.Builder create(String name) {
        return new PotatoCannonProjectileType.Builder(CogsOfCarminite.prefix(name));
    }

    private static Predicate<EntityHitResult> potion() {
        return ray -> {
            Entity entity = ray.getEntity();
            if (entity.level().isClientSide)
                return true;
            if (entity instanceof LivingEntity)
                applyEffect((LivingEntity) entity, new MobEffectInstance(MobEffects.GLOWING, 100, 0));
            return true;
        };
    }

    private static void applyEffect(LivingEntity entity, MobEffectInstance effect) {
        if (effect.getEffect()
                .isInstantenous())
            effect.getEffect()
                    .applyInstantenousEffect(null, null, entity, effect.getDuration(), 1.0);
        else
            entity.addEffect(effect);
    }

    public static void register() {}
}
