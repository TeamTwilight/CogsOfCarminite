package com.cogsofcarminite.reg;

import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.cogsofcarminite.CogsOfCarminite.TWILIGHT_REGISTRATE;
import static com.simibubi.create.AllTags.AllItemTags.PLATES;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CCItems {
    static {
        TWILIGHT_REGISTRATE.setCreativeTab(CCCreativeModeTabs.MAIN_TAB);
    }

    public static final ItemEntry<Item> IRONWOOD_SHEET = taggedIngredient("ironwood_sheet", PLATES.tag);
    public static final ItemEntry<Item> KNIGHTMETAL_SHEET = taggedIngredient("knightmetal_sheet", PLATES.tag);
    public static final ItemEntry<Item> FIERY_SHEET = taggedIngredient("fiery_sheet", PLATES.tag);

    public static void register() { }

    @SafeVarargs
    private static ItemEntry<Item> taggedIngredient(String name, TagKey<Item>... tags) {
        return TWILIGHT_REGISTRATE.item(name, Item::new)
                .tag(tags)
                .register();
    }
}
