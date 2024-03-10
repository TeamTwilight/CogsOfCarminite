package com.cogsofcarminite.data;

import com.cogsofcarminite.CogsOfCarminite;
import com.cogsofcarminite.reg.CCItems;
import com.mojang.datafixers.util.Pair;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.LangBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CCLangGenerator {
    public static void generateLang() {
        // Creative Menus
        addLang("item_group", "main", "Cogs Of Carminite");

        // Hover Tooltips
        addLang("tooltip", "hornblower.header", "Hornblower Information");
        addLang("tooltip", "hornblower.contains", "Item: %1$s");
        addLang("tooltip", "hornblower.instrument", "%1$s");
        addLang("tooltip", "hornblower.empty", "Use a Horn Item to Insert");

        // Logistics
        addLang("logistics", "block_filter", "Block Filter");
        addLang("logistics", "ore_filter", "Ore Filter");
        addLang("logistics", "mechanical_heart_mode", "Heart Operation Mode");
        addSillyLang("logistics", "mechanical_heart_mode.transform", "Transform nearby biome");
        addSillyLang("logistics", "mechanical_heart_mode.adapt", "Adapt to current biome");
        addSillyLang("logistics", "mechanical_heart_mode.revert", "Revert nearby biome");

        // Block Attributes
        addLang("block_attributes", "replaceable", "is replaceable");
        addLang("block_attributes", "replaceable.inverted", "is not replaceable");
        addLang("block_attributes", "block_entity", "has a Block Entity");
        addLang("block_attributes", "block_entity.inverted", "does not have a Block Entity");
        addLang("block_attributes", "analog_output_signal", "has an analog output signal");
        addLang("block_attributes", "analog_output_signal.inverted", "does not have an analog output signal");
        addLang("block_attributes", "ignited_by_lava", "is ignited by Lava");
        addLang("block_attributes", "ignited_by_lava.inverted", "is not ignited by Lava");
        addLang("block_attributes", "occlude", "can occlude");
        addLang("block_attributes", "occlude.inverted", "can not occlude");
        addLang("block_attributes", "large_collision_shape", "has a large collision shape");
        addLang("block_attributes", "large_collision_shape.inverted", "does not have a large collision shape");
        addLang("block_attributes", "offset_function", "has an offset function");
        addLang("block_attributes", "offset_function.inverted", "does not have an offset function");
        addLang("block_attributes", "randomly_ticking", "does randomly tick");
        addLang("block_attributes", "randomly_ticking.inverted", "does not randomly tick");
        addLang("block_attributes", "signal_source", "is a signal source");
        addLang("block_attributes", "signal_source.inverted", "is not a signal source");
        addLang("block_attributes", "requires_correct_tool_for_drops", "requires correct tool for drops");
        addLang("block_attributes", "requires_correct_tool_for_drops.inverted", "does not require correct tool for drops");
        addLang("block_attributes", "sticky_block", "is block sticky");
        addLang("block_attributes", "sticky_block.inverted", "is block not sticky");
        addLang("block_attributes", "waterlogged", "is waterlogged");
        addLang("block_attributes", "waterlogged.inverted", "is not waterlogged");
        addLang("block_attributes", "infiniburn", "has infiniburn");
        addLang("block_attributes", "infiniburn.inverted", "does not have infiniburn");
        addLang("block_attributes", "burning", "is burning");
        addLang("block_attributes", "burning.inverted", "is not burning");
        addLang("block_attributes", "propagates_skylight_down", "propagates skylight down");
        addLang("block_attributes", "propagates_skylight_down.inverted", "does not propagate skylight down");
        addLang("block_attributes", "rained_upon", "is being rained upon");
        addLang("block_attributes", "rained_upon.inverted", "is not being rained upon");
        addLang("block_attributes", "see_sky", "can see the sky");
        addLang("block_attributes", "see_sky.inverted", "can not see the sky");

        addShiftTip(CCItems.BLOCK_ATTRIBUTE_FILTER.getId(),
                Pair.of("summary", "_Matches blocks_ against a set of _attributes_ or _categories_. Can be used in _Block Filter Slots_ and the _Smart Observer_"),
                Pair.of("condition1", "When R-Clicked"),
                Pair.of("behaviour1", "Opens the _configuration interface_.")
        );
    }

    @SafeVarargs
    public static void addShiftTip(ResourceLocation item, Pair<String, String>... translations) {
        for (Pair<String, String> pair : translations) {
            CogsOfCarminite.TWILIGHT_REGISTRATE.addLang("item." + item.getNamespace(), new ResourceLocation(item.getPath(), "tooltip." + pair.getFirst()), pair.getSecond());
        }
    }

    public static void addLang(String type, String key, String translation) {
        CogsOfCarminite.TWILIGHT_REGISTRATE.addLang(CogsOfCarminite.MODID, new ResourceLocation(type, key), translation);
    }

    public static void addSillyLang(String type, String key, String translation) {
        CogsOfCarminite.TWILIGHT_REGISTRATE.addLang(Create.ID + "." + CogsOfCarminite.MODID, new ResourceLocation(type, key), translation);
    }

    public static LangBuilder translate(String langKey, Object... args) {
        return Lang.builder(CogsOfCarminite.MODID).translate(langKey, args);
    }
}
