package com.cogsofcarminite;

import com.mojang.datafixers.util.Pair;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import twilightforest.item.OreMagnetItem;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CCUtil {
    public static Map<Pair<Class<?>, String>, Field> REFLECTED_CACHE = new HashMap<>();

    public static Pair<Class<?>, String> ORE_TO_BLOCK_REPLACEMENTS = Pair.of(OreMagnetItem.class, "ORE_TO_BLOCK_REPLACEMENTS");
    public static Pair<Class<?>, String> FILTER_FIELD = Pair.of(FilteringBehaviour.class, "filter");
    public static Pair<Class<?>, String> CALLBACK = Pair.of(FilteringBehaviour.class, "callback");
    public static Pair<Class<?>, String> FILTER_IN_BE = Pair.of(MovementContext.class, "filter");

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T reflectAndGet(Pair<Class<?>, String> target, @Nullable Object self) {
        if (!REFLECTED_CACHE.containsKey(target)) reflect(target);
        try {
            return (T) REFLECTED_CACHE.get(target).get(self);
        } catch (IllegalAccessException | ClassCastException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void reflectAndSet(Pair<Class<?>, String> target, @Nullable Object self, T newObject) {
        if (!REFLECTED_CACHE.containsKey(target)) reflect(target);
        try {
            REFLECTED_CACHE.get(target).set(self, newObject);
        } catch (IllegalAccessException | ClassCastException e) {
            throw new RuntimeException(e);
        }
    }

    private static void reflect(Pair<Class<?>, String> target) {
        try {
            Field field = target.getFirst().getDeclaredField(target.getSecond());
            field.setAccessible(true);
            REFLECTED_CACHE.put(target, field);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isPositive(Direction direction) {
        return direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE);
    }
}
