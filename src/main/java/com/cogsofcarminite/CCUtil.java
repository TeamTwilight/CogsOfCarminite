package com.cogsofcarminite;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CCUtil {
    public static boolean isPositive(Direction direction) {
        return direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE);
    }
}
