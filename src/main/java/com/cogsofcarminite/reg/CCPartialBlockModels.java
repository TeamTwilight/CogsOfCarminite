package com.cogsofcarminite.reg;

import com.cogsofcarminite.CogsOfCarminite;
import com.jozufozu.flywheel.core.PartialModel;
import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CCPartialBlockModels {
    public static final PartialModel SHAFT_QUARTER = block("shaft_quarter");

    public static final PartialModel CLOCK_FLYWHEEL = block("flywheel/clock_flywheel");
    public static final PartialModel CLOCK_FLYWHEEL_OFF = block("flywheel/clock_flywheel_off");
    public static final PartialModel ENGINE_FLYWHEEL = block("flywheel/engine_flywheel");
    public static final PartialModel ENGINE_FLYWHEEL_OFF = block("flywheel/engine_flywheel_off");
    public static final PartialModel CORE_FLYWHEEL = block("flywheel/core_flywheel");
    public static final PartialModel CORE_FLYWHEEL_OFF = block("flywheel/core_flywheel_off");
    public static final PartialModel HEART_FLYWHEEL = block("flywheel/heart_flywheel");
    public static final PartialModel HEART_FLYWHEEL_OFF = block("flywheel/heart_flywheel_off");

    public static final PartialModel HORNBLOWER = block("hornblower/head");

    private static PartialModel block(String path) {
        return new PartialModel(CogsOfCarminite.prefix("block/" + path));
    }

    public static void init() {
        // init static fields
    }
}
