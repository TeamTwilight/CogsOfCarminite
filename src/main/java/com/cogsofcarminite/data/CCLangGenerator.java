package com.cogsofcarminite.data;

import com.cogsofcarminite.CogsOfCarminite;
import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CCLangGenerator {
    public static void generateLang() {
        CogsOfCarminite.TWILIGHT_REGISTRATE.addLang("itemGroup", CogsOfCarminite.prefix("main"), "Cogs Of Carminite");
    }
}
