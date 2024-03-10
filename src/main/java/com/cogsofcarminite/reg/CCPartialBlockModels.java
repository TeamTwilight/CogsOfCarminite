package com.cogsofcarminite.reg;

import com.cogsofcarminite.CogsOfCarminite;
import com.jozufozu.flywheel.core.PartialModel;
import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CCPartialBlockModels {
    public static final PartialModel SHAFT_QUARTER = block("shaft_quarter");

    public static final PartialModel CARMINITE_FLYWHEEL = block("flywheel/carminite_flywheel");
    public static final PartialModel INCOMPLETE_FLYWHEEL = block("flywheel/incomplete_flywheel");

    public static final PartialModel TIME_OFF = block("flywheel/timewood/off");
    public static final PartialModel TIME_OVERLAY = block("flywheel/timewood/overlay");

    public static final PartialModel SORTING_OFF = block("flywheel/sortingwood/off");
    public static final PartialModel SORTING_OVERLAY = block("flywheel/sortingwood/overlay");

    public static final PartialModel MINE_OFF = block("flywheel/minewood/off");
    public static final PartialModel MINE_OVERLAY = block("flywheel/minewood/overlay");

    public static final PartialModel TRANS_OFF = block("flywheel/transformation/off");
    public static final PartialModel TRANS_OVERLAY = block("flywheel/transformation/overlay");

    public static final PartialModel HORNBLOWER = block("hornblower/head");
    public static final PartialModel ROOT_PULLER_GEARS = block("mechanical_root_puller/gears");

    private static PartialModel block(String path) {
        return new PartialModel(CogsOfCarminite.prefix("block/" + path));
    }

    public static void init() {
        // init static fields
    }
}
