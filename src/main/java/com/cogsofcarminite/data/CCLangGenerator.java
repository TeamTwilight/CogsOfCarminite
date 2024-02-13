package com.cogsofcarminite.data;

import com.cogsofcarminite.CogsOfCarminite;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.LangBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CCLangGenerator {
    public static void generateLang() {
        addLang("item_group", "main", "Cogs Of Carminite");
        addLang("tooltip", "hornblower.header", "Hornblower Information");
        addLang("tooltip", "hornblower.contains", "Item: %1$s");
        addLang("tooltip", "hornblower.instrument", "%1$s");
        addLang("tooltip", "hornblower.empty", "Use a Horn Item to Insert");
    }

    public static void addLang(String type, String key, String translation) {
        CogsOfCarminite.TWILIGHT_REGISTRATE.addLang(CogsOfCarminite.MODID, new ResourceLocation(type, key), translation);
    }

    public static LangBuilder translate(String langKey, Object... args) {
        return Lang.builder(CogsOfCarminite.MODID).translate(langKey, args);
    }
}
